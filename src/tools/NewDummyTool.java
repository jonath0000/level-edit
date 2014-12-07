package tools;

import leveledit.DummyObjectFactory;
import levelmodel.DummyObject;
import levelmodel.Level;

public class NewDummyTool implements Tool {

	@Override
	public void click(int worldX, int worldY, int tileX, int tileY,
			boolean repeated, Level currentLevel, DummyObjectFactory dummyObjectFactory,
			TileSelector tileSelector, int currentTileLayer, RectSelecter rectSelecter) {
		if (repeated) return;
		DummyObject d = dummyObjectFactory.createDummyFromSelected();
		if (d != null) {
			currentLevel.isAboutToAlterState();
			currentLevel.getDummyObjects().newDummy(worldX, worldY, d);
		}
	}

}
