package complexion.test;

import java.io.IOException;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import complexion.client.Atom;
import complexion.client.Renderer;
import complexion.common.Directions;
import complexion.resource.Sprite;

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
			a.layer = 0;
			r.addAtom(a);
		}
		
		for(int x = 0; x<20; x++) for(int y=0;y<20;y++)
		{
			Atom a = new Atom();
			a.direction = Directions.EAST;
			a.frame = 0;
			a.sprite_state = "muzzle";
			a.sprite = s;
			a.x = 10+x*32;
			a.y = 10+y*32;
			a.layer = 2;
			r.addAtom(a);
		}
		
		r.draw();
		while(true) {
			r.draw();
			Thread.sleep(1000/60);
			
			if(Display.isCloseRequested()){
				break;
			}
		}
	}
}
