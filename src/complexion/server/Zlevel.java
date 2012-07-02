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
	/// Fixed sized array containing chunks each being 50x50(tiles) 
	private Chunk[][] map;
	private int level_width = 50;
	private int level_height = 50;
	private int z;
	
	/** Create a new Zlevel and initialize its chunks.
	 * @param z The z coordinate of the z-level
	 */
	public Zlevel(int z)
	{
		// Remember the z-level
		this.z = z;
		
		// Create the array to store the chunks in
		map = new Chunk[level_width][level_height];
		
		// Populate the chunk array
		for(int x=0;x<level_width;x++)
		{
			for(int y =0;y<level_height;y++)
			{
				map[x][y] = new Chunk(x,y,z);
			}
		}
	}
	/**
	 * Processes all chunks on the zlevel that are active.
	 */
	public void Tick()
	{
		for(int x=0;x<level_width;x++)
		{
			for(int y =0;y<level_height;y++)
			{
				Chunk cur_chunk = map[x][y];
				if(cur_chunk.isActive()) // if it's not in hibernation tick it.
					cur_chunk.Tick();
			}
		}
	}
	/**
	 * @param pos_x : the world x position of the tile
	 * @param pos_y : the world y position of the tile
	 * @param pos_z : the world z position of the tile
	 * Get the tile at the specified tile-position(NOT pixel-position)
	 * Returns null if Tile is past map boundaries. or the chunk is not present.
	 */
	Tile getTile(int pos_x,int pos_y)
	{
		Chunk chunk = getChunk(pos_x, pos_y);
		if(chunk == null) return null;
		
		return chunk.getTile(pos_x, pos_y);
	}
	
	/** Overwrite the tile at the specified tile position with the given tile.
	 */
	void setTile(int x, int y, Tile tile)
	{
		Chunk chunk = getChunk(x, y);
		if(chunk == null) return;
		
		chunk.setTile(x, y, tile);
	}
	
	/**
	 * Returns the chunk that contains the tile at (x,y)
	 * @param x Global tile coordinate on the x-axis
	 * @param y Global tile coordinate on the y-axis
	 * @return
	 */
	Chunk getChunk(int x, int y)
	{
		// Attempt to extract the chunk at the given index
		try
		{
			// Find the chunk that contains the tile at (x,y)
			Chunk chunk = map[x / Chunk.width][y / Chunk.height];
			return chunk;
		}
		catch(NullPointerException e)
		{
			// If the chunk was outside the bounds, return null
			return null;
		}
	}
}
