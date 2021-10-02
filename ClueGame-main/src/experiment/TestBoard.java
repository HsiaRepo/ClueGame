package experiment;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestBoard {
	private Set<TestBoardCell> targets;
	private Set<TestBoardCell> visited;
	public static TestBoardCell[][] grid;
	private Map<TestBoardCell, Set<TestBoardCell>> adjMatrix;
	final static int COLS = 4;
	final static int ROWS = 4;

	public TestBoard() {
		// set up the board
		targets = new HashSet<TestBoardCell>();
		visited = new HashSet<TestBoardCell>();
		grid = new TestBoardCell[ROWS][COLS];
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				grid[i][j] = new TestBoardCell(i, j);
			}
		}
	}

	public void calcTargets(TestBoardCell startCell, int steps) {
		visited.add(startCell);
		for (TestBoardCell cell : startCell.getAdjList()) {
			if (!cell.getOccupied()) {
				if (!visited.contains(cell)) {
					visited.add(cell);
					if (steps == 1) {
						targets.add(cell);
					} else {
						calcTargets(cell, steps - 1);
					}
					visited.remove(cell);
				}
			}
		}
	}

	public Set<TestBoardCell> getTargets() {
		return targets;
	}

	public TestBoardCell getCell(int row, int col) {
		TestBoardCell cell = grid[row][col];
		return cell;
	}

}
