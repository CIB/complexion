package complexion.server;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;
import complexion.common.Console;
import complexion.common.Directions;

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
		
		Log.set(Log.LEVEL_TRACE);
		
		// Generate all the Zlevels for the map and store them in this.map
		server.map = new Zlevel[height];
		for(int z = 0; z<height; z++)
		{
			server.map[z] = new Zlevel(z);
		}
		
		// Add a sample object
		Tile test_tile = new Tile(server,1,1,0);
		test_tile.setSprite("mask.dmi");
		test_tile.setSpriteState("muzzle");
		test_tile.setDirection(Directions.SOUTH);
		
		// Start server networking
		try {
			ServerListener master = new ServerListener(server,1024);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		// Create a window so that the application can be closed
		// This is for debugging only
		Console console = new Console();
		console.run();
		
		// Run the world in a loop
		while(true)
		{
			server.nextTick();
			if(server.getTick() % 50 < 25)
			{
				test_tile.setSpriteState("owl");
			}
			else
			{
				test_tile.setSpriteState("muzzle");
			}
			try {
				Thread.sleep(100);
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
		// Make sure the position exists
		if(pos_z > map.length || pos_z < 0)
		{
			return null;
		}
		
		return map[pos_z].getTile(pos_x, pos_y);
	}
	
	/** Overwrite the tile at the specified tile position with the given tile.
	 * @param x,y,z Position on the map where the tile will be inserted.
	 * @param tile  Tile to be inserted into the map.
	 */
	public void setTile(int x, int y, int z, Tile tile)
	{
		// Make sure the position exists
		if(z > map.length || z < 0)
		{
			return;
		}
		
		map[z].setTile(x, y, tile);
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
