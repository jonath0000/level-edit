package levelmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Tile map with several layers.
 * 
 */
public class TileMap {
	private List<int[][]> maps;

	private static final int FILL_TEMP_VAL = -50000;

	/**
	 * Create new tilemap size w*h with n layers.
	 * 
	 * @param w
	 *            Size x.
	 * @param h
	 *            Size y.
	 * @param n
	 *            Layers.
	 */
	public TileMap(int w, int h, int n) {
		maps = new ArrayList<int[][]>();
		for (int i = 0; i < n; i++) {
			maps.add(new int[h][w]);
		}
	}
	
	/**
	 * Init with copy.
	 * @param tileMapToCopy Deep copy this object.
	 */
	public TileMap(TileMap tileMapToCopy) {
		maps = new ArrayList<int[][]>();
		// ugh... stupid way to do it :P
		for (int i = 0; i < tileMapToCopy.getNumLayers(); i++) {
			int[][] newMap = new int[tileMapToCopy.getHeight()][tileMapToCopy.getWidth()];
			for (int y = 0; y < tileMapToCopy.getHeight(); y++) {
				for (int x = 0; x < tileMapToCopy.getWidth(); x++) {
					newMap[y][x] = tileMapToCopy.getTileVal(x, y, i);
				}
			}
			maps.add(newMap);
		}
	}

	public void deleteTiles(int x, int y, int w, int h, int n) {
		int[][] tiles = getMap(n);
		for (int yIter = y; yIter < y+h; yIter++) {
			for (int xIter = x; xIter < x+w; xIter++) {
				tiles[yIter][xIter] = 0;
			}
		}
	}
	
	public void insertTiles(int x, int y, int[][] tiles, int n) {
		
	}
	
	public int [][] getSubTiles(int x, int y, int w, int h, int n) {
		return null;
	}
	
	/**
	 * Add a blank map to the last index.
	 */
	public void addMap() {
		maps.add(new int[getHeight()][getWidth()]);
	}

	/**
	 * Add given map to the last index.
	 */
	public void addMap(int[][] map) {
		maps.add(map);
	}

	/**
	 * Delete map layer n.
	 * 
	 * @param n
	 *            Layer.
	 */
	public void deleteMap(int n) {
		maps.remove(n);
	}

	/**
	 * Set map array.
	 * 
	 * @param map
	 *            Map data.
	 * @param n
	 *            Layer.
	 */
	public void setMap(int[][] map, int n) {
		maps.set(n, map);
	}

	/**
	 * Get map array for layer.
	 * 
	 * @param n
	 *            Layer.
	 * @return Map data.
	 */
	public int[][] getMap(int n) {
		return maps.get(n);
	}

	/**
	 * Width of map in tiles.
	 * 
	 * @return Width.
	 */
	public int getWidth() {
		return maps.get(0)[0].length;
	}

	/**
	 * Height of map in tiles.
	 * 
	 * @return Height.
	 */
	public int getHeight() {
		return maps.get(0).length;
	}

	/**
	 * Number of layers.
	 * 
	 * @return Number of layers in tile map.
	 */
	public int getNumLayers() {
		return maps.size();
	}

	private boolean checkBounds(int x, int y) {
		int[][] map;
		map = maps.get(0);
		if (x >= 0 && x < map[0].length && y >= 0 && y < map.length)
			return true;
		return false;
	}

	/**
	 * Get tile value at x,y.
	 * 
	 * @param x
	 *            Tile x.
	 * @param y
	 *            Tile y.
	 * @param n
	 *            Layer to get.
	 * @return Value.
	 */
	public int getTileVal(int x, int y, int n) {
		return maps.get(n)[y][x];
	}

	/**
	 * Set tile value at x,y.
	 * 
	 * @param x
	 *            Tile x.
	 * @param y
	 *            Tile y.
	 * @param n
	 *            Layer to get.
	 * @param val
	 *            Value to set.
	 */
	public void setTileVal(int x, int y, int n, int val) {
		if (!checkBounds(x, y))
			return;
		maps.get(n)[y][x] = val;
	}

	private boolean isTileInMap(int x, int y, int[][] map) {
		if (x >= 0 && x < map[0].length && y >= 0 && y < map.length)
			return true;
		return false;
	}

	private boolean markSameValuedNeighbourENWS(int val, int x, int y, int[][] map) {
		boolean found = false;

		if (isTileInMap(x, y + 1, map) && map[y + 1][x] == val) {
			map[y + 1][x] = FILL_TEMP_VAL;
			found = true;
		}
		if (isTileInMap(x, y - 1, map) && map[y - 1][x] == val) {
			map[y - 1][x] = FILL_TEMP_VAL;
			found = true;
		}
		if (isTileInMap(x + 1, y, map) && map[y][x + 1] == val) {
			map[y][x + 1] = FILL_TEMP_VAL;
			found = true;
		}
		if (isTileInMap(x - 1, y, map) && map[y][x - 1] == val) {
			map[y][x - 1] = FILL_TEMP_VAL;
			found = true;
		}
		return found;
	}

	/**
	 * "Flood fill" operation from tile x,y.
	 * 
	 * @param n
	 *            Layer.
	 * @param tx
	 *            Source x.
	 * @param ty
	 *            Source y.
	 * @param oldval
	 *            Value to replace.
	 * @param newval
	 *            Value to insert.
	 */
	public void fill(int n, int tx, int ty, int oldval, int newval) {
		int[][] map;
		map = maps.get(n);

		if (isTileInMap(tx, ty, map)) {
			// mark tiles as belonging to area
			map[ty][tx] = FILL_TEMP_VAL;

			boolean foundOne;
			do {
				foundOne = false;
				for (int xi = 0; xi < map[0].length; xi++) {
					for (int yi = 0; yi < map.length; yi++) {
						if (map[yi][xi] == FILL_TEMP_VAL && markSameValuedNeighbourENWS(oldval, xi, yi, map)) {
							foundOne = true;
						}
					}
				}
			} while (foundOne);

			// change tile values
			for (int xi = 0; xi < map[0].length; xi++) {
				for (int yi = 0; yi < map.length; yi++) {
					if (map[yi][xi] == FILL_TEMP_VAL)
						map[yi][xi] = newval;
				}
			}
		}

	}

	/**
	 * Draw vertical or horizontal line.
	 * 
	 * @param n
	 *            Layer.
	 * @param sx
	 *            Source x.
	 * @param sy
	 *            Source y.
	 * @param dx
	 *            Dest x.
	 * @param dy
	 *            Dest y.
	 * @param val
	 *            Tile value to set.
	 */
	public void drawLine(int n, int sx, int sy, int dx, int dy, int val) {
		int[][] map;
		map = maps.get(n);

		if (dx >= 0 && dx < map[0].length && dy >= 0 && dy < map.length) {
			boolean isVert = false;
			int sign;
			if (Math.abs(sx - dx) < Math.abs(sy - dy))
				isVert = true;

			if (sx == dx && sy == dy) {
				map[sy][sx] = val;
				return;
			}

			if (isVert) {
				sign = dy - sy;
				sign = sign / Math.abs(sign);

				for (int y = sy; y != dy; y += sign) {
					map[y][sx] = val;
				}
			} else {
				sign = dx - sx;
				sign = sign / Math.abs(sign);

				for (int x = sx; x != dx; x += sign) {
					map[sy][x] = val;
				}
			}
		}
	}

}
