package complexion;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static complexion.Directions.*;

import javax.imageio.ImageIO;

public class Test {
	public static void main(String[] args) throws IOException
	{
		Sprite s = new Sprite("mask.dmi");
		
	    BufferedImage bi = s.getImage("muzzle", 100, 100);
	    File outputfile = new File("D:/saved.png");
	    ImageIO.write(bi, "png", outputfile);
	}
}
