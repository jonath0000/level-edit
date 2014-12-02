package leveledit;


public class FloatingLayer {
	private int[][] tiles;
	private int posX;
	private int posY;
	
	public FloatingLayer(int[][] tiles, int posX, int posY) {
		this.tiles = tiles;
		this.posX = posX;
		this.posY = posY;
	}
	
	public int getPosX() {
		return posX;
	}
	
	public int getPosY() {
		return posY;
	}
	
	public int[][] getTiles() {
		return tiles;
	}
	
	public void setCenter(int x, int y) {
		this.posX = x - tiles[0].length/2;
		this.posY = y - tiles.length/2;
	}
}
