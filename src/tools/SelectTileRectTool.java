package tools;

import leveledit.DummyObjectFactory;
import levelmodel.Level;

public class SelectTileRectTool implements Tool {
	
	private int selectX1 = -1;
	private int selectY1 = -1;
	private int selectX2 = -1;
	private int selectY2 = -1;
	
	@Override
	public void click(int worldX, int worldY, int tileX, int tileY,
			boolean repeated, Level currentLevel, DummyObjectFactory dummyObjectFactory, 
			TileSelector tileSelector, int currentTileLayer, RectSelecter rectSelecter) {
		if (repeated) {
			if (selectX1 == -1) selectX1 = tileX;
			if (selectY1 == -1) selectY1 = tileY;
			selectX2 = tileX;
			selectY2 = tileY;
		} else {
			selectX1 = tileX;
			selectY1 = tileY;
			selectX2 = tileX;
			selectY2 = tileY;
		}
		rectSelecter.setSelectedRect(selectX1, selectY1, selectX2, selectY2);
	}
}
