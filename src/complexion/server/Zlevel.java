package complexion.server;

/**
 * @author head
 * Hold's the map for a zlevel.
 * 
 * This is basically one "slice" of the map that holds all the tiles that are on the same
 * "height". 
 * 
 * Thus far, it's unclear whether we will support seeing tiles on a "below" z-level,
 * or whether different z-levels will be completely separate maps.
 */
public class Zlevel {
	// Fixed-size 2d array of all map tiles on this z-level
	private Tile[][] map;
	
	/**
	 * Get the tile at the specified tile-position(NOT pixel-position)
	 * Returns null if Tile is past map boundaries.
	 */
	Tile getTile(int pos_x,int pos_y)
	{
		return map[pos_x][pos_y];
	}
	
	// TODO: Create a getTileRange function which will get a list of tiles
	//       within a specific range
}
