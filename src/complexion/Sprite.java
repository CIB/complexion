package complexion;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
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
	
	private void parseDMI(InputStream source) throws IOException
	{
		// Load the PNG into memory and decode it
		PngImage load = new PngImage();
		load.read(source, false); // do not close the input stream
		
		// Extract the text chunk containing the .dmi metadata
		TextChunk chunk = load.getTextChunk("Description");
		if(chunk == null) throw new IOException("File has no metadata");
		String metadata = chunk.getText();
		
		// TODO: Parse the metadata and populate this.states
	}

	private Map<String,List<BufferedImage>> states;
	private String metadata;
}