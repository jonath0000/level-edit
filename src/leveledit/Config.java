package leveledit;

import levelmodel.DummyObject;
import graphicsutils.CompatibleImageCreator;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.ImageIcon;

/**
 * Represents the current project configuration in a config file.
 * 
 * @todo Could/should be done as XML file instead.
 */
public class Config {

	// Values read from file.
	public Image tiles;
	public Color bgCol;
	public ArrayList<DummyObject> typeData;
	public int sourceImageTileSize = 16;
	public int representationTileSize = 32;
	public int tilesPerRow = 8;
	public int numTiles = 200;
	public int mirrorTileVal = 100;
	public String projectPath = ".";
	public String exportPath = ".";
	public String dummyImagePath = ".";

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
			r.gotoPost("dummy_image_path", false);
			r.nextLine();
			dummyImagePath = r.getWord();
		} catch (Exception e) {
			System.out.println("[dummy_image_path] not configured, using default location " + dummyImagePath);
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

			// dummy definitions
			r.gotoPost("dummys", false);
			typeData = new ArrayList<DummyObject>();

			r.nextLine();
			do {
				String name = r.getWord();
				int w = r.getNextWordAsInt();
				int h = r.getNextWordAsInt();
				String addData = r.getRestOfLine();
				DummyObject dummy = new DummyObject(1, 1, w, h, addData, name);
				typeData.add(dummy);
				r.nextLine();
			} while (!r.getWord().equals("end"));

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
