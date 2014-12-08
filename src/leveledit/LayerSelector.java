package leveledit;

import java.util.List;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import levelmodel.TileMap;

public class LayerSelector extends JScrollPane 
	implements ListSelectionListener {

	public interface SelectedLayerChangeListener {
		public void onSelectedLayerChanged();
	}
	
	private JList<Object> layerList;
	private SelectedLayerChangeListener listener;
	
	public LayerSelector(SelectedLayerChangeListener listener) {
		this.listener = listener;
		layerList = new JList<>();
		layerList.setVisibleRowCount(3);
		layerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setViewportView(layerList);
		layerList.addListSelectionListener(this);
	}
	
	public TileMap.TileMapLayer getSelected() {
		return (TileMap.TileMapLayer)layerList.getSelectedValue();
	}
	
	public int getSelectedIndex() {
		return layerList.getSelectedIndex();
	}
	
	public void onLayersChanged(TileMap currentTileMap) {
		TileMap.TileMapLayer tempSelected = (TileMap.TileMapLayer)layerList.getSelectedValue();
		List<TileMap.TileMapLayer> layersTemp = currentTileMap.getLayers();
		layerList.setListData(layersTemp.toArray());
		layerList.setSelectedIndex(layersTemp.indexOf(tempSelected) >= 0 ? layersTemp.indexOf(tempSelected) : 0);
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (listener != null) {
			listener.onSelectedLayerChanged();
		}
	}
	
	
}
