package complexion.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.esotericsoftware.kryonet.Connection;

/**
 * Server class containing all global definitions for a specific server instance.
 */
public class Server {
	// Fixed-size array of the map z-levels
	// Probably should use lists instead, since we might want to resize map size
	// at runtime.
	private Zlevel[] map;
	
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

	// TODO: Create a getTileRange function which will get a list of tiles
	//       within a specific range
	
	/// Maps TCP connections to clients connected via that address
	/// Accessed by networking threads, so it has to be concurrent.
	ConcurrentMap<Connection,Client> clientsByIP 
		= new ConcurrentHashMap<Connection,Client>(); 
}
