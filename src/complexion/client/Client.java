package complexion.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import complexion.common.Config;
import complexion.common.Console;
import complexion.common.Utils;
import complexion.network.message.AtomDelta;
import complexion.network.message.AtomUpdate;
import complexion.network.message.FullAtomUpdate;
import complexion.network.message.InputData;

import com.esotericsoftware.minlog.Log;

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;

/**
 * Class representing the entire client application, and global
 * client state.
 */
public class Client {	

	/** Maintains a list of all dialogs currently open.
	 */
	ConcurrentMap<Integer,Dialog> dialogsByUID = new ConcurrentHashMap<Integer,Dialog>();
	
	/** Permanently passing around client instances is very bothersome.
	    Since in one application, we will have only one client, use a
		global instance instead.
	**/
	public static Client current;
	
	/** TWL topevel GUI instance this client uses for rendering its GUI widgets. **/
	GUI gui;
	
	/**
	 * Client program initialization and loop.
	 */
	public static void main(String[] args)
	{
		current = new Client();
		
		// See https://code.google.com/p/minlog/#Log_level
		//Log.set(Log.LEVEL_DEBUG);
		//new Console();
				
		// Initialize the client window.
		try {
			current.renderer = new Renderer(1.5);
		} catch (LWJGLException e) {
			Client.notifyError("Error setting up program window. Exiting.", e);
			System.exit(1); // Exit with 1 to signify that an error occured
		}
		
		// Build the GUI
        LWJGLRenderer guiRenderer;
		try {
			guiRenderer = new LWJGLRenderer();
	        current.gui = new GUI(guiRenderer);
	        // TODO: load the root level theme from a nicer file(in res/, no weird ../../.. paths)
	        ThemeManager theme = ThemeManager.createThemeManager(
	        		Client.class.getResource("test.xml"), guiRenderer);
	        current.gui.applyTheme(theme);
		} catch (LWJGLException e) {
			Client.notifyError("Error setting up GUI instance. Exiting.", e);
			System.exit(1);
		} catch (IOException e) {
			Client.notifyError("Error setting up GUI instance. Exiting.", e);
			System.exit(1);
		}

		// Try to log into a preconfigured server.
		// This should become a user dialog later.
		try {
			current.connection = new ServerConnection(current, InetAddress.getByName("localhost"), 1024);
			System.out.println(current.connection.login("head", "password"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		// Intercept and process AtomDelta's
		while(!Display.isCloseRequested())
		{
			if(current.atomDeltas.size() > 0)
			{
				Utils.sleep(Config.tickLag / current.atomDeltas.size());
			} else
			{
				Utils.sleep(1);
			}
					
			// If we have a tick initialized, that means we're connected,
			// so start processing incoming AtomDelta's
			if(current.tick != -1)
			{
				// We will only be processing a single "tick" here. Before consequent ticks are processed,
				// control will be given back to the client, which will lead to the updates being rendered, as
				// well as a small delay(Config.tickLag) being put in place.
				
				// By doing this, we can make sure there's always a more or less constant delay between ticks, 
				// which will smoothen out movement, animation and other changes to the map.
			
				// Without this "controlled delay" in processing updates, if the client were to experience 
				// minor lag spikes (of say 0.2 seconds), what he'd see is a mob moving 0 tiles due to the lag, 
				// then 2 tiles at once when two packages arrive at once, then 0 tiles, then 2 tiles at once, 
				// i.e. the "time" would be distorted from the client's point of view
						
				// Get a delta from the queue
				AtomDelta delta = current.atomDeltas.poll();
				if(delta != null)
				{
					// whether we're going to process this update
					boolean update_relevant = true;
							
					// Check if the update's tick is valid
					if(delta.tick <= current.tick)
					{
						// Update too old, ignore
						update_relevant = false;
					}
					else if(delta.tick > current.tick + 1)
					{
						// The tick is not the next tick. That's bad, it means we missed something.
						// When this occurs, that is an actual bug(as we do not want to miss any updates), so do not
						// remove this error, fix your buggy code instead.
						System.err.println("Next tick from server is " + delta.tick + ", but client is only at " + current.tick);
					}
					
					if(update_relevant)
					{
						// Process the individual updates
						for(AtomUpdate update : delta.updates)
						{
							current.processAtomUpdate(update);
						}
						
						// Skip ahead to the tick we just processed
						current.tick = delta.tick;
					}
				}
			}
			
			// Process mouse click events
			// TODO: Add scroll events from LWJGLInput.java
            while(Mouse.next()) {
            	// First check whether TWL can handle the event
                boolean handledByTWL = current.gui.handleMouse(
                        Mouse.getEventX(), current.gui.getHeight() - Mouse.getEventY() - 1,
                        Mouse.getEventButton(), Mouse.getEventButtonState());
                
                // If TWL can't handle the click, and the event was a mouse button press, 
                // check whether the event is an atom click
                if(!handledByTWL && Mouse.getEventButtonState())
                {
					current.onClick(Mouse.getEventX(),Mouse.getEventY(),Mouse.getEventButton());
                }
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
					current.connection.send(data);
				}
			}
			
			// Handle dialog messages
			for(Dialog dialog : Client.current.dialogsByUID.values())
			{
				Object message = dialog.messageQueue.poll();
				while(message != null)
				{
					// Invoke the Dialog's handler for processing messages
					dialog.processMessage(message);
					
					message = dialog.messageQueue.poll();
				}
			}
			
			// Clear the screen and depth buffer
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			
			// Re-render the widget
			current.renderer.draw();
			
			// Draw the GUI
			// TODO: handle mouse and keyboard events where appropriate
			current.gui.updateTime();
			current.gui.draw();

			Display.update();
		}

		current.renderer.destroy();
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
	 * 
	 * Method called when the user clicks
	 * Calculates the tile position 
	 * @param mouse_x  // The screen pixel x location of the click
	 * @param mouse_y // The screen pixel y location of the click
	 * @param key // Left Button(0) or Right Button(1)
	 */
	private void onClick(int mouse_x,int mouse_y,int key)
	{
		int tile_x = (int)Math.floor(mouse_x/Config.tileWidth); // Give us the tile y cordinate
		int tile_y = (int)Math.floor(mouse_y/Config.tileHeight);// Give us the tile x cordinate
		int offset_x =mouse_x-(tile_x*Config.tileWidth) ;// calculate what pixel inside the tile
		int offset_y = mouse_y-(tile_y*Config.tileWidth); // calculate what pixel inside the tile
		Atom clicked_atom = null;
		// I have to iterate from the end due that its sorted by layer
		for(int x=renderer.atoms.size()-1;x>=0;x--)
		{
			Atom A = renderer.atoms.get(x); // Get the atom
			if(A.tile_x == tile_x && A.tile_y == tile_y) // Check if atom is in the tile
			{
				if(!A.isTransparent(offset_x,offset_y)) // Check if said pixel is transparent.
				{
					clicked_atom = A;
					break;
				}
			}
		}
		// TODO: Debug code remove.
		if(clicked_atom != null)
		{
			System.err.println("Clicked "+clicked_atom.sprite_state);
		}
		// TODO: send event to server
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
		renderer.sortLayers();
	}
	
	/** Get the width of the LWJGL display **/
	public int getDisplayWidth()
	{
		return Display.getWidth();
	}

	

	/** Get the height of the LWJGL display **/
	public int getDisplayHeight()
	{
		return Display.getHeight();
	}

	
	private Renderer renderer;
	
	/** Instance responsible for exchanging messages with the server. **/
	ServerConnection connection;
	
	/// Maps Atom UID's to the respective atoms
	private Map<Integer,Atom> atomsByUID = new HashMap<Integer,Atom>();
	
	/// A private Queue of AtomDeltas which have been incoming.
	/// Sorted by the order in which they arrived.
	ConcurrentLinkedQueue<AtomDelta> atomDeltas =
			new ConcurrentLinkedQueue<AtomDelta>();
	
	/// Represents the current tick the client is processing from the server.
	/// A value of -1 means that no tick has been processed yet.
	int tick = -1;

	public void setTick(int new_tick)
	{
		tick = new_tick;
	}
}
