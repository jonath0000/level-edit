package levelmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds all DummyObjectList and manages them in a coordinate system.
 * 
 */
public class DummyObjectList {

    /**
     * Storage of objects.
     */
    private List<DummyObject> dummyObjects = new ArrayList<DummyObject>();
    
    /**
     * Current selection.
     */
    private DummyObject selectedDummy;

    /**
     * Get selected dummy.
     * @return
     */
    public DummyObject getSelected() {
        return selectedDummy;
    }

    /**
     * Get specific index in list.
     * @param i Index.
     * @return DummyObject at index.
     */
    public DummyObject elementAt(int i) {
        return dummyObjects.get(i);
    }

    /**
     * Size of list.
     * @return Size.
     */
    public int size() {
        return dummyObjects.size();
    }
    
    
    /**
     * Delete dummy.
     * @param d DummyObject to delete.
     */
    private void deleteDummy(DummyObject d) {
        
        // handle getSelected dummy
        if (d == selectedDummy) {
            if (dummyObjects.size() == 1) {
                selectedDummy = null;
            } else if (dummyObjects.size() > 1) {
                selectNextDummy();
            } else {
                return;
            }
        }
        
        // delete from list
        dummyObjects.remove(d);
    }

    /**
     * Dummy object at x, y.
     * @param x X coord.
     * @param y X coord.
     * @return DummyObject at coordinate or null.
     */
    private DummyObject dummyAt(int x, int y) {

        for (int i = dummyObjects.size() - 1; i >= 0; i--) {
            // backwards so top-viewed will be chosen
            DummyObject d = (DummyObject) dummyObjects.get(i);
            if (x > d.x - x && x < d.x + d.w - x
                    && y > d.y - y && y < d.y + d.h - y) {
                return d;
            }
        }
        
        System.out.println("No dummy at " + x + "," + y);
        return null;
    }
    
    /**
     * Select dummy at x, y if any.
     * @param x Mouse x
     * @param y Mouse y
     */
    public void selectDummy(int x, int y) {
        
        DummyObject d = dummyAt(x, y);
        if (d != null) {
            selectedDummy = d;
        }
    }
    
    /**
     * Delete dummy at x, y if any.
     * @param x Mouse x
     * @param y Mouse y
     */
    public void deleteDummy(int x, int y) {
        
        DummyObject d = dummyAt(x, y);
        if (d != null) {
            deleteDummy(d);
        }
    }

    /**
     * Moves a dummy
     * @param x Adds x to dummys xpos
     * @param y Adds y to dummys ypos
     */
    public void moveSelectedDummy(int x, int y) {
        if (selectedDummy != null) {
            selectedDummy.x += x;
            selectedDummy.y += y;
        }
    }

    /**
     * Selects next dummy in the dummy list.
     */
    public void selectNextDummy() {
        if (selectedDummy != null) {
            int size = dummyObjects.size();
            int index = dummyObjects.indexOf(selectedDummy);
            if (index + 1 >= size) {
                selectedDummy = (DummyObject) dummyObjects.get(0);
            } else {
                selectedDummy = (DummyObject) dummyObjects.get(index + 1);
            }
        }
    }

    /**
     * Select previous dummy in the dummy list
     */
    public void selectPrevDummy() {
        if (selectedDummy != null) {
            int size = dummyObjects.size();
            int index = dummyObjects.indexOf(selectedDummy);
            if (index - 1 < 0) {
                selectedDummy = (DummyObject) dummyObjects.get(size - 1);
            } else {
                selectedDummy = (DummyObject) dummyObjects.get(index - 1);
            }
        }
    }

    /**
     * Adds a new dummy d to the dummylist
     * @param d A new created dummy object
     */
    public void newDummy(DummyObject d) {
        dummyObjects.add(d);
        selectedDummy = d;
    }


    /**
     * Adds a dummy to the dummylist, but also sets its
     * x,y pos explicitly.
     * @param x Set dummy x to this
     * @param y Set dummy y to this
     * @param d A new created dummy
     */
    public void newDummy(int x, int y, DummyObject d) {
        d.x = x;
        d.y = y;
        newDummy(d);
    }
    
    /**
     * Removes the currently getSelected dummy from the dummy list
     */
    public void deleteSelectedDummy() {
        deleteDummy(selectedDummy);
    }

    /**
     * Flushes the dummyobject list
     */
    public void flushData() {
        dummyObjects = new ArrayList<DummyObject>();
    }
    
}
