package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cluegame.Board;
import cluegame.BoardCell;

//some unit tests were written incorrectly and needed to be changed
//unit tests in the 306 file were never changed
public class BoardAdjTargetTest {
	// load the board
	private static Board board;

	@BeforeAll
	public static void setUp() {
		board = Board.getInstance();
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		board.initialize();
	}

	// These cells are RED
	@Test
	public void testAdjacenciesRooms() {
		// cell is center of the White Room
		Set<BoardCell> testList = board.getAdjList(1, 1);
		assertTrue(testList.contains(board.getCell(6, 2)));
		assertEquals(1, testList.size());
		
		
		// Single Door Room (Orange Room) w/ Secret Passage (Blue Room)
		testList = board.getAdjList(3, 22);
		assertEquals(2, testList.size());
		// door
		assertTrue(testList.contains(board.getCell(0, 19)));
		// Secret Passage to Blue Room
		assertTrue(testList.contains(board.getCell(11, 2)));

		// Yellow Room
		testList = board.getAdjList(17, 22);
		assertEquals(3, testList.size());
		// walkway
		assertTrue(testList.contains(board.getCell(14, 24)));
		// walkway
		assertTrue(testList.contains(board.getCell(23, 20)));
		// Secret Passage to Black Room
		assertTrue(testList.contains(board.getCell(15, 16)));

		// White Room
		testList = board.getAdjList(20, 19);
		assertEquals(0, testList.size());
	}

	// These cells are PINK on the planning spreadsheet
	@Test
	public void testAdjacencyDoor() {
		Set<BoardCell> testList = board.getAdjList(23, 6);
		assertEquals(3, testList.size());
		// Enter Pink Room
		assertTrue(testList.contains(board.getCell(23, 3)));
		// Walkways
		assertTrue(testList.contains(board.getCell(22, 6)));
		assertTrue(testList.contains(board.getCell(23, 7)));

		testList = board.getAdjList(21, 16);
		assertEquals(3, testList.size());

		// Enter Black Room
		assertTrue(testList.contains(board.getCell(15, 16)));
		// Walkways
		assertTrue(testList.contains(board.getCell(22, 16)));
		assertTrue(testList.contains(board.getCell(21, 17)));

		testList = board.getAdjList(6, 2);
		assertEquals(4, testList.size());
		// Enter White Room
		assertTrue(testList.contains(board.getCell(1, 1)));
		// Walkways
		assertTrue(testList.contains(board.getCell(6, 1)));
		assertTrue(testList.contains(board.getCell(6, 3)));
		assertTrue(testList.contains(board.getCell(7, 2)));
	}

