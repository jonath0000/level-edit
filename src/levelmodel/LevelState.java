package levelmodel;

/**
 * State for a level.
 */
public class LevelState {
	private TileMap tileMap;
    private DummyObjectList dummyObjects;
    
    public LevelState(TileMap tileMap, DummyObjectList dummyObjects) {     	
    	this.tileMap = tileMap;
    	this.dummyObjects = dummyObjects;
    }
    
	public LevelState(LevelState stateToCopy) {
		dummyObjects = new DummyObjectList(stateToCopy.getDummyObjects());
		tileMap = new TileMap(stateToCopy.getTileMap());
	}
	
	public DummyObjectList getDummyObjects() {
        return dummyObjects;
    }
	
    public TileMap getTileMap() {
        return tileMap;
    }
}
