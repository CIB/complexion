package complexion.server;

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
}
