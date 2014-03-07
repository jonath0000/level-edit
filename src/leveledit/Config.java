package leveledit;

import levelmodel.DummyObject;
import graphicsutils.CompatibleImageCreator;

import java.awt.Color;
import java.awt.Image;
import java.awt.MediaTracker;
import javax.swing.ImageIcon;

/**
 * Represents the current project configuration in a config file.
 * 
 * @todo Could/should be done as XML file instead.
 */
public class Config {

	// Values read from file.
	public Image tiles;
	public ImageIcon dummyPics;
	public Color bgCol;
	public String typeNames[];
	public DummyObject typeData[];
	public int sourceImageTileSize = 16;
	public int representationTileSize = 32;
	public int tilesPerRow = 8;
	public int numTiles = 200;
	public int mirrorTileVal = 100;
	public String projectPath = ".";
	public String exportPath = ".";

	public Config(String path, String tileMapImageOverride) {
		System.out.println("Loading config file " + path);
		ResFileReader r;
		try {			
			r = new ResFileReader(path);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		try {	
			r.gotoPost("project_path", false);
			r.nextLine();
			projectPath = r.getWord();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("[project_path] not configured, using default location.");
		}
		
		try {	
			r.gotoPost("export_path", false);
			r.nextLine();
			exportPath = r.getWord();
		} catch (Exception e) {
			System.out.println("[export_path] not configured, using default location " + exportPath);
		}
		
		try {			
			// tile image path
			r.gotoPost("tiles", false);
			r.nextLine();
			String strTiles = r.getWord();
			if (tileMapImageOverride != null && tileMapImageOverride.length() > 0) {
				strTiles = tileMapImageOverride;
			}
			tiles = CompatibleImageCreator.createCompatibleImage(new ImageIcon(strTiles).getImage());
			if (tiles == null)
				throw new Exception("Couldn't load tile image!");
			sourceImageTileSize = r.getNextWordAsInt();
			numTiles = r.getNextWordAsInt();
			tilesPerRow = r.getNextWordAsInt();

			// mirror tile val
			r.gotoPost("tile_mirror_idx", false);
			r.nextLine();
			mirrorTileVal = r.getWordAsInt();

			// dummy pic path
			r.gotoPost("dummypics", false);
			r.nextLine();
			String strDummys = r.getWord();
			dummyPics = new ImageIcon(strDummys);
			if (dummyPics.getImageLoadStatus() != MediaTracker.COMPLETE)
				throw new Exception("Couldn't load dummy image!");

			// dummy definitions
			r.gotoPost("dummys", false);
			r.nextLine();
			int nDummys = r.getWordAsInt();
			if (nDummys > 100) {
				throw new Exception("Max number of [dummys] is 100!");
			}

			typeNames = new String[nDummys];
			typeData = new DummyObject[nDummys];

			for (int i = 0; i < nDummys; i++) {
				r.nextLine();
				String caption = r.getWord();
				String name = r.getNextWord();
				int w = r.getNextWordAsInt();
				int h = r.getNextWordAsInt();
				boolean hasPic = r.getNextWordAsBool();
				int picX = r.getNextWordAsInt();
				int picY = r.getNextWordAsInt();
				int picW = picX + r.getNextWordAsInt();
				int picH = picY + r.getNextWordAsInt();
				String addData = r.getRestOfLine();
				DummyObject dummy = new DummyObject(1, 1, w, h, addData, name, hasPic, picX, picY, picW, picH);
				typeNames[i] = caption;
				typeData[i] = dummy;
			}

			// bg color
			r.gotoPost("bgcol", false);
			r.nextLine();
			String hexrgb = r.getWord();
			bgCol = new Color(Integer.parseInt(hexrgb, 16));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
