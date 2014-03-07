package leveledit;

import levelmodel.DummyObject;
import levelmodel.Level;

public interface LevelEditComponentsAccessor {
	public int getSelectedTileIndex();
	public void setSelectedTileIndex(int index);
	public DummyObject createNewDummyFromSelectedType();
	public Level getLevelModel();
	public ToolSelector.Tool getSelectedTool();
	public Config getConfig();
	public ImageStore getImageStore();
}
