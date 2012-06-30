package complexion.network.message;

import complexion.client.Atom;
import complexion.resource.Cache;

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
	

	/** Build a FullAtomUpdate object from a server Atom. This can then be used to transfer
	 *  data about a server atom over the network.
	 * 
	 *  @return A FullAtomUpdate object which can be used to fully transmit 
	 *  data relevant to the client.
	 */
	public FullAtomUpdate(complexion.server.Atom atom)
	{
		this.direction = (byte) atom.getDirection(); // a downcast is okay, directions are never large
		this.layer = atom.getLayer();
		this.pos_x = atom.getX();
		this.pos_y = atom.getY();
		this.sprite = atom.getSprite();
		this.sprite_state = atom.getSpriteState();
		this.UID = atom.getUID();
	}
	
	/**
	 * Load this update into a client atom, updating its data.
	 * @param into   The atom which to update with the new data.
	 */
	public void updateClientAtom(complexion.client.Atom into)
	{
		into.direction = this.direction;
		into.sprite = Cache.getSprite(this.sprite);
		into.sprite_state = this.sprite_state;
		into.tile_x = this.pos_x;
		into.tile_y = this.pos_y;
		into.UID    = this.UID;
	}
}
