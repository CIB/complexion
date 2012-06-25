package complexion.client;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 * The renderer is strictly the low-level mechanism responsible for drawing
 * objects, text, widgets and so on. It should be created and managed by a
 * higher-level Client manager.
 */
public class Renderer {
	/**
	 * Initialize the LWJGL/OpenGL context.
	 * @throws LWJGLException
	 */
	public Renderer() throws LWJGLException 
	{
		// Create the client window
		Display.setDisplayMode(new DisplayMode(800,600));
		Display.create();
		
		// Initialize OpenGL
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 800, 0, 600, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable( GL11.GL_BLEND );
		GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
	}
	
	/**
	 * Function responsible for drawing the current frame.
	 * This will draw all objects currently assigned to the renderer.
	 */
	public void draw()	
	{
	    // Clear the screen and depth buffer
	    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);	
		
	    // Iterate over the atoms and draw them one by one
	    // TODO: sort them by layer
	    for(Atom a : atoms)
	    {
	    	Texture t = this.textures.get(a.getCurrentImage());
	    	if(t == null)
	    	{
	    		// texture doesn't exist yet, need to generate it	    		
	    		try {
	    			// yeah, this is horrible
	    			byte[] buffer = ((DataBufferByte)(a.getCurrentImage()).getRaster().getDataBuffer()).getData();
	    			t = TextureLoader.getTexture("PNG", new ByteArrayInputStream(buffer));
	    		}
	    		catch(IOException e)
	    		{
	    			// This shouldn't happen at all, so dump the stack and return
	    			Thread.dumpStack();
	    			return;
	    		}
	    	}
	    	
	    	t.bind();

			GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0,0);
			GL11.glVertex2f(a.x,a.y);
			GL11.glTexCoord2f(1,0);
			GL11.glVertex2f(a.x+t.getTextureWidth(),a.y);
			GL11.glTexCoord2f(1,1);
			GL11.glVertex2f(a.x+t.getTextureWidth(),a.y+t.getTextureHeight());
			GL11.glTexCoord2f(0,1);
			GL11.glVertex2f(a.x,a.y+t.getTextureHeight());
			GL11.glEnd();
	    }
 
	    Display.update();
	}
	
	// All the atoms we're currently rendering.
	private List<Atom> atoms = new ArrayList<Atom>();
	private Map<BufferedImage,Texture> textures = new HashMap<BufferedImage,Texture>();
}
