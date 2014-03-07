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
public class DummyTypeSelector extends JScrollPane {

	private JList typeList;

	public DummyTypeSelector() {
		typeList = new JList();
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

}
