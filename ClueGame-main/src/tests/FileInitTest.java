package tests;

import java.io.FileNotFoundException;
import static org.junit.Assert.*;
import org.junit.Assert;
import org.junit.jupiter.api.*;

import cluegame.*;

class FileInitTest {

	public static final int NUM_ROWS = 26;
	public static final int NUM_COLUMNS = 26;
	private static Board board;

	@BeforeAll
	public static void setUpBeforeClass() throws FileNotFoundException{
		// Board setup, 2 files for config, and initializing the board,
		board = Board.getInstance();
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		board.initialize();
	}

	// test room names
	@Test
	public void testRoomNames() {
		assertEquals("White Room", board.getRoom('W').getName());
		assertEquals("Black Room", board.getRoom('B').getName());
		assertEquals("Blue Room", board.getRoom('L').getName());
		assertEquals("Purple Room", board.getRoom('P').getName());
		assertEquals("Green Room", board.getRoom('G').getName());
		assertEquals("Yellow Room", board.getRoom('Y').getName());
		assertEquals("Red Room", board.getRoom('R').getName());
		assertEquals("Orange Room", board.getRoom('O').getName());
		assertEquals("Pink Room", board.getRoom('K').getName());
		assertEquals("Walkway", board.getRoom('Z').getName());
		assertEquals("Unused", board.getRoom('X').getName());
	}

	// test board row and col values
	@Test
	public void testRowsCols() {
		assertEquals(NUM_ROWS, board.getNumRows());
		assertEquals(NUM_COLUMNS, board.getNumColumns());
	}

	// test up direction
	@Test
	public void upTestDoorDirection() {
		BoardCell cell = board.getCell(6, 2);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.UP, cell.getDoorDirection());
	}

	// test down direction
	@Test
	public void downTestDoorDirection() {
		BoardCell cell = board.getCell(8, 4);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.DOWN, cell.getDoorDirection());
	}

	// test left direction
	@Test
	public void leftTestDoorDirection() {
		BoardCell cell = board.getCell(17, 6);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.LEFT, cell.getDoorDirection());
	}

	// test right direction
	@Test
	public void rightTestDoorDirection() {
		BoardCell cell = board.getCell(14, 14);
		assertTrue(cell.isDoorway());
		assertEquals(DoorDirection.RIGHT, cell.getDoorDirection());
	}

	// test locations that are not doorways return false
	@Test
	public void testNotDoorWay() {
		BoardCell cell = board.getCell(7, 0);
		assertFalse(cell.isDoorway());
		cell = board.getCell(7, 7);
		assertFalse(cell.isDoorway());
		cell = board.getCell(20, 5);
		assertFalse(cell.isDoorway());
	}

	// test the number of expected doorways
	@Test
	public void testNumberOfDoorways() {
		int doors = 0;
		for (int row = 0; row < board.getNumRows(); row++)
			for (int col = 0; col < board.getNumColumns(); col++) {
				BoardCell cell = board.getCell(row, col);
				if (cell.isDoorway())
					doors++;
			}
		Assert.assertEquals(19, doors);
	}

	// test standard room location
	@Test
	public void testWhiteRoom() {
		BoardCell cell = board.getCell(3, 0);
		Room room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "White Room");
		assertFalse(cell.isLabel());
		assertFalse(cell.isRoomCenter());
		assertFalse(cell.isDoorway());
	}

	// test label cell
	@Test
	public void testBlackRoom() {
		BoardCell cell = board.getCell(18, 16);
		Room room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Black Room");
		assertTrue(cell.isLabel());
		assertTrue(room.getLabelCell() == cell);
	}

	// test room center cell
	@Test
	public void testBlueRoom() {
		BoardCell cell = board.getCell(11, 2);
		Room room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Blue Room");
		assertTrue(cell.isRoomCenter());
		assertTrue(room.getCenterCell() == cell);
	}

	// test secret passage
	@Test
	public void testOrangeRoom() {
		BoardCell cell = board.getCell(8, 25);
		Room room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Orange Room");
		assertTrue(cell.getSecretPassage() == 'L');
	}

	// test secret passage
	@Test
	public void testYellowRoom() {
		BoardCell cell = board.getCell(16, 25);
		Room room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Yellow Room");
		assertTrue(cell.getSecretPassage() == 'B');
	}

	// test room center cell
	@Test
	public void testRedRoom() {
		BoardCell cell = board.getCell(17, 3);
		Room room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Red Room");
		assertTrue(cell.isRoomCenter());
		assertTrue(room.getCenterCell() == cell);
	}

	// test walkway
	@Test
	public void testWalkway() {
		BoardCell cell = board.getCell(7, 6);
		Room room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Walkway");
		assertFalse(cell.isRoomCenter());
		assertFalse(cell.isLabel());
	}

	// test unused
	@Test
	public void testUnused() {
		BoardCell cell = board.getCell(24, 5);
		Room room = board.getRoom(cell);
		assertTrue(room != null);
		assertEquals(room.getName(), "Unused");
		assertFalse(cell.isRoomCenter());
		assertFalse(cell.isLabel());
	}

}
