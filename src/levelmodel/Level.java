package levelmodel;

/**
 * Level contains a tilemap and a list of dummy objects.
 * 
 */
public class Level {
    private TileMap tileMap;
    private DummyObjectList dummyObjects;

    public DummyObjectList getDummyObjects() {
        return dummyObjects;
    }

    public void setDummyObjects(DummyObjectList dummyObjects) {
        this.dummyObjects = dummyObjects;
    }

    public TileMap getTileMap() {
        return tileMap;
    }

    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }
    
}
