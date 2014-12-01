package tools;

import leveledit.DummyObjectFactory;
import levelmodel.Level;

public class DeleteDummyTool implements Tool {

	@Override
	public void click(int worldX, int worldY, int tileX, int tileY,
			boolean repeated, Level currentLevel, DummyObjectFactory dummyObjectFactory, 
			TileSelector tileSelector, int currentTileLayer, RectSelecter rectSelecter) {
		if (repeated) return;
		currentLevel.isAboutToAlterState();
		currentLevel.getDummyObjects().deleteDummy(worldX, worldY);
	}

}
