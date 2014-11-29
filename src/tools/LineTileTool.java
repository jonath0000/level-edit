package tools;

import leveledit.DummyObjectFactory;
import levelmodel.Level;

public class LineTileTool implements Tool {
	private long lastMousePressStateSaveTime = 0;
	private boolean firstClick = true;
	private int lastEditedTileX = -1;
	private int lastEditedTileY = -1;
	
	@Override
	public void click(int worldX, int worldY, int tileX, int tileY,
			boolean repeated, Level currentLevel, DummyObjectFactory dummyObjectFactory, 
			TileSelector tileSelector, int currentTileLayer) {
		// to prevent a lot of undo states
		if (!repeated || System.currentTimeMillis() - lastMousePressStateSaveTime > 500) {
			lastMousePressStateSaveTime = System.currentTimeMillis();
			currentLevel.isAboutToAlterState();
		}
		if (firstClick ) {
            firstClick = false;
            currentLevel.getTileMap().setTileVal(tileX, tileY, currentTileLayer, tileSelector.getSelectedIndex());
        }
        else {
        	currentLevel.isAboutToAlterState();
			currentLevel.getTileMap().drawLine(currentTileLayer, lastEditedTileX,
                lastEditedTileY, tileX, tileY, tileSelector.getSelectedIndex());
        }
		
		lastEditedTileX = tileX;
        lastEditedTileY = tileY;
	}
}
