package complexion.server;

import java.util.ArrayList;
import java.util.List;

public class Tile extends Atom {
	
	///The x/y location of the tile on the map.
	private int x, y;
	
	/// A package-private list of all the contents of the tile
	List<Atom> contents = new ArrayList<Atom>();
	
	/** Tiles must always be created with an associated map coordinate.
	 *  After having been created, the tile can not been moved.
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
	public Atom getTile()
	{
		return this;
	}
	
	/**
	 * @return A list of all the atoms contained directly in the tile. Does not include contents of the contents.
	 * 		   Modifying this list will not affect the atom.
	 */
	public List<Atom> getContents()
	{
		// Copy the list to make sure it's not modified.
		return new ArrayList<Atom>(contents);
	}
}
