package complexion.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;

import complexion.common.Config;
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
	
	/// Permanently passing around server instances is very bothersome.
	///
	/// Since in one application, we will have only one server, use a
	/// global instance instead.
	public static Server current;
	
	/** Start the server program.
	 */
	public static void main(String[] args)
	{
		current = new Server();
		
		Log.set(Log.LEVEL_DEBUG);
		
		// Generate all the Zlevels for the map and store them in this.map
		current.map = new Zlevel[height];
		for(int z = 0; z<height; z++)
		{
			current.map[z] = new Zlevel(z);
		}
		
		for(int x=0; x<10; x++) for(int y=0;y<10;y++)
		{
			// Add a sample object
			Tile test_tile = new Tile(current,x,y,0);
			test_tile.setSprite("floors.dmi");
			test_tile.setSpriteState("floor");
			test_tile.setDirection(Directions.SOUTH);
			test_tile.setLayer(0);
		}
		
		Movable test_mover = new Movable();
		test_mover.setSprite("mask.dmi");
		test_mover.setSpriteState("muzzle");
		test_mover.setLayer(10);
		test_mover.Move(current.getTile(1, 1, 0));
		
		// Start server networking
		try {
			ServerListener master = new ServerListener(current,1024);
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
			current.nextTick();
			if(current.getTick() % 50 < 25)
			{
				test_mover.setSpriteState("owl");
				test_mover.Move(current.getTile(2, 1, 0));
			}
			else
			{
				//test_mover.setSpriteState("muzzle");
				test_mover.Move(current.getTile(3, 1, 0));
			}
			try {
				Thread.sleep(Config.tickLag);
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
		
		// First update all clients
		for(Map.Entry<Connection, Client> entry : clientsByIP.entrySet())
		{
			Client client = entry.getValue();
			// Check if the client has a connection; If not, it's not ready.
			if(client.connection == null) continue;
			
			client.synchronizeAtoms();
		}
		
		// Then clear atom updates for all clients
		for(Map.Entry<Connection, Client> entry : clientsByIP.entrySet())
		{
			Client client = entry.getValue();
			// Check if the client has a connection; If not, it's not ready.
			if(client.connection == null) continue;
			
			// This sets atom.outdated = 0 for all atoms in the client's view
			client.clearAtomsInView();
		}
	}
	

	/** 
	 * Get all chunks that overlap the given view-range.
	 * @return A list that contains all chunks which overlap the range defined by the parameters.
	 */
	public List<Chunk> getOverlappingChunks(int start_x, int end_x, int start_y, int end_y, int start_z, int end_z)
	{
		List<Chunk> rval = new ArrayList<Chunk>();
		
		// convert the tile positions on the x and y axis to chunk indices
		int chunk_start_x = start_x / Chunk.width;
		int chunk_end_x   = end_x   / Chunk.width;
		
		int chunk_start_y = start_y / Chunk.height;
		int chunk_end_y   = end_y   / Chunk.height;
		
		// Make sure everything is within bounds
		if(start_z <          0) start_z = 0;
		if(end_z   > map.length) end_z   = map.length;
		
		// Now populate the return list by iterating over all the
		// relevant chunk indices.
		for(int z = start_z; z<=end_z; z++)
		{
			Zlevel zl = this.map[z];
			for(int x = chunk_start_x; x<=chunk_end_x; x++) 
				for(int y = chunk_start_y; y<=chunk_end_y; y++)
			{
				// Try to extract the chunk and add it if it exists
				Chunk chunk = zl.getChunk(x, y);
				if(chunk != null) rval.add(chunk);
			}
		}
		
		
		return rval;
	}

	// TODO: Create a getTileRange function which will get a list of tiles
	//       within a specific range
	
	/// Maps TCP connections to clients connected via that address
	/// Accessed by networking threads, so it has to be concurrent.
	ConcurrentMap<Connection,Client> clientsByIP 
		= new ConcurrentHashMap<Connection,Client>();
	
}
