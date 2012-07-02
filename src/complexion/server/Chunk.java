package complexion.server;

/**
 * @author head
 * A small part of a larger map.
 */
public class Chunk
{
	/// The global x,y,z location of the chunk
	private int global_x,global_y,global_z;
	/// The tiles inside this chunk
	private Tile[][] contents; 
	
	/// the size of the chunk width (tiles)
	public static int width = 50;
	/// the size of the chunk height (tiles)
	public static int height = 50;

	/// if the chunk is active
	private boolean active = false;
	
	/** Initialize the chunk.
	 * 
	 * @param x The global x chunk index at which the chunk starts.
	 * @param y The global y chunk index at which the chunk starts.
	 * @param z The global z chunk index at which the chunk starts.
	 */
	public Chunk(int x, int y, int z)
	{
		contents = new Tile[Chunk.width][Chunk.height];
	}
	
	/**
	 * @return the chunks X location on the zlevel
	 */
	public int getGlobalX()
	{
		return global_x;
	}
	/**
	 * @return the chunks Y location on the zlevel
	 */
	public int getGlobalY()
	{
		return global_y;
	}
	/**
	 * The chunks Z location
	 * @return the z
	 */
	public int getGlobalZ()
	{
		return global_z;
	}
	
	/**
	 * if the chunk is active and should be processed
	 * @return return true if it active and returns false if not
	 */
	public boolean isActive()
	{
		return active;
	}
	/**
	 * unloads the chunk to a  cache file
	 * @return true if successful false if it failed.
	 */
	public boolean unloadChunk()
	{
		return false;
	}
	
	/**
	 * Loads the chunk from its cache file
	 * @return true if successful false if it failed.
	 */
	public boolean loadChunk()
	{
		return false;
	}
	/**
	 * Get the tile at the specified tile-position(NOT pixel-position)
	 * Returns null if Tile is past map boundaries.
	 */
	public Tile getTile(int x,int y)
	{
		// Extract the position within the chunk
		x = x % Chunk.width;
		y = y % Chunk.height;
		
		// Make sure the tile is within bounds
		if( x >= Chunk.width || y >= Chunk.height || x < 0 || y < 0)
		{
			return null;
		}
		
		Tile T = contents[x][y];
		return T;
		
	}
	
	/** Overwrite the tile at the specified tile position with the given tile.
	 */
	void setTile(int x, int y, Tile tile)
	{
		// Extract the position within the chunk
		x = x % Chunk.width;
		y = y % Chunk.height;
		
		// Make sure the tile is within bounds
		if( x >= Chunk.width || y >= Chunk.height || x < 0 || y < 0)
		{
			return;
		}
		
		contents[x][y] = tile;
	}
	
	/**
	 * Processes all tiles in the chunk. Calls Tile.Tick that properly should process its contents.
	 */
	public void Tick()
	{
		for(int x = 0;x<Chunk.width;x++)
		{
			for(int y = 0;x<Chunk.height;y++)
			{
				Tile T = contents[x][y];
				T.Tick();
			}
		}
	}
}
