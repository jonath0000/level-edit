package leveledit;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import levelmodel.DummyObject;

/**
 * Frame for choosing one of the available dummy types, 
 * and creating new DummyObjects.
 * 
 */
public class DummyTypeSelector extends JScrollPane {
    
    private JList typeList;
    public DummyObject typeData [] = new DummyObject [1];
    
    
    public DummyTypeSelector() {
        
	typeList = new JList();
	typeList.setVisibleRowCount(3);
	typeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION) ;        
	setViewportView(typeList);
    }
    
    public void setDummyList(String typeNames[], DummyObject typeData[]) {
        typeList.setListData(typeNames);
        this.typeData = typeData;
    }    
    
    public DummyObject createNewDummyObject() {
        return new DummyObject(typeData[typeList.getSelectedIndex()]);
    }
    
}