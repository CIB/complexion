package complexion;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.regex.*;

import com.sixlegs.png.PngImage;
import com.sixlegs.png.TextChunk;

/**
 * A sprite consists of several different images(icon states) indexed by string, 
 * which are in turn split into several animation frames indexed by integer.
 * 
 * By supplying an icon state and an animation frame, a BufferedImage can be extracted,
 * which can be used to draw the object.
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
	 * Internal function for parsing .dmi metadata and building this.states
	 * from it.
	 * 
	 * @throws IOException
	 */
	private void parseDMI(InputStream source) throws IOException
	{
		// Load the PNG into memory and decode it
		PngImage load = new PngImage();
		load.read(source, false); // do not close the input stream
		
		// Extract the text chunk containing the .dmi metadata
		TextChunk chunk = load.getTextChunk("Description");
		if(chunk == null) throw new IOException("File has no metadata");
		String metadata = chunk.getText();
		
		// Parse the metadata
		List<SpriteInfoBlock> nodes = parseMetadata(metadata);
		
		for(SpriteInfoBlock s : nodes)
		{
			System.out.println(s.key + "=" + s.value);
		}
	}
	
	/**
	 * Internal function for parsing plaintext metadata and building a kind of
	 * abstract syntax tree from it.
	 */
	private List<SpriteInfoBlock> parseMetadata(String data) throws IOException
	{
		// Prepare a "syntax tree" to return
		List<SpriteInfoBlock> nodes = new ArrayList<SpriteInfoBlock>();
		
		// Split the metadata into individual lines and parse them individually
		String[] lines = data.split("\n");
		
		for(String line : lines) {
			// If the line is all spaces, or begins with #, ignore it
			if(line.matches("\\s*") || line.matches("#.*"))
			{
				continue;
			}
			
			// Create a regular expression to parse lines of the type:
			// key = value
			Pattern pattern = Pattern.compile("(\\s*)([^\\s]+)\\s*=\\s*(.*[^\\s])\\s*");
			Matcher matcher = pattern.matcher(line);
			
			// Try to match our line against the key = value pattern
			boolean matchFound = matcher.matches();
			if(!matchFound) throw new IOException("Malformed .DMI metadata");
			
			String indentation = matcher.group(1);
			String key = matcher.group(2);
			String value = matcher.group(3);
			
			SpriteInfoBlock new_block = new SpriteInfoBlock();
			new_block.indent = (indentation.length() != 0);
			new_block.key    = key;
			new_block.value  = value;
			
			nodes.add(new_block);
		}

		return nodes;
	}

	private Map<String,List<BufferedImage>> states;
	private String metadata;
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