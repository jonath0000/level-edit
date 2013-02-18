package levelmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Tile map with several layers.
 * 
 */
public class TileMap {
    private List<int[][]> maps; 
    
    /**
     * Create new tilemap size w*h with n layers.
     * @param w Size x.
     * @param h Size y.
     * @param n Layers.
     */
    public TileMap(int w, int h, int n) {
        maps = new ArrayList();
        for(int i = 0; i < n; i++){
            maps.add(new int[h][w]);
        }
    }
    
    /**
     * Set map array.
     * @param map Map data.
     * @param n Layer.
     */
    public void setMap(int [][] map, int n) {
        maps.set(n, map);
    }
    
    /**
     * Get map array for layer.
     * @param n Layer.
     * @return Map data.
     */
    public int [][] getMap(int n) {
        return maps.get(n);
    }
    
    /**
     * Width of map in tiles.
     * @return Width.
     */
    public int getWidth() {
        return maps.get(0)[0].length;
    }
    
    /**
     * Height of map in tiles.
     * @return Height.
     */
    public int getHeight() {
        return maps.get(0).length;
    }
            
    
    private boolean checkBounds(int x, int y) {
        int [][] map;
        map = maps.get(0);
        if (x >= 0 && x < map[0].length && y >= 0 && y < map.length)
            return true;
        return false;
    }
    
    /**
     * Get tile value at x,y.
     * @param x Tile x.
     * @param y Tile y.
     * @param n Layer to get.
     * @return Value.
     */
    public int getTileVal(int x, int y, int n) {
        return maps.get(n)[y][x];
    }
    
    /**
     * Set tile value at x,y.
     * @param x Tile x.
     * @param y Tile y.
     * @param n Layer to get.
     * @param val Value to set.
     */
    public void setTileVal(int x, int y, int n, int val) {       
        if (!checkBounds(x, y)) return;
        maps.get(n)[y][x] = val;
    }
   
    
    /**
     * "Flood fill" operation from tile x,y.
     * @todo Will get stack overflow with this method!
     * 
     * @param n      Layer.
     * @param tx     Source x.
     * @param ty     Source y.
     * @param oldval Value to replace.
     * @param newval Value to insert.
     */
    public void fillRecursive(int n, int tx, int ty, int oldval, int newval)
    {
        int [][] map;
        map = maps.get(n);
        
	if (tx >= 0 && tx < map[0].length && ty >= 0 && ty < map.length)
	{	
	    if(map[ty][tx] == oldval)
	    {
		map[ty][tx] = newval;
		fillRecursive(n, tx+1, ty,   oldval, newval);
		fillRecursive(n, tx-1, ty,   oldval, newval);
		fillRecursive(n, tx,   ty+1, oldval, newval);
		fillRecursive(n, tx,   ty-1, oldval, newval);
	    }
	}
    }

    /**
     * Draw vertical or horizontal line.
     * 
     * @param n  Layer.
     * @param sx Source x.
     * @param sy Source y.
     * @param dx Dest x.
     * @param dy Dest y.
     * @param val Tile value to set.
     */
    public void drawLine(int n, int sx, int sy, int dx, int dy, int val)
    {
        int [][] map;
        map = maps.get(n);
        
	if (dx >= 0 && dx < map[0].length && dy >= 0 && dy < map.length)
	{	
	    boolean isVert = false;
	    int sign;
	    if (Math.abs(sx-dx) < Math.abs(sy-dy))
		isVert = true;	   

	    if (sx == dx && sy == dy) 
	    {
		map[sy][sx] = val;
		return;
	    }

	    if (isVert)
	    {
		sign = dy - sy;
		sign = sign/Math.abs(sign);

		for (int y = sy; y != dy; y+=sign)
		{
		    map[y][sx] = val;
		}	
	    }
	    else
	    {
		sign = dx - sx;
		sign = sign/Math.abs(sign);
		
		for (int x = sx; x != dx; x+=sign)
		{
		    map[sy][x] = val;
		}	       
	    }
	}
    }

    
}
