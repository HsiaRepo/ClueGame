package experiment;
import java.util.*;

public class TestBoardCell {
	private int row;
	private int column;
	private Set<TestBoardCell> adjList = new HashSet<TestBoardCell>();
	private boolean partRoom;
	private boolean occupiedRoom;

	public TestBoardCell(int row, int column) {
		super();
		this.row = row;
		this.column = column;
		partRoom = false;
		occupiedRoom = false;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return column;
	}

	public void addAdjacency(TestBoardCell cell) {
		adjList.add(cell);
	}

	// generate an adjacency list for each cell
	public Set<TestBoardCell> getAdjList() {
		Set<TestBoardCell> adjacent = new HashSet<TestBoardCell>();
		TestBoardCell[][] board = TestBoard.grid;
		if (row - 1 > -1 && board[row - 1][column] != null) {
			adjacent.add(board[row - 1][column]);
		}

		if (column - 1 > -1 && board[row][column - 1] != null) {
			adjacent.add(board[row][column - 1]);
		}

		if (column + 1 < 4 && board[row][column + 1] != null) {
			adjacent.add(board[row][column + 1]);
		}

		if (row + 1 < 4 && board[row + 1][column] != null) {
			adjacent.add(board[row + 1][column]);
		}
		adjList = adjacent;
		return adjList;
	}

	public void setRoom(boolean roomBool) {
		this.partRoom = roomBool;
	}

	public boolean isRoom() {
		return partRoom;
	}

	public void setOccupied(boolean occupied) {
		this.occupiedRoom = occupied;
	}

	public boolean getOccupied() {
		return occupiedRoom;
	}
}
