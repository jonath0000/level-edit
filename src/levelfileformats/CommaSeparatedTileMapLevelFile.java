package levelfileformats;

import java.io.PrintWriter;

import levelmodel.DummyObject;
import levelmodel.DummyObjectList;
import levelmodel.LevelFileInterface;
import levelmodel.TileMap;

/**
 * Comma separated export of only the tile map.
 *
 */
public class CommaSeparatedTileMapLevelFile implements LevelFileInterface {

	private String path;
    
    /**
     * Is created for specific file.
     */
    public CommaSeparatedTileMapLevelFile(String path) {
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
			PrintWriter out = new PrintWriter(path);
			for (int layer = 0; layer < tileMap.getNumLayers(); layer++) {
				out.print(tileMap.getWidth());
				out.print("," + tileMap.getHeight());
	
				for (int j = 0; j < tileMap.getHeight(); j++) {
					for (int i = 0; i < tileMap.getWidth(); i++) {
						out.print("," + tileMap.getTileVal(i, j, layer));
					}
				}
				out.print("\n");
			}
			out.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return true;
	}

    /**
     * Not supported.
     * @return false.
     */
	@Override
	public boolean read(DummyObjectList dummyObjects, DummyObject[] dummyTypes,
			TileMap tileMap) {
		return false;
	}

}
