package tests;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.jupiter.api.*;
import experiment.*;

class BoardTestsExp {
	TestBoard board;

	@BeforeEach
	public void setup() {
		// board creates adjacency list

		Map<TestBoardCell, Set<TestBoardCell>> adjMatrix;
		board = new TestBoard();
	}

	// test top left corner
	@Test
	public void testAdj0() {
		TestBoardCell left = board.getCell(0, 0);
		Set<TestBoardCell> testingLeft = left.getAdjList();
		Assert.assertTrue(testingLeft.contains(board.getCell(1, 0)));
		Assert.assertTrue(testingLeft.contains(board.getCell(0, 1)));
		Assert.assertEquals(2, testingLeft.size());
	}

	// test bottom right corner
	@Test
	public void testAdj1() {
		TestBoardCell right = board.getCell(3, 3);
		Set<TestBoardCell> testingRight = right.getAdjList();
		Assert.assertTrue(testingRight.contains(board.getCell(3, 2)));
		Assert.assertTrue(testingRight.contains(board.getCell(2, 3)));
		Assert.assertEquals(2, testingRight.size());
	}

	// test right edge
	@Test
	public void testAdj2() {
		TestBoardCell rightEdge = board.getCell(1, 3);
		Set<TestBoardCell> testingRightEdge = rightEdge.getAdjList();
		Assert.assertTrue(testingRightEdge.contains(board.getCell(1, 2)));
		Assert.assertTrue(testingRightEdge.contains(board.getCell(0, 3)));
		Assert.assertTrue(testingRightEdge.contains(board.getCell(2, 3)));
		Assert.assertEquals(3, testingRightEdge.size());
	}

	// test left edge
	@Test
	public void testAdj3() {
		TestBoardCell leftEdge = board.getCell(3, 0);
		Set<TestBoardCell> testingLeftEdge = leftEdge.getAdjList();
		Assert.assertTrue(testingLeftEdge.contains(board.getCell(2, 0)));
		Assert.assertTrue(testingLeftEdge.contains(board.getCell(3, 1)));
		Assert.assertEquals(2, testingLeftEdge.size());
	}

	// test middle board
	@Test
	public void testAdj4() {
		// a left edge
		TestBoardCell center = board.getCell(2, 2);
		Set<TestBoardCell> testCenter = center.getAdjList();
		Assert.assertTrue(testCenter.contains(board.getCell(2, 1)));
		Assert.assertTrue(testCenter.contains(board.getCell(2, 3)));
		Assert.assertTrue(testCenter.contains(board.getCell(1, 2)));
		Assert.assertTrue(testCenter.contains(board.getCell(3, 2)));
		Assert.assertEquals(4, testCenter.size());
	}

	// starting location [0,0] steps: 3
	@Test
	public void calcTarget0_0() {
		TestBoardCell cell = board.getCell(0, 0);
		board.calcTargets(cell, 3);
		Set<TestBoardCell> targets = board.getTargets();
		Assert.assertEquals(6, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(0, 3)));
		Assert.assertTrue(targets.contains(board.getCell(3, 0)));
		Assert.assertTrue(targets.contains(board.getCell(1, 0)));
		Assert.assertTrue(targets.contains(board.getCell(1, 2)));
		Assert.assertTrue(targets.contains(board.getCell(0, 1)));
		Assert.assertTrue(targets.contains(board.getCell(2, 1)));
	}

	// starting location [0,2] steps: 4
	@Test
	public void calcTarget0_2() {
		TestBoardCell cell = board.getCell(0, 2);
		board.calcTargets(cell, 4);
		Set<TestBoardCell> targets = board.getTargets();
		Assert.assertTrue(targets.contains(board.getCell(1, 1)));
		Assert.assertTrue(targets.contains(board.getCell(2, 0)));
		Assert.assertTrue(targets.contains(board.getCell(2, 2)));
		Assert.assertTrue(targets.contains(board.getCell(3, 1)));
		Assert.assertTrue(targets.contains(board.getCell(3, 3)));
		Assert.assertTrue(targets.contains(board.getCell(0, 0)));
		Assert.assertTrue(targets.contains(board.getCell(1, 3)));
		Assert.assertEquals(7, targets.size());
	}

	// starting location [0,2] steps: 4
	// mainly testing setRoom and setOccupied functions
	@Test
	public void calcTargetsMixed() {
		board.getCell(1, 1).setOccupied(true);
		board.getCell(0, 2).setRoom(true);
		TestBoardCell cell = board.getCell(0, 2);
		board.calcTargets(cell, 4);
		Set<TestBoardCell> targets = board.getTargets();
		Assert.assertTrue(targets.contains(board.getCell(2, 2)));
		Assert.assertTrue(targets.contains(board.getCell(3, 3)));
		Assert.assertTrue(targets.contains(board.getCell(3, 1)));
		Assert.assertTrue(targets.contains(board.getCell(1, 3)));
		Assert.assertTrue(targets.contains(board.getCell(2, 0)));
	}

	// starting location [0,2] steps: 4
	// testing setOccupied separately
	@Test
	public void calcTargetsSetOccupied() {
		board.getCell(1, 1).setOccupied(true);
		TestBoardCell cell = board.getCell(0, 2);
		board.calcTargets(cell, 4);
		Set<TestBoardCell> targets = board.getTargets();
		Assert.assertEquals(5, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(2, 2)));
		Assert.assertTrue(targets.contains(board.getCell(3, 3)));
		Assert.assertTrue(targets.contains(board.getCell(3, 1)));
		Assert.assertTrue(targets.contains(board.getCell(1, 3)));
		Assert.assertTrue(targets.contains(board.getCell(2, 0)));

	}

	// starting location [0,0] steps: 3
	// testing setRoom separately
	@Test
	public void calcTargetSetRoom() {
		board.getCell(1, 2).setRoom(true);
		TestBoardCell cell = board.getCell(0, 0);
		board.calcTargets(cell, 3);
		Set<TestBoardCell> targets = board.getTargets();
		Assert.assertEquals(6, targets.size());
		Assert.assertTrue(targets.contains(board.getCell(0, 3)));
		Assert.assertTrue(targets.contains(board.getCell(3, 0)));
		Assert.assertTrue(targets.contains(board.getCell(1, 0)));
		Assert.assertTrue(targets.contains(board.getCell(1, 2)));
		Assert.assertTrue(targets.contains(board.getCell(0, 1)));
		Assert.assertTrue(targets.contains(board.getCell(2, 1)));

	}

}
