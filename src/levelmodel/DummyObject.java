package levelmodel;
/**
 * A game entity in the editor.
 * <p>
 * Has x,y & type to be used in actual exported map data.
 * <p>
 * Also has data how the picture etc, is shown in the editor.
 * 
 */
public class DummyObject {

    public String name;
    public int x, y, w, h;
    public boolean pic = true;
    public int picX,picY,picW,picH;
    public String additionalData;

    /**
     * Create a dummy object from new parameters.
     * @param x
     * @param y
     * @param w
     * @param h
     * @param additionalData
     * @param name
     * @param pic
     * @param picX
     * @param picY
     * @param picW
     * @param picH
     */
    public DummyObject (
	int x, 
	int y, 
	int w, 
	int h, 
	String additionalData, 
	String name, 
	boolean pic, 
	int picX, 
	int picY, 
	int picW, 
	int picH)
    {
	this.x = x; 
	this.y = y; 
	this.w = w; 
	this.h= h; 
	this.name = name; 
	this.additionalData = additionalData;
	this.picX = picX; 
	this.picY = picY; 
	this.picW = picW; 
	this.picH= picH; 
	this.pic = pic;
    }

    /**
     * Use to clone a dummyobject.
     * @param d
     */
    public DummyObject (DummyObject d) {
	this.x = d.x; 
	this.y = d.y; 
	this.w = d.w; 
	this.h= d.h; 
	this.name = d.name;
	this.picX = d.picX; 
	this.picY = d.picY; 
	this.picW = d.picW; 
	this.picH= d.picH; 
	this.pic = d.pic;
	this.additionalData = d.additionalData;
    }

}