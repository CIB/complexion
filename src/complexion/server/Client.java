package complexion.server;

import java.util.ArrayList;

import complexion.network.message.AtomDelta;
import complexion.network.message.AtomUpdate;
import complexion.network.message.FullAtomUpdate;

/**
 * This class represents a single client connected to the server.
 * The class can be used to send messages to the server etc.
 */
public class Client 
{
	/// Identifies the client in the (as of yet non-existent)
	/// login system
	String account_name;
	
	/// The TCP connection this client uses.
	ClientConnection connection;

	public String getAccountName() {
		return account_name;
	}
	
	/**
	 * @return The current atom determining the client's view range
	 */
	public Atom getEye() {
		return eye;
	}

	/** Update the client's viewport, this may cause the client's view to
	 *  "jump" elsewhere
	 * @param The new atom to associate the client's view range with
	 */
	public void setEye(Atom eye) {
		this.eye = eye;
	}
	
	/** Send an update for all visible atoms to the remote client.
	 * @param server The server on which the world is running.
	 */
	public void synchronizeAtoms(Server server)
	{
		AtomDelta delta = new AtomDelta();
		
		// Extract the viewport center from this.eye
		if(this.eye != null)
		{
			delta.eye_x = this.eye.getX();
			delta.eye_y = this.eye.getY();
		}
		else
		{
			// TODO: rather than setting the position to 0, notify the client somehow
			//       that they aren't on the map at all anymore
			delta.eye_x = 0; delta.eye_y = 0;
		}
		
		// Send how far the client can see
		delta.viewrange = this.view_range;
		
		// Start building the list of atoms to update
		delta.updates = new ArrayList<AtomUpdate>();
		
		// TODO: rather than resend everything when anything changed, try to somehow figure out
		//		 which part of the viewrange is new, and which is old
		if(delta.eye_x != last_x || delta.eye_y != last_y || resendEverything)
		{
			resendEverything = false;
			
			// For now, simply resend everything
			
			// Iterate over all the atoms in the given range
			// TODO: Add a function to Server that extracts a range for us
			for(int x=delta.eye_x - view_range; x<=delta.eye_x + view_range; x++)
			{
				for(int y=delta.eye_y - view_range; y<=delta.eye_y + view_range; y++)
				{
					// Extract the turf at the given tile
					Tile turf = server.getTile(x, y, 0);
					
					// Make sure the turf actually exists
					if(turf != null)
					{
						delta.updates.add(new FullAtomUpdate(turf));
						for(Atom content : turf.contents)
						{
							delta.updates.add(new FullAtomUpdate(turf));
						}
					}
				}
			}
			
			// TODO: Resend the tiles in the viewrange that have already been sent,
			//		 but have changed since then.
			
		}
		
		// Send the atom updates to the remote client.
		connection.send(delta);
	}

	/// An atom on the map that this client is associated with.
	/// This will determine the client's current view range on the map.
	private Atom eye;
	
	/// Describes how far the client can see from her eye in each direction.
	private int view_range = 8;
	
	/// The last viewport(center and size) that was sent to the client
	private int last_x, last_y, last_z, last_view_range;
	
	/// Force the synchronization handler to resend all atoms.
	/// This flag will be automatically cleared once everything has been sent.
	/// Set to true by default, because on the first tick everything should be resent.
	private boolean resendEverything = true;
}
