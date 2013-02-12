package levelfileformats;

import levelmodel.DummyObject;
import levelmodel.DummyObjectList;
import leveledit.LevelFileInterface;
import leveledit.ResFileReader;
import levelmodel.TileMap;

/**
 *
 *  Mappy file format.
 */
public class MappyLevelFile 
implements LevelFileInterface {
    
    private String path;
    
    /**
     * Is created for specific file.
     */
    public MappyLevelFile(String path) {
        this.path = path;
    }
    
    /**
     * Not supported.
     * 
     * @return true if ok.
     */
    @Override
    public boolean write(DummyObjectList dummyObjects, TileMap tileMap) {        
        return false;
    }
    
    /**
     * Read a level into memory.
     * @return true if ok.
     */
    @Override
    public boolean read(DummyObjectList dummyObjects, DummyObject [] dummyTypes, 
        TileMap tileMap) {
        // import from Mappy 
  
	System.out.println("Importing Mappy .txt file");
	try {
	    ResFileReader r;
	    r = new ResFileReader(path);

	    // 1: const short apa_map0[8][100] = {
	    r.getNextWord();
	    String tmp = r.getNextWord();
	    String tmp2 = tmp.substring(tmp.indexOf("[")+1,tmp.indexOf("]"));
	    int height = Integer.parseInt(tmp2);
	    String tmp3 = tmp.substring(tmp.lastIndexOf("[")+1,tmp.lastIndexOf("]"));
	    int len = Integer.parseInt(tmp3);
	    tileMap.setMap(new int [height][len], 0);	

	    // 2: "{" <tiledata>, | \r\n ... "},"
		
	    r.nextLine();  	   
	    for (int i = 0; i < height; i++) {
		int c = 0;
		for (int j = 0; j < len+2; j++) {			

		    try {
			String str = r.getNextWord();

			// ignore start brace
			if (str.compareTo("{")==0){
			    str = r.getNextWord();
			}

			// check if row end brace
			if (str.length() > 1 && 
			    (str.substring(0,2).compareTo("},")==0) ||
			    (str.compareTo("}")==0)
			    ){
			    j = len+10;
			    r.nextLine(true);
			}

			// we're ok: parse number
			else{
			    // number can have comma or not
			    if (str.lastIndexOf(",") != -1){
				str = str.substring(0,str.lastIndexOf(","));
			    }

			    tileMap.setTileVal(j, i, 0, Integer.parseInt(str));
			}

			// eol ecountered, continue row
		    } catch (Exception e) {
			r.nextLine(true);
			String str = r.getWord();
			if (str.compareTo("{")==0){
			    str = r.getNextWord();
			}
			str = str.substring(0,str.lastIndexOf(","));
			tileMap.setTileVal(j, i, 0, Integer.parseInt(str));
		    }
		}
	    }
	} catch (Exception e){ return false;}
	

	System.out.println("Init finished");
        return true;
    }   
}