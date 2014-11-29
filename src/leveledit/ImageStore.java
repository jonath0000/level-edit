package leveledit;

import graphicsutils.CompatibleImageCreator;

import java.awt.Image;
import java.awt.MediaTracker;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

public class ImageStore {
	
	private Map<String, Image> images;
	private final String imageDirectory;
	
	public ImageStore(String imageDirectory) {
		if (!imageDirectory.endsWith("/")) {
			imageDirectory = imageDirectory + "/";
		}
		this.imageDirectory = imageDirectory;
		images = new HashMap<String, Image>();
	}
	
	private boolean loadImage(String imageName) {
		String imageToLoad = imageDirectory + imageName + ".png";
		Image image = CompatibleImageCreator.createCompatibleImageFromFile(imageToLoad);
		images.put(imageName, image);
		System.out.println(" - OK.");
		return true;
	}
	
	public Image getImage(String imageName) {
		if (!images.containsKey(imageName)) {
			if (!loadImage(imageName)) {
				System.out.println("Couldn't find image " + imageName);
				return null;
			}
		}
		return images.get(imageName);
	}
}
