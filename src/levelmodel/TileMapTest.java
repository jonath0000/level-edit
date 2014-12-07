package levelmodel;

import static org.junit.Assert.*;

import org.junit.Test;

public class TileMapTest {

	@Test
	public void testResize() {
		TileMap tileMap = new TileMap(10, 10, 3);
		tileMap.getMap(1)[3][3] = 3;
		tileMap.getMap(1)[7][7] = 7;
		
		tileMap.resize(0, 0, 0, 3);
		assertEquals(3, tileMap.getMap(1)[3][3]);
		assertEquals(7, tileMap.getMap(1)[7][7]);
		assertEquals(10, tileMap.getWidth());
		assertEquals(13, tileMap.getHeight());
		
		tileMap.resize(0, 0, 2, 0);
		assertEquals(3, tileMap.getMap(1)[5][3]);
		assertEquals(7, tileMap.getMap(1)[9][7]);
		assertEquals(10, tileMap.getWidth());
		assertEquals(15, tileMap.getHeight());
		
		tileMap.resize(1, 2, 0, 0);
		assertEquals(3, tileMap.getMap(1)[5][4]);
		assertEquals(7, tileMap.getMap(1)[9][8]);
		assertEquals(13, tileMap.getWidth());
		assertEquals(15, tileMap.getHeight());
		
		tileMap.resize(-3, 0, 0, 0);
		assertEquals(3, tileMap.getMap(1)[5][1]);
		assertEquals(7, tileMap.getMap(1)[9][5]);
		assertEquals(10, tileMap.getWidth());
		assertEquals(15, tileMap.getHeight());
	}

}
