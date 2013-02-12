package levelfileformats;

import java.io.FileOutputStream;
import levelmodel.DummyObject;
import levelmodel.DummyObjectList;
import leveledit.LevelFileInterface;
import levelmodel.TileMap;

/**
 *
 * Blocko rev. 2 file format.
 * 
 * <p>
 * Byte 1 is number of entities.
 * <p>
 * Then entity data: ID, posX, posY (shorts, 6 bytes total).
 * <p>
 * Then map x,y (2 bytes).
 * <p>
 * Then x*y*2 bytes of tile values. (2 layers)
 * <p>TODO: Modify this method to support your own game file format instead of "Blocko".
 */
public class Blocko2LevelFile 
implements LevelFileInterface {
    
    private String path;
    
    /**
     * Is created for specific file.
     */
    public Blocko2LevelFile(String path) {
        this.path = path;
    }
    
    /**
     * Write level to file.
     * 
     * @return true if ok.
     */
    @Override
    public boolean write(DummyObjectList dummyObjects, TileMap tileMap) {
        try {
	
	    FileOutputStream f = new FileOutputStream(path);

	    // byte 1:
	    // number of objects
	    f.write((byte)dummyObjects.size());

	    // byte 2 -> 2+(byte 1)*12 (12 bytes per object)
	    // objects data
	    // 
	    DummyObject p;
	    for (int i = 0; i < dummyObjects.size(); i++)
	    {
		p = (DummyObject)dummyObjects.elementAt(i);
				
		// byte 1,2 - code
		String id = new String();
		if (p.name.length() == 1) id = p.name + " ";
		else id = p.name.substring(0,2);
		f.write( id.getBytes() );
		
		// byte 3,4 - xpos
		f.write( (byte)( (int)p.x));
		f.write( (byte)( (int)p.x>>8));

		// byte 5,6 - ypos
		f.write( (byte)( (int)p.y));
		f.write( (byte)( (int)p.y>>8));

		// byte 7...12 (6 byte)
		f.write((byte)p.additionalData.charAt(0));
		f.write((byte)p.additionalData.charAt(1));
		f.write((byte)p.additionalData.charAt(2));
		f.write((byte)p.additionalData.charAt(3));
		f.write((byte)p.additionalData.charAt(4));
		f.write((byte)p.additionalData.charAt(5));
	    }	   	    
	    
	    
	    // map array
	    // byte 1,2 w,h
	    int w = tileMap.getWidth();
	    int h = tileMap.getHeight();	 
	    f.write(w);
	    f.write(h);
	    int maxtMsbTile = 0;
	    // next w*h bytes data  
	    for (int j = 0; j < h; j++)
	    {
		for (int i = 0; i < w; i++)
		{
		    char tileLsb = (char) tileMap.getTileVal(i, j, 0);
		    char tileMsb = (char) (tileMap.getTileVal(i, j, 0) >> 8);
		    if (tileLsb == 1) tileLsb = 0;
                    //f.write(tile);
		    
		    f.write(tileMsb);
		    f.write(tileLsb);

		    System.out.println(
			"Original value: "
			+ Integer.toString(tileMap.getTileVal(i, j, 0))
			+" Wrote tile: "
			+" LSB: " 
			+ Integer.toString((int)tileLsb)
			+ " MSB: "
			+ Integer.toString((int)tileMsb));
		    
		}
	    }
            // next w*h bytes data  
	    for (int j = 0; j < h; j++)
	    {
		for (int i = 0; i < w; i++)
		{
		    char tileLsb = (char) tileMap.getTileVal(i, j, 1);
		    char tileMsb = (char) (tileMap.getTileVal(i, j, 1) >> 8);
		    if (tileLsb == 1) tileLsb = 0;
		    f.write(tileMsb);
		    f.write(tileLsb);
		}
	    }
	    
	    f.close();	    
	} catch (Exception e) 
	{
	    System.out.println(e.toString());
	}
        return true;
    }
    
    /**
     * Not supported.
     * @return false.
     */
    @Override
    public boolean read(DummyObjectList dummyObjects, DummyObject [] dummyTypes, 
        TileMap tileMap) {
        return false;
    }   
}