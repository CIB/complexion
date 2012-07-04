package complexion.client;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import complexion.common.Config;
/**
 * The renderer is strictly the low-level mechanism responsible for drawing
 * objects, text, widgets and so on. It should be created and managed by a
 * higher-level Client manager.
 */
public class Renderer {
	/**
	 * Initialize the LWJGL/OpenGL context.
	 * 
	 * @throws LWJGLException
	 */
	public Renderer() throws LWJGLException {
		// Create the client window
		Display.setDisplayMode(new DisplayMode(800, 600));
		Display.create();

		// Initialize OpenGL
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 800, 0, 600, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	/**
	 * Function responsible for drawing the current frame. This will draw all
	 * objects currently assigned to the renderer.
	 */
	public void draw() {
		// Clear the screen and depth buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		// Iterate over the atoms and draw them one by one
		// TODO: sort them by layer
		for (Atom a : atoms) {
			// Try to get a texture for our atom from its sprite
			BufferedImage buf = a.getCurrentImage();
			
			// Check if the image exists
			if(buf == null) continue;

			// Do we have the texture cached?
			if (!this.textures.containsKey(buf)) {
				// Texture isn't cached yet, need to generate it
				textures.put(buf, TextureLoader.loadTexture(buf));
			}
			
			// Load cached texture
			int t = this.textures.get(a.getCurrentImage());

			// Bind cached texture
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, t);

			// Get the sprite dimensions
			int width = buf.getWidth();
			int height = buf.getHeight();
			
			// Check where to render the sprite on the buffer
			int offset_x = a.tile_x * Config.tileWidth + a.pixel_x;
			int offset_y = a.tile_y * Config.tileWidth + a.pixel_y;
			
			// Objects with onMap set are displayed relative to the
			// current viewport position(scrolling)
			if(a.onMap)
			{
				offset_x -= this.eye_x;
				offset_y -= this.eye_y;
			}

			// Draw the sprite as quad
			// TODO: Check if drawing two triangles is faster
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(offset_x, offset_y);
			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2f(offset_x + width, offset_y);
			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2f(offset_x + width, offset_y + height);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2f(offset_x, offset_y + height);
			GL11.glEnd();
		}

		Display.update();
		
		// TODO: uncache unused textures if the cache is too crowded
	}
	
	/**
	 * Add a visible atom to the Renderer for rendering.
	 */
	public void addAtom(Atom a)
	{
		atoms.add(a);
		Collections.sort(atoms, new Renderer.LayerComparator());
	}
	
	/**
	 * Sets the current position of the client viewport on the map.
	 * This will affect how Atoms with onMap = true are displaced
	 * on the rendering window.
	 */
	public void setEyePos(int pos_x, int pos_y)
	{
		this.eye_x = pos_x;
		this.eye_y = pos_y;
	}
	
	/**
	 * Destroy the renderer and rendering window.
	 */
	public void destroy()
	{
		Display.destroy();
	}
	
	/** 
	 * Private class used for sorting collections by layer.
	 */
	private static class LayerComparator implements Comparator<Atom>
	{
		public int compare(Atom a1, Atom a2) {
			// TODO: something like return Integer.compare(a1.layer,a2.layer)
			//       would be more efficient
			if     (a1.layer < a2.layer) return -1;
			else if(a2.layer < a1.layer) return 1;
			else                         return 0;
		}
	}
	

	// All the atoms we're currently rendering.
	List<Atom> atoms = new ArrayList<Atom>();
	private Map<BufferedImage, Integer> textures = new HashMap<BufferedImage, Integer>();
	
	// Current position of the viewport on the map
	private int eye_x, eye_y;
}
