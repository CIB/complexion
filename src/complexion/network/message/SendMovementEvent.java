package complexion.network.message;

/** This is used to notify the client of a movement event on the given tick. 
 *  
 *  Usually sent alongside an AtomDelta.
 **/
public class SendMovementEvent {
	/// Tick on which this movement event starts
	public int beginTick;
	
	/// Tick on which this movement event ends
	public int endTick;
	
	/// UID of the atom which to move
	public int atomUID;
	
	/// Begin and end location of the movement. z-coordinates are not needed,
	/// as movement on the z-axis is not visualized.
	public int start_x, start_y, end_x, end_y;
}
