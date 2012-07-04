package complexion.server;


public class Tile extends Atom {
	
	///The x/y location of the tile on the map.
	private int x, y;
	
	/** Tiles must always be created with an associated map coordinate.
	 *  After having been created, the tile can not be moved.
	 */
	public Tile(Server server, int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		
		// Overwrite the existing tile.
		server.setTile(x, y, z, this);
	}

	/**
	 * @return The x location of the tile.
	 */
	public int getX()
	{
		return x;
	}
	/**
	 * @return The y location of the tile.
	 */
	public int getY()
	{
		return y;
	}
	/**
	 * This is here so that it doesn't inherit this from Atom.
	 * Don't call this.
	 */
	public void setLoc(Atom new_loc)
	{
		return;
	}
	/**
	 * Changed from Atom so that if loc does get set somehow then it still returns null.
	 * If it's not set then it would do the same thing, but be faster this way.
	 * @param new_loc the new location of the Atom
	 * @return null
	 */
	public Atom getLoc()
	{
		return null;
	}
	/**
	 * Gets the tile object at the bottom of whatever location it is on, which since this is
	 * the tile, is this.
	 * @return this
	 */
	public Tile getTile()
	{
		return this;
	}
	
	/** Callback handler invoked when another atom attempts to enter this tile.
	 * 
	 *  Usually invoked by movable.Move()
	 *  
	 *  Note that this should not be used for path-finding.
	 * 
	 *  @return true if allowed to enter, false if not
	 */
	public boolean Enter(Movable mover)
	{
		return true;
	}
	
	/** Callback handler invoked when another atom has managed to enter this tile.
	 *  It needn't necessarily have gone through the Move/Enter functions to do this,
	 *  but may have simply used setLoc.
	 */
	public void Entered(Movable mover)
	{
		return;
	}
}
