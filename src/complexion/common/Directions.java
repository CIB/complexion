package complexion.common;

public class Directions {
	public static final int CENTER = 0;
	public static final int NORTH  = 1;
	public static final int SOUTH  = 2;
	public static final int EAST   = 4;
	public static final int WEST   = 8;
	public static final int UP     = 16;
	public static final int DOWN   = 32;
	
	public static final int NORTHEAST = 5;
	public static final int NORTHWEST = 9;
	public static final int SOUTHEAST = 6;
	public static final int SOUTHWEST = 10;
	
	/* Extracts the offset associated with a direction on the x-axis.
	 * @param direction The direction to extract the coordinate from.
	 * @return The offset on the x-axis you'd get if you took one step into this direction.
	 */
	public static int getOffsetX(int direction)
	{
		if((direction & WEST) != 0)
		{
			return -1;
		}
		if((direction & EAST) != 0)
		{
			return 1;
		}
		return 0;
	}
	
	/* Extracts the offset associated with a direction on the y-axis.
	 * @param direction The direction to extract the coordinate from.
	 * @return The offset on the y-axis you'd get if you took one step into this direction.
	 */
	public static int getOffsetY(int direction)
	{
		if((direction & SOUTH) != 0)
		{
			return -1;
		}
		if((direction & NORTH) != 0)
		{
			return 1;
		}
		return 0;
	}
}
