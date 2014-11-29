package tools;

import leveledit.DummyObjectFactory;
import levelmodel.Level;

public class SetTileTool implements Tool {
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
		
		currentLevel.getTileMap().setTileVal(tileX, tileY, currentTileLayer, tileSelector.getSelectedIndex());
	}
}
