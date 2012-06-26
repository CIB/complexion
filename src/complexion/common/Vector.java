package complexion.common;

public class Vector {
	int x;
	int y;
	public Vector(int x,int y)
	{
		this.x = x;
		this.y = y;
	}
	public Vector add(Vector source)
	{
		Vector B = new Vector(source.x+this.x,source.y+this.y);
		return B;
	}
}
