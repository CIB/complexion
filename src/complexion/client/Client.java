package complexion.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.esotericsoftware.minlog.Log;

import complexion.common.Config;
import complexion.common.Console;
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
			System.out.println(connection.login("head", "password"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Log.set(Log.LEVEL_DEBUG);
		new Console();
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
		/*	if(tick == -1)
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
			}*/
			
			// If we have a tick initialized, that means we're connected,
			// so start processing incoming AtomDelta's
			if(tick != -1)
			{
				// Get a delta from the queue
				AtomDelta delta;
				while((delta = atomDeltas.poll()) != null)
				{
				if(delta != null)
				{
					// Process the individual updates
					for(AtomUpdate update : delta.updates)
					{
						this.processAtomUpdate(update);
					}
					tick++;
				}
				}
				// TODO: remove all atomdelta's that are older than the current tick
				
				// TODO: make sure we're not waiting forever for the tick to come
				//       ticks shouldn't get lost in theory, but who knows
			}
			if(Mouse.isButtonDown(0)) // left click
			{
				onClick(Mouse.getX(),Mouse.getY(),0);
			}
			else if(Mouse.isButtonDown(1)) // Right click
			{
				onClick(Mouse.getX(),Mouse.getY(),1);
			}
			// Check if any keys were pressed.
			while(Keyboard.next() && Display.isVisible())
			{
				int key = Keyboard.getEventKey();
				boolean state = Keyboard.getEventKeyState();
				if(state == true)
				{
					// Only send an input message when the button was released
					InputData data = new InputData(key);
					connection.send(data);
				}
			}
			// Re-render the widget
			renderer.draw();
		}
		
		renderer.destroy();
	}
	/**
	 * 
	 * 
	 */
	private void onClick(int mouse_x,int mouse_y,int key)
	{
		int tile_x = (int)Math.floor(mouse_x/Config.tileWidth);
		int tile_y = (int)Math.floor(mouse_y/Config.tileHeight);
		int offset_x =mouse_x-(tile_x*Config.tileWidth) ;
		int offset_y = mouse_y-(tile_y*Config.tileWidth);
		Atom clicked_atom = null;
		
		for(int x=renderer.atoms.size()-1;x>=0;x--)
		{
			Atom A = renderer.atoms.get(x);
			if(A.tile_x == tile_x && A.tile_y == tile_y)
			{
				if(!A.sprite.isTransparent(offset_x,offset_y,A))
				{
					clicked_atom = A;
					break;
				}
			}
		}
		if(clicked_atom != null)
		{
			System.err.println("Clicked "+clicked_atom.sprite_state);
		}
	}
	
	/**
	 * Process a single AtomUpdate type object to add it to the map/add a new object.
	 * @param data The AtomUpdate to process.
	 */
	private void processAtomUpdate(AtomUpdate data)
	{
		// Check if the atom already exists
		boolean exists = atomsByUID.containsKey(data.UID);
	//	if(Config.debug)
	//		System.err.println("Processing AtomUpdate");
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
		renderer.sortLayers();
	}
	
	private Renderer renderer;
	private ServerConnection connection;
	
	/// Maps Atom UID's to the respective atoms
	private Map<Integer,Atom> atomsByUID = new HashMap<Integer,Atom>();
	
	/// A private HashMap of AtomDeltas which have been incoming.
	/// This maps world ticks to AtomDelta's
	ConcurrentLinkedQueue<AtomDelta> atomDeltas =
			new ConcurrentLinkedQueue<AtomDelta>();
	
	/// Represents the current tick the client is processing from the server.
	/// A value of -1 means that no tick has been processed yet.
	int tick = -1;
}
