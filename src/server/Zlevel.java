package server;

/**
 * @author head
 * Hold's the map for a zlevel.
 * 
 */
public class Zlevel {
	Tile[][] map;
	public Tile getTile(int pos_x,int pos_y)
	{
		return map[pos_x][pos_y];
	}
	public Tile[][] getMap(int pos_x,int pos_y)
	{
		return map;
		
	}
}
