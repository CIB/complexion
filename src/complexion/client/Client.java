package complexion.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import complexion.network.message.AtomDelta;
import complexion.network.message.AtomUpdate;
import complexion.network.message.FullAtomUpdate;

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
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// Just ignore and continue
			}
			
			// Get a delta from the queue
			AtomDelta delta = atomDeltas.poll();
			if(delta != null)
			{
				// Process the individual updates
				for(AtomUpdate update : delta.updates)
				{
					this.processAtomUpdate(update);
				}
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
	
	/// A private queue of AtomDeltas which have been incoming.
	/// TODO: Make sure the AtomDelta's are sorted by their respective tick
	ConcurrentLinkedQueue<AtomDelta> atomDeltas =
			new ConcurrentLinkedQueue<AtomDelta>();
}
