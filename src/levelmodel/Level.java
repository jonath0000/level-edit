package levelmodel;

/**
 * Level contains a tilemap and a list of dummy objects.
 * 
 */
public class Level {
	
	private LevelStateHistory levelStateHistory;

    /**
     * Create level with some default values.
     */
    public Level() {
    	levelStateHistory = new LevelStateHistory(new LevelState(new TileMap(30, 30, 2), new DummyObjectList()));
    }            
    
    /**
     * Using class should notify before state changes, i.e. dummys are changed or
     * tilemap is changed. This will save the state for undo.
     */
    public void isAboutToAlterState() {    	
    	levelStateHistory.newState();
    }
    
    /**
     * Move to previous state.
     */
    public void undo() {
    	levelStateHistory.undo();
    }
    
    /**
     * Get the dummy objects.
     * @return Dummy objects for this level.
     */
    public DummyObjectList getDummyObjects() {
        return levelStateHistory.getCurrentState().getDummyObjects();
    }

    /**
     * Get the tilemap object.
     * @return TileMap object.
     */
    public TileMap getTileMap() {
        return levelStateHistory.getCurrentState().getTileMap();
    }
    
    /**
     * Init level from a specified file.
     * @param lf Level file format.
     * @param dummyTypes  
     */
    public void initFromFile(LevelFileInterface lf, DummyObject [] dummyTypes) {
        
    	levelStateHistory.newState(new LevelState(new TileMap(10, 10, 1), new DummyObjectList()));
        lf.read(levelStateHistory.getCurrentState().getDummyObjects(), 
        		dummyTypes, 
        		levelStateHistory.getCurrentState().getTileMap());
    }

    /**
     * Creates a blank map.
     * 
     * @param x width of new map.
     * @param y height of new map.
     */
    public void initBlankMap(int x, int y) {

    	levelStateHistory.newState(new LevelState(new TileMap(x, y, 2), new DummyObjectList()));
    }    
    
    /**
     * Save level to file.
     * @param lf Specifies file format.
     */
    public void writeToFile(LevelFileInterface lf) {
        lf.write(levelStateHistory.getCurrentState().getDummyObjects(), levelStateHistory.getCurrentState().getTileMap());
    }
}
