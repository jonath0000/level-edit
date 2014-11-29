package tools;

import leveledit.DummyObjectFactory;
import levelmodel.Level;

public class SelectDummyTool implements Tool {

	@Override
	public void click(int worldX, int worldY, int tileX, int tileY,
			boolean repeated, Level currentLevel, DummyObjectFactory dummyObjectFactory, 
			TileSelector tileSelector, int currentTileLayer) {
		if (repeated) return;
		currentLevel.getDummyObjects().selectDummy(worldX, worldY);
	}

}
