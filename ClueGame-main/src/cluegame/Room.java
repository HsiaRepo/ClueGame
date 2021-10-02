package cluegame;

public class Room {
	//initializes instance variables
	private String name;
	private BoardCell centerCell;
	private BoardCell labelCell;

	//room constructor with name parameter
	public Room(String n) {
		super();
		name = n;
	}

	//TEMPORARY: included a toString method to test our boardCell objects
	@Override
	public String toString() {
		return "Room [name=" + name + ", centerCell=" + centerCell + ", labelCell=" + labelCell + "]";
	}

	//returns the room name
	public String getName() {
		return name;
	}

	//sets room name
	public void setName(String n) {
		name = n;
	}

	//returns the center cell of the room
	public BoardCell getCenterCell() {
		return centerCell;
	}

	//sets the center cell of the room
	public void setCenterCell(BoardCell cell) {
		centerCell = cell;
	}

	//returns the room label cell
	public BoardCell getLabelCell() {
		return labelCell;
	}

	//sets the room label cell
	public void setLabelCell(BoardCell cell) {
		labelCell = cell;
	}

}
