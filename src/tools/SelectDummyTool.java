package tools;

import leveledit.DummyObjectFactory;
import levelmodel.DummyObject;
import levelmodel.Level;

public class SelectDummyTool implements Tool {

	
	@Override
	public void click(int worldX, int worldY, int tileX, int tileY,
			boolean repeated, Level currentLevel, DummyObjectFactory dummyObjectFactory, 
			TileSelector tileSelector, int currentTileLayer) {
		if (repeated) {
			DummyObject clickedDummy = currentLevel.getDummyObjects().getSelected();
			if (clickedDummy != null) {
				if (!(worldX < clickedDummy.x || worldX > clickedDummy.x + clickedDummy.w
						|| worldY < clickedDummy.y || worldY > clickedDummy.y + clickedDummy.h)) {
					clickedDummy.x = worldX - clickedDummy.w/2;
					clickedDummy.y = worldY - clickedDummy.h/2;
				}
			}
		} else {
			currentLevel.getDummyObjects().selectDummy(worldX, worldY);
		}
	}

}
