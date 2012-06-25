package complexion.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class DisplayExample {
	public void start() throws IOException, InterruptedException {
		try {
			Display.setDisplayMode(new DisplayMode(800,600));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		// init OpenGL
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 800, 0, 600, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable( GL11.GL_BLEND );
		GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
		
		Texture t = TextureLoader.getTexture("PNG", new FileInputStream("mmask.dmi"));
	 
		while (!Display.isCloseRequested()) {
		    // Clear the screen and depth buffer
		    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);	
			
		    // set the color of the quad (R,G,B,A)
		    GL11.glColor3f(0.5f,0.5f,1.0f);
		    
		    for(int x=0; x<30; x++) for(int y=0; y<30; y++)
		    {
				Color.white.bind();
				t.bind(); // or GL11.glBind(texture.getTextureID());
			    
			    // draw quad
				GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(0,0);
				GL11.glVertex2f(100+32*x,100+32*y);
				GL11.glTexCoord2f(1,0);
				GL11.glVertex2f(100+32*x+t.getTextureWidth(),100+32*y);
				GL11.glTexCoord2f(1,1);
				GL11.glVertex2f(100+32*x+t.getTextureWidth(),100+32*y+t.getTextureHeight());
				GL11.glTexCoord2f(0,1);
				GL11.glVertex2f(100+32*x,100+32*y+t.getTextureHeight());
				GL11.glEnd();
		    }
	 
		    Display.update();
		    
		    Thread.sleep(20);
		}
		
		Display.destroy();
	}
	
	public static void main(String[] argv) throws IOException, InterruptedException {
		DisplayExample displayExample = new DisplayExample();
		displayExample.start();
	}
}
