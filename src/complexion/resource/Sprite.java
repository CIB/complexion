package complexion.resource;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.zip.CRC32;

import complexion.common.Directions;

/**
 * A sprite consists of several different images(icon states) indexed by string, 
 * which are in turn split into several animation frames indexed by integer, which
 * are in turn split into several directions indexed by integer.
 * 
 * By supplying an icon state, an animation frame, and a direction, a BufferedImage 
 * can be extracted, which can be used to draw the object.
 * 
 * @author cib
 *
 */
public class Sprite extends Resource {
	/**
	 * Load a .dmi file(a tiled PNG with metadata) into a Sprite object.
	 * @param filename The filepath as string of the .dmi
	 * @throws IOException
	 */
	public Sprite(String filename) throws IOException 
	{
		// Remember the filename
		this.filename = filename;
		// Generate a CRC from the file on disk
		this.hashID = getCRC32(filename);
		
		parseDMI(new FileInputStream(filename));
	}
	
	/**
	 * Load an input stream into a sprite object. Useful if the .dmi is part
	 * of a .rsc file, rather than being in its own file.
	 * @param stream An InputStream object containing the .dmi
	 * @throws IOException
	 */
	public Sprite(String filename, InputStream stream) throws IOException
	{
		// Remember the filename
		this.filename = filename;
		
		parseDMI(stream);
	}
	
	/**
	 * Helper function for initializing a DMIParser and using the PNG metadata
	 * to populate the sprite states.
	 */
	private void parseDMI(InputStream stream) throws IOException
	{
		DMIParser parser = new DMIParser();
		parser.parse(stream);
		
		// extract the results
		this.states = parser.states;
		this.width  = parser.width;
		this.height = parser.height;
	}
	
	/**
	 * Get the width of the sprite in pixels. This includes transparent/invisible areas.
	 * Width is a constant value throughout instance lifetime.
	 */
	public int getWidth()
	{
		return width;
	}
	
	/**
	 * Get the height of the sprite in pixels. This includes transparent/invisible areas.
	 * Height is a constant value throughout instance lifetime.
	 */
	public int getHeight()
	{
		return height;
	}
	
	/**
	 * Get the BufferedImage associated with a specific state/frame/dir combination.
	 * @param state A string representing the sprite state.
	 * @param frame The index in the sprite's animation.
	 * @param dir   The direction in which the sprite is facing.
	 * @return The image, or null if not defined.
	 */
	public BufferedImage getImage(String state, int frame, int dir)
	{
		// Try to get the state
		SpriteState st = states.get(state);
		if(st == null) return null;
		
		// Try to get the frame
		SpriteFrame fr;
		try
		{
			fr = st.frames.get(frame);
		} 
		catch(IndexOutOfBoundsException e)
		{
			// If the frame doesn't exist, try frame 0, that one must
			// always exist
			fr = st.frames.get(0);
		}
		
		BufferedImage b = fr.directions.get(dir);
		if(b == null)
		{
			// If the direction doesn't exist, try direction SOUTH,
			// that one must always exist
			b = fr.directions.get(Directions.SOUTH);
		}
		
		return b;
	}
	
	private Map<String,SpriteState> states;
	private int width  = 0;
	private int height = 0;
}

/**
 * Private class used to hold a sprite state/icon state of a Sprite.
 * This in turn may hold multiple animation frames, which in turn
 * may hold multiple directions.
 */
class SpriteState
{
	List<SpriteFrame> frames;
}

/**
 * Private class used to hold an animation frame of a SpriteState.
 * This in turn may hold multiple directions.
 */
class SpriteFrame
{
	Map<Integer,BufferedImage> directions;
	int delay; // How many 1/10th of a second will pass until the next frame
}