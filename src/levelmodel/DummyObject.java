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
	public String additionalData;

	/**
	 * Create a dummy object from new parameters.
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param additionalData
	 * @param name
	 */
	public DummyObject(int x, int y, int w, int h, String additionalData, String name) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.name = name;
		this.additionalData = additionalData;
	}

	/**
	 * Use to clone a dummy object.
	 * 
	 * @param d
	 */
	public DummyObject(DummyObject d) {
		this.x = d.x;
		this.y = d.y;
		this.w = d.w;
		this.h = d.h;
		this.name = d.name;
		this.additionalData = d.additionalData;
	}
	
	public String toString() {
		return name;
	}

}