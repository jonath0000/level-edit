package graphicsutils;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CompatibleImageCreator {
	
	/**
	 * Create image optimized for drawing on current system.
	 * @param oldImage Image to be optimized.
	 * @return Optimized image.
	 */
	public static Image createCompatibleImage(Image oldImage) {

        GraphicsConfiguration graphicsConfiguration
                = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();
         
        BufferedImage compatibleImage
                = graphicsConfiguration.createCompatibleImage(
                oldImage.getWidth(null),
                oldImage.getHeight(null), 
                Transparency.TRANSLUCENT);

        Graphics g = compatibleImage.getGraphics();
        g.drawImage(oldImage, 0, 0, null);
        g.dispose();
        
        return compatibleImage;
	}
	
	public static Image createCompatibleImageFromFile(String path) {
		BufferedImage loadedImage = null;
		try {
		    loadedImage = ImageIO.read(new File(path));
		} catch (IOException e) {
			System.out.println("Load " + path + " failed.");
			return null;
		}
		return createCompatibleImage(loadedImage);
	}
}
