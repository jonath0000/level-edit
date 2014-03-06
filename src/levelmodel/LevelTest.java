package levelmodel;

import static org.junit.Assert.*;

import org.junit.Test;

public class LevelTest {

	@Test
	public void testDummyUndoHistory() {
		DummyObject dummy1 = new DummyObject(0, 0, 10, 10, "none", "dummy1", true, 0, 0, 10, 10);
		DummyObject dummy2 = new DummyObject(20, 20, 200, 200, "adddata", "dummy2", true, 20, 20, 20, 20);
		DummyObject dummy3 = new DummyObject(3000, 0, 3000, 3000, "", "dummy3", true, 90, 80, 70, 60);	
		Level level = new Level();
		level.initBlankMap(10, 10);
		
		level.getDummyObjects().newDummy(dummy1);
		level.getDummyObjects().newDummy(dummy2);
		level.getDummyObjects().newDummy(dummy3);
		
		level.getDummyObjects().selectDummy(4, 1);
		assertEquals(0, level.getDummyObjects().getSelected().x);
		dummy1.x = 3;
		assertEquals(3, level.getDummyObjects().getSelected().x); // should be dummy1 still
		
		level.isAboutToAlterState();
		dummy1.x = 1;
		assertEquals(3, level.getDummyObjects().getSelected().x); // should not be dummy1 now
		
		level.isAboutToAlterState();
		level.getDummyObjects().getSelected().x = 1;
		level.isAboutToAlterState();
		level.getDummyObjects().getSelected().x = 2;
		level.isAboutToAlterState();
		level.getDummyObjects().getSelected().x = 3;
		level.isAboutToAlterState();
		level.getDummyObjects().getSelected().x = 4;
		level.undo();
		assertEquals(3, level.getDummyObjects().getSelected().x);
		level.undo();
		assertEquals(2, level.getDummyObjects().getSelected().x);
		level.undo();
		assertEquals(1, level.getDummyObjects().getSelected().x);
	}
	
	@Test 
	public void testTilesUndoHistory() {
		Level level = new Level();
		level.initBlankMap(10, 10);
		assertEquals(0, level.getTileMap().getTileVal(5, 5, 0));
		level.getTileMap().setTileVal(5, 5, 0, 1);
		assertEquals(1, level.getTileMap().getTileVal(5, 5, 0));
		
		level.isAboutToAlterState();
		assertEquals(1, level.getTileMap().getTileVal(5, 5, 0));
		level.getTileMap().setTileVal(5, 5, 0, 2);
		assertEquals(2, level.getTileMap().getTileVal(5, 5, 0));
		
		level.isAboutToAlterState();
		assertEquals(2, level.getTileMap().getTileVal(5, 5, 0));
		level.getTileMap().setTileVal(5, 5, 0, 3);
		assertEquals(3, level.getTileMap().getTileVal(5, 5, 0));
		
		level.undo();
		assertEquals(2, level.getTileMap().getTileVal(5, 5, 0));
		
		level.isAboutToAlterState();
		assertEquals(2, level.getTileMap().getTileVal(5, 5, 0));
		level.getTileMap().setTileVal(5, 5, 0, 4);
		assertEquals(4, level.getTileMap().getTileVal(5, 5, 0));
		
		level.undo();
		assertEquals(2, level.getTileMap().getTileVal(5, 5, 0));
		
		level.undo();
		assertEquals(1, level.getTileMap().getTileVal(5, 5, 0));
	}
}
