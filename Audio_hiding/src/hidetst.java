import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;

public class hidetst {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		BufferedImage img=null;
		try {
			img = ImageIO.read(new File("president_speech.wav"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(int width=0; width < img.getWidth(); width++)
		{
		    for(int height=0; height < img.getHeight(); height++)
		    {
		          Color temp = new Color(0,0,0);
		          img.setRGB(width, height, temp.getRGB());
		    }
		}
		try {
			ImageIO.write(img, "jpg", new File("check.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
	}

}
