package levelmodel;

import java.util.Vector;

/**
 * Holds all DummyObjectList, draws and manages them.
 * 
 */
public class DummyObjectList {

    public Vector<DummyObject> dummyObjects = new Vector<DummyObject>();
    public DummyObject selectedDummy;
    private int w;
    private int h;
    private int newXPos;

    public void DummyObjects(int w, int h) {
        this.w = w;
        this.h = h;
        this.newXPos = 0;
    }

    public DummyObject selected() {
        return selectedDummy;
    }

    public DummyObject elementAt(int i) {
        return dummyObjects.elementAt(i);
    }

    public int size() {
        return dummyObjects.size();
    }

    public int getWidth() {
        return w;
    }

    public int getHeight() {
        return h;
    }

    /**
     * Camera position x
     * @return camX
     */
    public int getScrollX() {
        return 0;//camX;
    }

    /**
     * Camera position y
     * @return camY
     */
    public int getScrollY() {
        return 0;//camY;
    }

    /**
     * Select dummy from mouse click
     * @param x Mouse x
     * @param y Mouse y
     */
    public void selectDummy(int x, int y) {
        int sx = getScrollX();
        int sy = getScrollY();

        for (int i = dummyObjects.size() - 1; i >= 0; i--) {
            // backwards so top-viewed will be chosen
            DummyObject d = (DummyObject) dummyObjects.elementAt(i);
            if (x > d.x - sx && x < d.x + d.w - sx
                    && y > d.y - sy && y < d.y + d.h - sy) {
                selectedDummy = d;
            }
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
                selectedDummy = (DummyObject) dummyObjects.elementAt(0);
            } else {
                selectedDummy = (DummyObject) dummyObjects.elementAt(index + 1);
            }
        } else {
            System.out.println("Create a dummy first!");
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
                selectedDummy = (DummyObject) dummyObjects.elementAt(size - 1);
            } else {
                selectedDummy = (DummyObject) dummyObjects.elementAt(index - 1);
            }
        } else {
            System.out.println("Create a dummy first!");
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
     * Adds a new dummy to the dummylist, but also moves it to 
     * "ground level".
     * @param d A new created dummy.
     */
    public void newDummyCommand(DummyObject d) {
        // find "ground" level
        d.y = getScrollY() + this.getHeight() - d.h - 40;
        int x = getScrollX();
        d.x = x + newXPos;

        newDummy(d);
    }

    /**
     * Adds a dummy to the dummylist, but also sets its
     * x,y pos explicitly.
     * @param x Set dummy x to this
     * @param y Set dummy y to this
     * @param d A new created dummy
     */
    public void newDummy(int x, int y, DummyObject d) {
        d.x = x + getScrollX();
        d.y = y + getScrollY();
        newDummy(d);
    }

    /**
     * Removes the currently selected dummy from the dummy list
     */
    public void deleteSelectedDummy() {
        DummyObject d = selectedDummy;
        if (dummyObjects.size() == 1) {
            selectedDummy = null;
        } else if (dummyObjects.size() > 1) {
            selectNextDummy();
        } else {
            return;
        }

        dummyObjects.remove(d);

    }

    /**
     * Flushes the dummyobject list
     */
    public void flushData() {
        dummyObjects = new Vector<DummyObject>();
    }
    
}
