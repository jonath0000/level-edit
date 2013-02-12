package leveledit;

import levelmodel.DummyObject;
import levelmodel.DummyObjectList;
import levelmodel.TileMap;

/**
 * Abstract level file class to represent any I/O format. 
 */
public interface LevelFileInterface {
        
    /**
     * Write level to file.
     * 
     * @return true if ok.
     */
    public boolean write(DummyObjectList dummyObjects, TileMap tileMap);
    
    /**
     * Read a level into memory.
     * @return true if ok.
     */
    public boolean read(DummyObjectList dummyObjects, DummyObject [] dummyTypes, 
            TileMap tileMap);
}
