package complexion;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.regex.*;

import com.sixlegs.png.PngImage;
import com.sixlegs.png.TextChunk;

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
public class Sprite {
	/**
	 * Load a .dmi file(a tiled PNG with metadata) into a Sprite object.
	 * @param filename The filepath as string of the .dmi
	 * @throws IOException
	 */
	public Sprite(String filename) throws IOException 
	{
		parseDMI(new FileInputStream(filename));
	}
	
	/**
	 * Load an input stream into a sprite object. Useful if the .dmi is part
	 * of a .rsc file, rather than being in its own file.
	 * @param stream An InputStream object containing the .dmi
	 * @throws IOException
	 */
	public Sprite(InputStream stream) throws IOException
	{
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

	private Map<String,SpriteState> states;
	private String metadata;
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

/**
 * Private class used to temporarily store the key-value pairs in
 * that have been stored in the PNG metadata.
 */
class SpriteInfoBlock
{
	boolean indent = false; // whether this key-value pair is indented
	String  key    = null;  // the key(left-hand side) of the pair
	String  value  = null;  // the value(right-hand side) of the pair
}