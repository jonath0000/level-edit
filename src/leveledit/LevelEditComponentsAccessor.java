package leveledit;

import tools.TileSelector;
import tools.Tool;
import levelmodel.Level;

public interface LevelEditComponentsAccessor {
	public TileSelector getTileSelector();
	public DummyObjectFactory getDummyObjectFactory();
	public Level getLevelModel();
	public Tool getSelectedTool();
	public Config getConfig();
	public ImageStore getImageStore();
	public ApplicationClipBoard getClipBoard();
}
