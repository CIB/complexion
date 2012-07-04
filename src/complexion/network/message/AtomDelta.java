package complexion.network.message;

import java.util.ArrayList;

/** 
 * Transmit a range of updated atoms from server to client.
 * This can be descriptions of entirely new atoms, or updates to existing atoms.
 * It more or less always uses the same format. Creation is simply a full update
 * of an atom that hasn't been sent yet.
 * 
 * The AtomDelta message will also include a viewport center, and a viewrange.
 * This will implicitly tell the client that it can forget about any objects outside this view range.
 * (We might yet go for a caching approach, where stuff outside the client's view is stored, such as turfs,
 *  but that would be implemented quite differently, that is via chunks)
 */
public class AtomDelta {
	public AtomDelta(){};
	
	public ArrayList<AtomUpdate> updates;
	
	/// Center of the client viewport
	public int eye_x, eye_y;
	
	// Viewrange of the client
	public int viewrange;
	
	// World tick on which the atom was sent.
	public int tick;
}
