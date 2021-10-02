package cluegame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;

public class BoardCell {
	// initializes instance variables
	private int row;
	private int col;
	private boolean isDoorway;
	private DoorDirection doorDirection;
	private boolean isRoomLabel;
	private boolean isRoomCenter;
	private boolean isUnused; // TODO still need to set in loadConfig
	private boolean isRoom; // TODO still need to set in loadConfig
	private char secretPassageChar;
	private boolean isSecretPassage;
	private char roomChar;
	private Set<BoardCell> adjList = new HashSet<>();
	private boolean isOccupied = false;
	private boolean hasMorePlayers;
	private int numPlayers;
	private Board board;
	public int width;
	public int height;
	public boolean isTarget;


	@Override
	public String toString() {
		return "BoardCell [row=" + row + ", col=" + col + ", isDoorway=" + isDoorway + ", doorDirection="
				+ doorDirection + ", isRoomLabel=" + isRoomLabel + ", isRoomCenter=" + isRoomCenter + ", secretPassage="
				+ secretPassageChar + ", roomChar=" + roomChar + "]";
	}

	// BoardCell constructor with row and column parameters
	public BoardCell(int row, int col) {
		super();
		this.row = row;
		this.col = col;
		board = Board.getInstance();
	}

	// returns rows
	public int getRow() {
		return row;
	}
	
	// designates a cell is in a room
	public void setIsRoom(boolean b) {
		isRoom = b;
	}

	// sets rows
	public void setRow(int row) {
		this.row = row;
	}

	// returns columns
	public int getCol() {
		return col;
	}

	// sets columns
	public void setCol(int col) {
		this.col = col;
	}

	// returns bool for whether or not the cell is a doorway
	public boolean isDoorway() {
		return isDoorway;
	}

	// sets doorway instance variable to bool
	public void setIsDoorway(boolean d) {
		isDoorway = d;
	}

	// returns enum type door direction for the cell
	public DoorDirection getDoorDirection() {
		return doorDirection;
	}

	// sets the door direction for the cell
	public void setDoorDirection(DoorDirection d) {
		this.doorDirection = d;
	}

	// checks whether the cell is a room label and returns bool
	public boolean isLabel() {
		return isRoomLabel;
	}

	// sets the room label for each cell
	public void setIsRoomLabel(boolean r) {
		this.isRoomLabel = r;
	}

	// returns bool for whether or not the cell is a room center
	public boolean isRoomCenter() {
		return isRoomCenter;
	}

	// sets the bool room center for the cell
	public void setIsRoomCenter(boolean r) {
		this.isRoomCenter = r;
	}

	// returns the secret passage way for the cell
	public char getSecretPassage() {
		return secretPassageChar;
	}

	// sets the secret passage way for the cell
	public void setSecretPassage(char s) {
		this.secretPassageChar = s;
	}

	// returns the room label in char type
	public char getRoomChar() {
		return roomChar;
	}

	// sets the room character for the cell
	public void setRoomChar(char c) {
		roomChar = c;
	}

	// returns the adjacent list for the cell
	public Set<BoardCell> getAdjList() {
		return adjList;
	}

	// sets the adjacency list for the cell
	public void setAdjList(Set<BoardCell> a) {
		this.adjList = a;
	}

	// adds adjacent cells to adjacency list
	public void addAdjacency(BoardCell cell) {
		adjList.add(cell);
	}

	// sets the occupied room to boolean value
	public void setOccupied(boolean b) {
		isOccupied = b;
	}

	// checks if a room is occupied by returning boolean value
	public boolean isOccupied() {
		return isOccupied;
	}

	// returns if a cell is unused
	public boolean isUnused() {
		return isUnused;
	}

	// designate if a cell is unused
	public void setUnused(boolean isUnused) {
		this.isUnused = isUnused;
	}

	// returns if a cell is in a room
	public boolean isRoom() {
		return isRoom;
	}

	// returns if a cell is a secret passage
	public boolean isSecretPassage() {
		return isSecretPassage;
	}
	

	// denote a cell is a secret passage
	public void boolSecretPassage(boolean isSecretPassage) {
		this.isSecretPassage = isSecretPassage;
	}

	public void draw(Graphics g) {
		int len = board.getCellLength();
		this.width = len;
		this.height = len;

		// coordinates
		int x = width * col;
		int y = height * row;

		if (isUnused) { // fill empty cells
			g.setColor(Color.GRAY);
			g.fillRect(x, y, width, height);
		} else if (isRoom) { // fill room cells

			g.setColor(Color.cyan);
			g.fillRect(x, y, width, height);

			if (isRoomCenter()) { // if its a room center, draw the room name in black
				g.setColor(Color.BLACK);
				g.drawString(board.getRoom(this).getName(), x - width, y - 5);
			}

			if (isSecretPassage()) { // fill secret passage
				g.setColor(Color.YELLOW);
				g.fillRect(x, y, width, height);
				g.setColor(Color.BLACK);
				g.drawString("  S", x, y + height - 5);
			}
		} else { // fill rooms
			g.setColor(Color.GREEN);
			g.fillRect(x, y, width, height);
			g.setColor(Color.black);
			g.drawRect(x, y, width, height);
			if (isDoorway()) {
				g.setColor(Color.BLUE);
				switch (this.doorDirection) {
				case LEFT:
					g.fillRect(x, y, width / 5, height);
					break;
				case RIGHT:
					g.fillRect(x + 21 + width * (9 / 10), y, width + 20/ 5, height);
					break;
				case UP:
					g.fillRect(x , y, width, height / 5);
					break;
				case DOWN:
					g.fillRect(x, y, width, height);
					g.setColor(Color.GREEN);
					g.fillRect(x, y, width, height / 5 + 16);
					g.setColor(Color.black);
					g.drawRect(x, y, width, height);
					break;
				default:
					break;
				}
			}
		}

		// draw target cells
		if (isTarget) {
			g.setColor(Color.ORANGE);
			g.fillRect(x, y, width, height);
			isTarget = false;
		}
	}

	// checks if target cell is a room center.  If it is, calls targetRoom()
	public void targetCell(boolean target) {
		if (this.isRoomCenter()) {
			board.getCell(row, col).targetRoom();
		}
		
		isTarget = target;
	}

	// draws an entire room as a target
	public void targetRoom() {
		if (isRoom && !isTarget) {
			isTarget = true;
			board.addTarget(this);
			// do this for every adjacency in the room (will eventually mark the whole room as targets)
			if ((row + 1) < board.getNumRows() && board.getCell(row + 1, col) != null) {
				board.getCell(row + 1, col).targetRoom();
			}
			if ((col + 1) < board.getNumColumns() && board.getCell(row, col + 1) != null) {
				board.getCell(row, col + 1).targetRoom();
			}
			if ((row - 1) >= 0 && board.getCell(row - 1, col) != null) {
				board.getCell(row - 1, col).targetRoom();
			}
			if ((col - 1) >= 0 && board.getCell(row, col - 1) != null) {
				board.getCell(row, col - 1).targetRoom();
			}
		}
	}

	public boolean hasMorePlayers() {
		return hasMorePlayers;
	}

	public void setHasMorePlayers(boolean hasMorePlayers) {
		this.hasMorePlayers = hasMorePlayers;
	}

	public int getNumPlayers() {
		return numPlayers;
	}

	public void setNumPlayers(int numPlayers) {
		this.numPlayers = numPlayers;
	}
	
}
