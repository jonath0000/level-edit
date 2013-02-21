package levelmodel;

/**
 * Level contains a tilemap and a list of dummy objects.
 * 
 */
public class Level {
    private TileMap tileMap;
    private DummyObjectList dummyObjects;

    /**
     * Create level with some default values.
     */
    public Level() {
        dummyObjects = new DummyObjectList();
        tileMap = new TileMap(30, 30, 2);
    }            
    
    /**
     * Get the dummy objects.
     * @return Dummy objects for this level.
     */
    public DummyObjectList getDummyObjects() {
        return dummyObjects;
    }

    /**
     * Set the dummy objects.
     * @param dummyObjects Dummy objects.
     */
    public void setDummyObjects(DummyObjectList dummyObjects) {
        this.dummyObjects = dummyObjects;
    }

    /**
     * Get the tilemap object.
     * @return TileMap object.
     */
    public TileMap getTileMap() {
        return tileMap;
    }

    /**
     * Set tilemap object.
     * @param tileMap TileMap object.
     */
    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }
    
    /**
     * Init level from a specified file.
     * @param lf Level file format.
     * @param dummyTypes  
     */
    public void initFromFile(LevelFileInterface lf, DummyObject [] dummyTypes) {
        
        dummyObjects.flushData();
        tileMap = new TileMap(10, 10, 1);
        lf.read(dummyObjects, dummyTypes, tileMap);
    }

    /**
     * Creates a blank map.
     * 
     * @param x width of new map.
     * @param y height of new map.
     */
    public void initBlankMap(int x, int y) {

        dummyObjects.flushData();
        tileMap = new TileMap(x, y, 2);
    }    
    
    /**
     * Save level to file.
     * @param lf Specifies file format.
     */
    public void writeToFile(LevelFileInterface lf) {
        lf.write(dummyObjects, tileMap);
    }
}
