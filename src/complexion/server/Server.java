package complexion.server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.esotericsoftware.kryonet.Connection;

/**
 * Server class containing all global definitions for a specific server instance.
 */
public class Server {
	// Fixed-size array of the map z-levels
	// Probably should use lists instead, since we might want to resize map size
	// at runtime.
	private Zlevel[] map;
	
	///  The current iteration of the world ticker.
	private int tick = 0;
	
	/// Height of the map in z-levels
	private static int height = 1;
	
	/** Start the server program.
	 */
	public static void main(String[] args)
	{
		Server server = new Server();
		
		// Generate all the Zlevels for the map and store them in this.map
		server.map = new Zlevel[height];
		for(int z = 0; z<height; z++)
		{
			server.map[z] = new Zlevel(z);
		}
		
		// Start server networking
		try {
			ServerListener master = new ServerListener(server,1024);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		// Create a window so that the application can be closed
		// This is for debugging only
		JFrame frame = new JFrame("Complexion Server");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel emptyLabel = new JLabel("");
		emptyLabel.setPreferredSize(new Dimension(400, 100));
		frame.getContentPane().add(emptyLabel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		
		// Run the world in a loop
		while(true)
		{
			server.nextTick();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// Just continue ^^
			}
		}
	}
	
	/**
	 * Get the tile at the specified map position(NOT pixel coordinates)
	 * 
	 * Returns null if position exceeds map boundaries.
	 */
	public Tile getTile(int pos_x, int pos_y, int pos_z)
	{
		Zlevel zl = map[pos_z];
		
		if(zl == null) return null;
		
		return zl.getTile(pos_x, pos_y);
	}
	
	/**
	 *  Handle a login request from a client. This will later utilize some kind
	 *  of account system, which may work similarly to the BYOND one.
	 *  
	 *  @return true if login info valid, false otherwise
	 */
	public boolean handleLoginRequest(String account_name, String password)
	{
		// accept ALL the login requests
		return true;
	}
	
	/** 
	 * @return The current iteration of the world ticker
	 */
	public int getTick()
	{
		return this.tick;
	}
	
	/**
	 * Run the next tick iteration.
	 * 
	 * This function is responsible for gathering data about all changes to objects
	 * and synchronizing these changes with the clients.
	 */
	public void nextTick()
	{
		this.tick++;
		
		for(Map.Entry<Connection, Client> entry : clientsByIP.entrySet())
		{
			Client client = entry.getValue();
			
			// Check if the client has a connection; If not, it's not ready.
			if(client.connection == null) continue;
			
			client.synchronizeAtoms(this);
		}
	}

	// TODO: Create a getTileRange function which will get a list of tiles
	//       within a specific range
	
	/// Maps TCP connections to clients connected via that address
	/// Accessed by networking threads, so it has to be concurrent.
	ConcurrentMap<Connection,Client> clientsByIP 
		= new ConcurrentHashMap<Connection,Client>(); 
}
