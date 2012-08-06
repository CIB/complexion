package complexion.server;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.lwjgl.input.Keyboard;

import complexion.common.Directions;
import complexion.network.message.AtomDelta;
import complexion.network.message.AtomUpdate;
import complexion.network.message.FullAtomUpdate;

/**
 * This class represents a single client connected to the server.
 * The class can be used to send messages to the server etc.
 */
public class Client 
{	
	/** Maintains a list of all dialogs currently open.
	 */
	ConcurrentMap<Integer,DialogHandle> dialogsByUID = new ConcurrentHashMap<Integer,DialogHandle>();
	
	/// Identifies the client in the (as of yet non-existent)
	/// login system
	String account_name;
	
	// The mob that the client uses and input is relayed too. aka the "thing" the client is  controlling
	private Mob holder;
	/// The TCP connection this client uses.
	ClientConnection connection;
	
	// TODO: test variable, remove
	DialogHandle testDialog;

	/** A sort of constructor called after the Client has been set up properly. **/
	public void initialize()
	{
		Mob test_mover = new Mob();
		test_mover.setSprite("mask.dmi");
		test_mover.setSpriteState("muzzle");
		test_mover.setLayer(10);
		test_mover.Move(Server.current.getTile(1, 1, 0));
		setHolder(test_mover);
		
		testDialog = new DialogHandle(this,"complexion.test.TestDialog",null);
		DialogHandle dialog2 = new DialogHandle(this,"randomgarbleclass",null);
		DialogHandle dialog3 = new DialogHandle(this,"complexion.test.KryoTest",null);
		
		testDialog.sendMessage("Test");
	}
	
	/** Handler invoked regularly(every tick) to process things. **/
	public void Tick()
	{
		//testDialog.sendMessage("TickMessage!");
		Object o = testDialog.pollMessage();
		if(o != null) System.out.println(o);
	}
	
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
	
	/** Remove the updated flags from all atoms in view of the client.
	 *  This should be called on each client at the end of the processing
	 *  tick.
	 */
	void clearAtomsInView()
	{
		int eye_x = 0;
		int eye_y = 0;
		
		if(this.eye != null)
		{
			eye_x = eye.getX();
			eye_y = eye.getY();
		}
		
		// Scan over all the atoms and see if they're outdated
		for(int x=eye_x - view_range; x<=eye_x + view_range; x++)
		{
			for(int y=eye_y - view_range; y<=eye_y + view_range; y++)
			{
				// Extract the turf at the given tile
				Tile turf = Server.current.getTile(x, y, 0);
				
				// Make sure the turf actually exists
				if(turf != null)
				{
					// clear the outdated flags for the tile and its contents
					turf.outdated = 0;
					for(Atom content : turf.contents)
					{
						content.outdated = 0;
					}
				}
			}
		}
	}
	
	/** Send an update for all visible atoms to the remote client.
	 * @param server The server on which the world is running.
	 */
	void synchronizeAtoms()
	{
		Server server = Server.current;
		
		AtomDelta delta = new AtomDelta();
		
		// Store on which tick this message was sent.
		delta.tick = server.getTick();
		
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
			// TODO: comment this out when a method has been added to detect it when atoms have changed
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
					
					// Check if the tile is entirely new, or still in the old viewrange
					if(last_x - view_range <= x && x <= last_x + view_range &&
					   last_y - view_range <= y && y <= last_y + view_range)
					{
						// The tile was also on the previous viewport, it's enough to update it
						addTileIfOutdated(turf, delta);
					}
					else
					{
						// The tile wasn't on the previous viewport, resend completely
						addTileToDelta(turf, delta);
					}
					
				}
			}
			
			last_x = delta.eye_x;
			last_y = delta.eye_y;
		}
		else
		{
			// We're not resending everything, so let's find out if any of our atoms changed at all.
			
			// Scan over all the atoms and see if they're outdated
			for(int x=delta.eye_x - view_range; x<=delta.eye_x + view_range; x++)
			{
				for(int y=delta.eye_y - view_range; y<=delta.eye_y + view_range; y++)
				{
					// Extract the turf at the given tile
					Tile turf = server.getTile(x, y, 0);
					addTileIfOutdated(turf, delta);
				}
			}
		}
		
		// Send the atom updates to the remote client.
		if(!delta.updates.isEmpty())
			connection.send(delta);
	}
	/**
	 * Processes input received from clients
	 * @param key : The internal number of a keyboard key
	 */
	public void ProcessInput(int key)
	{
		if(Keyboard.KEY_W == key)
		{
			if(holder != null)
				holder.Step(Directions.NORTH);
		}
		if(Keyboard.KEY_D == key)
		{
			if(holder != null)
				holder.Step(Directions.EAST);
		}
		if(Keyboard.KEY_A == key)
		{
			if(holder != null)
				holder.Step(Directions.WEST);
		}
		if(Keyboard.KEY_S == key)
		{
			if(holder != null)
				holder.Step(Directions.SOUTH);
		}
	}
	/**
	 * @return the holder
	 */
	public Mob getHolder()
	{
		return holder;
	}

	/**
	 * @param holder the holder to set
	 */
	public void setHolder(Mob holder)
	{
		if(holder != null && holder.getClient() != null)
			return;
		this.holder = holder;
		this.holder.setClient(this);
	}
	
	private void addTileToDelta(Tile tile, AtomDelta delta)
	{
		// Make sure the turf actually exists
		if(tile != null)
		{
			delta.updates.add(new FullAtomUpdate(tile));
			for(Atom content : tile.contents)
			{
				delta.updates.add(new FullAtomUpdate(content));
			}
		}
	}
	
	private void addTileIfOutdated(Tile tile, AtomDelta delta)
	{
		// Make sure the turf actually exists
		if(tile != null)
		{
			// TODO: discriminate between the different outdated types
			if(tile.outdated != 0)
			{
				delta.updates.add(new FullAtomUpdate(tile));
			}
			for(Atom content : tile.contents)
			{
				if(content.outdated != 0)
				{
					delta.updates.add(new FullAtomUpdate(content));
				}
			}
		}
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
