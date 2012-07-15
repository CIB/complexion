package complexion.server;

import complexion.common.Directions;

/** Movable atoms are the only type that define methods for
 *  moving around, Entering tiles etc.
 */
public class Movable extends Atom {
	
	/** Callback handler invoked when this movable attempts to enter a tile.
	 *  @return true if allowed to enter, false if not
	 */
	public boolean Enter(Tile tile)
	{
		return true;
	}
	
	/** Callback handler invoked when this movable has managed to enter a tile.
	 *  Note that this may be invoked by setLoc, which doesn't invoke Enter() first
	 */
	public void Entered(Tile tile)
	{
		return;
	}
	
	/** Function used to try and move into a tile. Calls the
	 *  Enter/Entered family of functions to determine whether it's possible.
	 *  
	 *  @return true if move successful, false otherwise
	 */
	public boolean Move(Tile tile)
	{
		// Concise method of checking if both tile.Enter(this)
		// and this.Enter(tile) succeed.
		boolean success = (tile.Enter(this) && this.Enter(tile));
		
		// If the movement succeeded, update stuff and notify everyone.
		if(success)
		{
			this.setLoc(tile);
			tile.Entered(this);
			this.Entered(tile);
		}
		
		return success;
	}
	
	/** Function used to take a single step into a direction.
	 *  
	 *  Can be overloaded by the user.
	 *  
	 *  @param direction Direction as int into which to move from the current location.
	 *  @return true if move successful, false otherwise.
	 */
	public boolean Step(int direction)
	{
		// Get the offset
		int offset_x = Directions.getOffsetX(direction);
		int offset_y = Directions.getOffsetY(direction);
		
		// Get the new tile by adding the offset to the current location
		int other_x = this.getX() + offset_x;
		int other_y = this.getY() + offset_y;
		
		// TODO: properly handle z coordinates(and possibly directions)
		Tile move_to = Server.current.getTile(other_x, other_y, 0);
		
		// If the tile doesn't exist, failure
		if(move_to == null)
		{
			return false;
		}
		
		// If the tile does exist, see if we can move into it.
		return this.Move(move_to);
	}
}
