package complexion.client;

import java.awt.image.BufferedImage;

import complexion.resource.Sprite;

/**
 * Client-side representation of a game Atom, that is,
 * an object displayed on the map.
 * 
 * Only provides the data required for rendering objects,
 * as well as interacting with them(such as using verbs).
 *
 */
public class Atom {
	// Unique ID of the object, generated by the server.
	public int UID; 
	
	// Position on the map
	public int x,y;
	
	// Sprite the object is currently associated with.
	public Sprite sprite;  

	// Determines whether the object will be rendered above or
	// below other objects
	public int layer;
	
	// Each sprite has multiple states, which are more or less
	// sprites of their own. sprite_state determines which state
	// is used.
	public String sprite_state;
	
	// The current animation frame the sprite is in.
	public int frame;
	
	// A sprite can define different appearances depending on the
	// direction. This variable should be one of the constants defined
	// in complexion.Direction
	public int direction;
	
	// If this is set to false, the atom will always have the same position
	// on the viewport, rather than being tied to a map position.
	boolean onMap = true;
	
	/**
	 * @return The image currently representing the atom, given state, frame and direction.
	 */
	public BufferedImage getCurrentImage()
	{
		return sprite.getImage(sprite_state, frame, direction);
	}
}
