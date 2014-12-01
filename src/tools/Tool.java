package tools;

import leveledit.DummyObjectFactory;
import levelmodel.Level;

public interface Tool {
	public void click(int worldX, int worldY, int tileX, int tileY, boolean repeated, 
			Level currentLevel, DummyObjectFactory dummyObjectFactory, 
			TileSelector tileSelector, int currentTileLayer, RectSelecter rectSelecter);
}
