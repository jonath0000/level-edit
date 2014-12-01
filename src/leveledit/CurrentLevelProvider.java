package leveledit;

import levelmodel.Level;

public interface CurrentLevelProvider {
	public Level getLevel();
	public int getSelectedLayer();
}
