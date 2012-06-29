package complexion.resource;

import static complexion.common.Directions.EAST;
import static complexion.common.Directions.NORTH;
import static complexion.common.Directions.NORTHEAST;
import static complexion.common.Directions.NORTHWEST;
import static complexion.common.Directions.SOUTH;
import static complexion.common.Directions.SOUTHEAST;
import static complexion.common.Directions.SOUTHWEST;
import static complexion.common.Directions.WEST;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sixlegs.png.PngImage;
import com.sixlegs.png.TextChunk;

public class DMIParser {
	
	/**
	 * Function for parsing .dmi metadata and building this.states
	 * from it.
	 * 
	 * @throws IOException
	 */
	void parse(InputStream source) throws IOException
	{	
		// Load the PNG into memory and decode it
		PngImage load = new PngImage();
		
		// Extract the image data itself from the PNG
		buffer = load.read(source, false); // do not close the input stream
		
		// Start with the tiling at the top left
		pixel_x = 0; pixel_y = 0;
		
		// Extract the text chunk containing the .dmi metadata
		TextChunk chunk = load.getTextChunk("Description");
		if(chunk == null) throw new IOException("File has no metadata");
		String metadata = chunk.getText();
		
		
		// Parse the metadata
		List<SpriteInfoBlock> nodes = parseMetadata(metadata);
		
		// Interpret the syntax tree and build this.states
		// -----------------------------------------------
		
		// information about a single state
		String state_name = "";
		
		// For each frame, there should be a separate delay.
		List<Integer> state_delay = new ArrayList<Integer>(); 
        
		int state_frames = 0;     // Number of frames in this state
		int state_directions = 0; // Number of directions in this state
		
		boolean had_state = false; // Used to check whether a state was already created
		
		this.states = new HashMap<String,SpriteState>();

		// Go over the syntax tree node by node and extract info
		// =====================================================
		Iterator<SpriteInfoBlock> it = nodes.iterator();
		while(it.hasNext())
		{
			SpriteInfoBlock i = it.next();
			
		    // If there's data on the line, collect it
		    // ---------------------------------------
            if(i.key.equals("dirs"))
            {
            	// get the number of directions
                state_directions = Integer.parseInt(i.value); 
            }
            else if(i.key.equals("frames"))
            {
            	// number of frames per direction
                state_frames = Integer.parseInt(i.value);
            }
            else if(i.key.equals("delay"))
            {
            	// Delay between animation frames, this is a list
            	// in the form of "2,2,2,4", one for each frame
            	for(String delay : i.value.split(","))
            	{
            		state_delay.add(Integer.parseInt(delay));
            	}
            }
			else if(i.key.equals("version")){
				if(!i.value.equals("4.0")){
					throw new IOException(".DMI version "+i.value+" incompatible");
				}
			}
			else if(i.key.equals("width")){
				width  = Integer.parseInt(i.value);
			}
			else if(i.key.equals("height")){
				height = Integer.parseInt(i.value);
			}


            // See if we're done with this specific icon state.
            // We're done if another icon state follows, or if we're at the end of the file
            // ----------------------------------------------------------------------------
            if(had_state) if(i.key.equals("state") || !it.hasNext())
            {
            	// Make sure this icon state is properly defined
            	if(state_frames == 0 || state_directions == 0)
            	{
            		throw new IOException(".DMI metadata malformed");
            	}
            	
            	// If the state delays haven't been defined, go for a default
            	if(state_delay.size() == 0)
            	{
            		while(state_delay.size() < state_frames)
            		{
            			state_delay.add(1);
            		}
            	}
            	
            	if(state_delay.size() != state_frames)
            	{
            		throw new IOException(".DMI metadata malformed");
            	}
            	
            	SpriteState state = generateSpriteState(state_frames, state_directions, state_delay);
            	
            	// intern state_name to make string comparison more efficient
            	state_name = state_name.intern();
                states.put(state_name, state); // add state to the sprite

                state_frames = 0;
                state_directions = 0;
                state_delay.clear();
            }

            // A "state" line opens a new state
			if(i.key.equals("state"))
			{
                state_name = i.value.substring(1,i.value.length() - 1);
                had_state = true;
			}
		}
	}
	
	/**
	 * Internal function for parsing the next N frames into a sprite state.
	 * This will tile the buffer into sub-images and assign the sub-images
	 * to state/frame/direction combinations.
	 * 
	 * Modifies pixel_x/pixel_y values to "advance" the current tile.
	 */
	private SpriteState generateSpriteState(int frames, int directions, List<Integer> delays) throws IOException
	{
		SpriteState state = new SpriteState();
		state.frames = new ArrayList<SpriteFrame>();
		
    	// Extract the width of the full image(not the tiles)
        int base_width = buffer.getWidth();

        for(int framei = 0; framei < frames; framei++)
        {
            // Prepare a new frame to insert into our state
            SpriteFrame frame = new SpriteFrame();
            frame.directions = new HashMap<Integer,BufferedImage>();
            
            // This array simply maps tile indices to directions.
            // For example, the first tile in the frame would be the direction SOUTH.
            int dirs[] = {SOUTH, NORTH, EAST, WEST, SOUTHEAST, SOUTHWEST, NORTHEAST, NORTHWEST};

            if(directions != 1 && directions != 4 && directions != 8) {
            	throw new IOException(".DMI metadata malformed");
            }
            
            // Go over all directions and insert them into our frame
            for(int i=0; i < directions; i++) {
            	// Find out which direction the next tile will be
                int next_dir = dirs[i];
                
            	// If we're at the end of the buffer on the right, go to the next line
                if(pixel_x >= base_width)
                {
                    pixel_x = 0;
                    pixel_y = pixel_y + height;
                }
                
                BufferedImage image = buffer.getSubimage(pixel_x, pixel_y, width, height);
                frame.directions.put(next_dir, image);
                pixel_x += width; // push to the next "row"

            }
            
            // Set the proper delay of the frame
            frame.delay = delays.get(framei);
            
            // Add the frame to our state
            state.frames.add(frame);
        }
        
        return state;
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
			
			// Extract key, value and indentation
			String indentation = matcher.group(1);
			String key = matcher.group(2);
			String value = matcher.group(3);
			
			// Build a new entry in our parsetree for it
			SpriteInfoBlock new_block = new SpriteInfoBlock();
			new_block.indent = (indentation.length() != 0);
			new_block.key    = key;
			new_block.value  = value;
			
			nodes.add(new_block);
		}

		return nodes;
	}
	
	// Variables to store the parsing results in
	Map<String,SpriteState> states;
	int width, height = 0;
	
	private BufferedImage buffer; // Image we're currently taking apart
	private int pixel_x, pixel_y; // Current position on the image(0,0 is top-left corner)
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