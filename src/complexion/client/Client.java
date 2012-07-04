package complexion.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import complexion.common.Config;
import complexion.network.message.AtomDelta;
import complexion.network.message.AtomUpdate;
import complexion.network.message.FullAtomUpdate;
import complexion.network.message.InputData;

/**
 * Class representing the entire client application, and global
 * client state.
 */
public class Client {
	public static void main(String[] args)
	{
		// This will start the client program and loop indefinitely
		new Client(args);
	}
	
	/**
	 * Notify the user of an error. This function should always work, even when there's no
	 * valid LWJGL context.
	 * @param user_error User-friendly message describing what went wrong.
	 * @param e          Internal stack-trace, useful for debugging.
	 */
	public static void notifyError(String user_error, Exception e)
	{
		System.out.println(user_error);
	}
	
	/**
	 * Client program initialization and loop.
	 */
	public Client(String[] args)
	{
		// Try to log into a preconfigured server.
		// This should become a user dialog later.
		try {
			connection = new ServerConnection(this, InetAddress.getByName("localhost"), 1024);
			System.out.println(connection.login("CIB", "password"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Initialize the client window.
		try {
			renderer = new Renderer();
		} catch (LWJGLException e) {
			Client.notifyError("Error setting up program window.. Exiting.",e);
			System.exit(1); // Exit with 1 to signify that an error occured
		}
		
		// Intercept and process AtomDelta's
		while(!Display.isCloseRequested())
		{
			try {
				// TODO: if too many deltas have stacked up, try to catch up
				//       by waiting shorter
				Thread.sleep(Config.tickLag);
			} catch (InterruptedException e) {
				// Just ignore and continue
			}
			
			// Check if we need to initialize the ticker.
			if(tick == -1)
			{
				// Look for the first tick we can get.
				ArrayList<Integer> keys = new ArrayList<Integer>(atomDeltas.keySet());
				Collections.sort(keys);
				
				// Check if a first tick exists at all.
				if(keys.size() > 0)
				{
					// Extract the key(tick) of the first tick.
					int firstTick = keys.get(0);
					
					// Make this our global tick.
					this.tick = firstTick;
				}
			}
			
			// If we have a tick initialized, that means we're connected,
			// so start processing incoming AtomDelta's
			if(tick != -1)
			{
				// Get a delta from the queue
				AtomDelta delta = atomDeltas.get(tick);
				if(delta != null)
				{
					// Process the individual updates
					for(AtomUpdate update : delta.updates)
					{
						this.processAtomUpdate(update);
					}
					tick++;
				}
				// TODO: remove all atomdelta's that are older than the current tick
				
				// TODO: make sure we're not waiting forever for the tick to come
				//       ticks shouldn't get lost in theory, but who knows
			}
			while(Keyboard.next() && Display.isVisible())
			{
				int key = Keyboard.getEventKey();
				boolean state = Keyboard.getEventKeyState();
				InputData data = new InputData(key,state);
				connection.send(data);
			}
			// Re-render the widget
			renderer.draw();
		}
		
		renderer.destroy();
	}
	
	/**
	 * Process a single AtomUpdate type object to add it to the map/add a new object.
	 * @param data The AtomUpdate to process.
	 */
	private void processAtomUpdate(AtomUpdate data)
	{
		// Check if the atom already exists
		boolean exists = atomsByUID.containsKey(data.UID);
		
		// Check the type of the AtomUpdate
		if(data instanceof FullAtomUpdate)
		{
			FullAtomUpdate full = (FullAtomUpdate) data;
			if(exists)
			{
				// Atom already exists, update it
				Atom old = atomsByUID.get(full.UID);
				
				// Load the entire data of the atom update into the existing atom
				full.updateClientAtom(old);
			}
			else
			{
				// The atom doesn't exist yet, create it
				Atom atom = new Atom();
				full.updateClientAtom(atom);
				
				// Add the created atom to our atom cache, and also
				// to the renderer.
				atomsByUID.put(data.UID, atom);
				renderer.addAtom(atom);
			}
		}
	}
	
	private Renderer renderer;
	private ServerConnection connection;
	
	/// Maps Atom UID's to the respective atoms
	private Map<Integer,Atom> atomsByUID = new HashMap<Integer,Atom>();
	
	/// A private HashMap of AtomDeltas which have been incoming.
	/// This maps world ticks to AtomDelta's
	ConcurrentHashMap<Integer,AtomDelta> atomDeltas =
			new ConcurrentHashMap<Integer,AtomDelta>();
	
	/// Represents the current tick the client is processing from the server.
	/// A value of -1 means that no tick has been processed yet.
	int tick = -1;
}
