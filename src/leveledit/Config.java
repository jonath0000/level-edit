package leveledit;

import levelmodel.DummyObject;
import java.awt.Color;
import java.awt.MediaTracker;
import javax.swing.ImageIcon;

/**
 * Represents the current project configuration in a config file.
 * @todo Could/should be done as XML file instead. 
 */
public class Config {
    
    // Hard-coded values.
    
    
    // Values read from file. 
    public ImageIcon tiles;
    public ImageIcon dummyPics;
    public Color bgCol;
    public String typeNames [];
    public DummyObject typeData [];
    
    public Config(String path) {
	System.out.println("Loading config file");
	try {
	    ResFileReader r;
	    r = new ResFileReader(path);
	
	    // tile image path
	    r.gotoPost("tiles", false);
	    r.nextLine();
	    String strTiles = r.getWord();
	    tiles = new ImageIcon(strTiles);
	    if (tiles.getImageLoadStatus() != MediaTracker.COMPLETE) 
                throw new Exception("Couldn't load tile image!");

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
	    
            typeNames = new String [nDummys];
            typeData = new DummyObject [nDummys];
            
	    for (int i = 0; i < nDummys; i++){
		r.nextLine();
		String caption = r.getWord();
		String name = r.getNextWord();
		int w = r.getNextWordAsInt();
		int h = r.getNextWordAsInt();
		boolean hasPic = r.getNextWordAsBool();
		int picX = r.getNextWordAsInt();
		int picY = r.getNextWordAsInt();
		int picW = picX+r.getNextWordAsInt();
		int picH = picY+r.getNextWordAsInt();
		String addData = r.getRestOfLine();
		DummyObject dummy = new DummyObject (
		    1,1,w,h,addData,name,hasPic,picX,picY,picW,picH);
		typeNames[i] = caption;
		typeData[i] = dummy;
	    }

	    // bg color
	    r.gotoPost("bgcol", false);
	    r.nextLine();
	    String hexrgb = r.getWord();
	    bgCol = new Color(Integer.parseInt(hexrgb,16));
	}
	catch (Exception e) {
	    System.out.println("Error reading config file!");
	}
    }
    
}
