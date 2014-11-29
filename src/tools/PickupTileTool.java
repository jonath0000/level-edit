package tools;

import leveledit.DummyObjectFactory;
import levelmodel.Level;

public class PickupTileTool implements Tool {
	long lastMousePressStateSaveTime = 0;
	
	@Override
	public void click(int worldX, int worldY, int tileX, int tileY,
			boolean repeated, Level currentLevel, DummyObjectFactory dummyObjectFactory, 
			TileSelector tileSelector, int currentTileLayer) {
		// to prevent a lot of undo states
		if (!repeated || System.currentTimeMillis() - lastMousePressStateSaveTime > 500) {
			lastMousePressStateSaveTime = System.currentTimeMillis();
			currentLevel.isAboutToAlterState();
		}
		if (currentLevel.getTileMap().getTileVal(tileX, tileY, currentTileLayer) > 0) {
			tileSelector.setSelectedIndex(currentLevel.getTileMap().getTileVal(tileX, tileY, currentTileLayer));
		}
	}
}
