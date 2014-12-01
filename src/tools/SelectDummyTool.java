package tools;

import leveledit.DummyObjectFactory;
import levelmodel.DummyObject;
import levelmodel.Level;

public class SelectDummyTool implements Tool {

	DummyObject movedDummy = null;
	
	@Override
	public void click(int worldX, int worldY, int tileX, int tileY,
			boolean repeated, Level currentLevel, DummyObjectFactory dummyObjectFactory, 
			TileSelector tileSelector, int currentTileLayer, RectSelecter rectSelecter) {
		if (repeated) {
			if (movedDummy == null) {
				currentLevel.isAboutToAlterState();
			}
			movedDummy = currentLevel.getDummyObjects().getSelected();
			if (movedDummy != null) {
				if (!(worldX < movedDummy.x || worldX > movedDummy.x + movedDummy.w
						|| worldY < movedDummy.y || worldY > movedDummy.y + movedDummy.h)) {
					movedDummy.x = worldX - movedDummy.w/2;
					movedDummy.y = worldY - movedDummy.h/2;
				}
			}
		} else {
			currentLevel.getDummyObjects().selectDummy(worldX, worldY);
			movedDummy = null;
		}
	}

}
