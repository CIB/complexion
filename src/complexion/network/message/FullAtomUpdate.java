package complexion.network.message;

/** Resend all the data about the atom.
 * 
 * This will be the standard update method for sending new atoms.
 */
public class FullAtomUpdate extends AtomUpdate {
	/// A resource filename identifying the sprite
	public String sprite;
	
	/// The sprite state of the transmitted atom
	public String sprite_state;
	
	/// The layer of the transmitted atom
	public int layer;
	
	/// The direction of the transmitted atom, a byte suffices to store all possible directions
	public byte direction;
	
	/// The coordinates of the object on the map.
	public int pos_x, pos_y, pos_z;
}
