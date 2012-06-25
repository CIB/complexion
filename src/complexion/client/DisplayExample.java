package complexion.client;

import java.io.IOException;

import org.lwjgl.LWJGLException;
import complexion.Directions;
import complexion.Sprite;

public class DisplayExample {
	public static void main(String[] argv) throws IOException, InterruptedException, LWJGLException {
		Renderer r = new Renderer();
		
		Sprite s = new Sprite("mask.dmi");
		
		for(int x = 0; x<20; x++) for(int y=0;y<20;y++)
		{
			Atom a = new Atom();
			a.direction = Directions.SOUTH;
			a.frame = 0;
			a.sprite_state = "muzzle";
			a.sprite = s;
			a.x = 10+x*32;
			a.y = 10+y*32;
			r.atoms.add(a);
		}
		
		r.draw();
		for(int i=0; i<1000; i++) {
			for(Atom a : r.atoms)
			{
				a.x += 1;
			}
			r.draw();
			Thread.sleep(10);
		}
	}
}
