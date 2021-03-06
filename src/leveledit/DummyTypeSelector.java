package leveledit;

import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import levelmodel.DummyObject;

/**
 * Frame for choosing one of the available dummy types, and creating new
 * DummyObjects.
 * 
 */
public class DummyTypeSelector extends JScrollPane implements DummyObjectFactory {

	private JList<Object> typeList;

	public DummyTypeSelector() {
		typeList = new JList<>();
		typeList.setVisibleRowCount(3);
		typeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setViewportView(typeList);
	}

	public void setDummyList(ArrayList<DummyObject> typeData) {
		typeList.setListData(typeData.toArray());
	}

	public DummyObject createNewDummyObject() {
		return new DummyObject((DummyObject) typeList.getSelectedValue());
	}

	@Override
	public DummyObject createDummyFromSelected() {
		DummyObject dummy = (DummyObject) typeList.getSelectedValue();
		if (dummy != null) {
			return new DummyObject(dummy);
		}
		return null;
	}

}