	// These tests are LIGHT PURPLE on the planning spreadsheet
	@Test
	public void testAdjacencyWalkways() {

		// edge
		Set<BoardCell> testList = board.getAdjList(20, 0);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(20, 1)));
		
		testList = board.getAdjList(25, 8);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(24, 8)));
		
		testList = board.getAdjList(0, 14);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(0, 13)));
		assertTrue(testList.contains(board.getCell(0, 15)));
		assertTrue(testList.contains(board.getCell(1, 14)));
		
		testList = board.getAdjList(12, 25);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(12, 24)));
		assertTrue(testList.contains(board.getCell(11, 25)));

		testList = board.getAdjList(10, 19);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(10, 20)));
		assertTrue(testList.contains(board.getCell(10, 18)));
		assertTrue(testList.contains(board.getCell(9, 19)));
		assertTrue(testList.contains(board.getCell(11, 19)));

		testList = board.getAdjList(9, 24);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(9, 23)));
		assertTrue(testList.contains(board.getCell(10, 24)));

		testList = board.getAdjList(6, 12);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(6, 11)));
		assertTrue(testList.contains(board.getCell(6, 13)));
		assertTrue(testList.contains(board.getCell(5, 12)));
	}

	// These are DARK PURPLE on the planning spreadsheet
	//@Test
	public void testTargetsInPinkRoom() {
		// test a roll of 1
		board.calcTargets(board.getCell(2, 10), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(6, targets.size());
		assertTrue(targets.contains(board.getCell(5, 13)));
		assertTrue(targets.contains(board.getCell(7, 9)));

		// test a roll of 2
		board.calcTargets(board.getCell(2, 10), 2);
		targets = board.getTargets();
		assertEquals(10, targets.size());
		assertTrue(targets.contains(board.getCell(4, 13)));
		assertTrue(targets.contains(board.getCell(6, 13)));
		assertTrue(targets.contains(board.getCell(5, 8)));
		assertTrue(targets.contains(board.getCell(5, 10)));

		// test a roll of 3
		board.calcTargets(board.getCell(12, 20), 3);
		targets = board.getTargets();
		assertEquals(17, targets.size());
		assertTrue(targets.contains(board.getCell(5, 7)));
		assertTrue(targets.contains(board.getCell(6, 8)));
		assertTrue(targets.contains(board.getCell(4, 14)));
		assertTrue(targets.contains(board.getCell(2, 14)));
	}

	//@Test
	public void testTargetsInBlueRoom() {
		// test a roll of 1
		board.calcTargets(board.getCell(9, 4), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCell(8, 4)));
		assertTrue(targets.contains(board.getCell(12, 1)));
		assertTrue(targets.contains(board.getCell(3, 22)));

		// test a roll of 2
		board.calcTargets(board.getCell(9, 4), 2);
		targets = board.getTargets();
		assertEquals(8, targets.size());
		assertTrue(targets.contains(board.getCell(8, 3)));
		assertTrue(targets.contains(board.getCell(13, 2)));
		// out secret passage
		assertTrue(targets.contains(board.getCell(0, 19)));

		// test a roll of 3
		board.calcTargets(board.getCell(9, 4), 3);
		targets = board.getTargets();
		assertEquals(10, targets.size());
		assertTrue(targets.contains(board.getCell(8, 2)));
		assertTrue(targets.contains(board.getCell(6, 4)));
		assertTrue(targets.contains(board.getCell(14, 0)));
		assertTrue(targets.contains(board.getCell(13, 3)));
		assertTrue(targets.contains(board.getCell(0, 18)));
		assertTrue(targets.contains(board.getCell(1, 19)));
	}

	// These are DARK GREEN on the planning spreadsheet
	//@Test
	public void testTargetsAtDoor() {
		// test a roll of 1, at door
		board.calcTargets(board.getCell(11, 16), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(10, 15)));
		assertTrue(targets.contains(board.getCell(15, 16)));

		// test a roll of 2, at door
		board.calcTargets(board.getCell(11, 16), 2);
		assertEquals(8, targets.size());
		assertTrue(targets.contains(board.getCell(14, 14)));
		assertTrue(targets.contains(board.getCell(7, 16)));
		assertTrue(targets.contains(board.getCell(17, 22)));
		assertTrue(targets.contains(board.getCell(21, 16)));

		// test a roll of 3, at door
		board.calcTargets(board.getCell(11, 16), 3);
		assertEquals(18, targets.size());
		assertTrue(targets.contains(board.getCell(8, 14)));
		assertTrue(targets.contains(board.getCell(13, 13)));
		assertTrue(targets.contains(board.getCell(22, 16)));
		assertTrue(targets.contains(board.getCell(9, 19)));
		assertTrue(targets.contains(board.getCell(24, 14)));
		assertTrue(targets.contains(board.getCell(23, 20)));
	}

	//@Test
	public void testTargetsInWalkway() {
		// test a roll of 1
		board.calcTargets(board.getCell(8, 6), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCell(8, 5)));
		assertTrue(targets.contains(board.getCell(7, 6)));

		// test a roll of 2
		board.calcTargets(board.getCell(8, 6), 2);
		targets = board.getTargets();
		assertEquals(5, targets.size());
		assertTrue(targets.contains(board.getCell(8, 4)));
		assertTrue(targets.contains(board.getCell(6, 6)));
		assertTrue(targets.contains(board.getCell(9, 7)));

		// test a roll of 3
		board.calcTargets(board.getCell(8, 6), 3);
		targets = board.getTargets();
		assertEquals(10, targets.size());
		// enters the Blue Room
		assertTrue(targets.contains(board.getCell(9, 4)));
		assertTrue(targets.contains(board.getCell(8, 3)));
		assertTrue(targets.contains(board.getCell(7, 6)));
		assertTrue(targets.contains(board.getCell(7, 8)));
		assertTrue(targets.contains(board.getCell(7, 10)));
	}

	//@Test
	// test to make sure occupied locations do not cause problems
	public void testTargetsOccupied() {
		// test a roll of 3 blocked 1 down
		board.getCell(22, 10).setOccupied(true);
		board.calcTargets(board.getCell(13, 7), 4);
		board.getCell(22, 9).setOccupied(false);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(9, targets.size());
		assertTrue(targets.contains(board.getCell(21, 11)));
		assertTrue(targets.contains(board.getCell(23, 11)));
		assertTrue(targets.contains(board.getCell(22, 6)));
		assertFalse(targets.contains(board.getCell(21, 7)));
		assertFalse(targets.contains(board.getCell(21, 9)));

		// we want to make sure we can get into the Red Room when its occupied
		board.getCell(17, 3).setOccupied(true);
		board.calcTargets(board.getCell(14, 4), 1);
		board.getCell(17, 3).setOccupied(false);
		targets = board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(17, 3)));
		assertTrue(targets.contains(board.getCell(13, 4)));
		assertTrue(targets.contains(board.getCell(14, 3)));

		// check leaving a room with a blocked doorway
		board.getCell(20, 3).setOccupied(true);
		board.getCell(23, 6).setOccupied(true);
		board.calcTargets(board.getCell(23, 3), 2);
		board.getCell(20, 3).setOccupied(false);
		board.getCell(23, 6).setOccupied(false);
		targets = board.getTargets();
		assertEquals(2, targets.size());
		assertTrue(targets.contains(board.getCell(20, 0)));
		assertTrue(targets.contains(board.getCell(20, 2)));
	}
}
