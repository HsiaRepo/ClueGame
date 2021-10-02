package cluegame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ComputerPlayer extends Player {

	private Room currentRoom;
	Random rand = new Random();

	public ComputerPlayer(String name, Color color, int row, int col) {
		super(name, color, row, col);
	}

	public void updateSeen(Card card) {
		seen.add(card);
	}

	public void updateUnseen(Card card) {
		unseen.add(card);
	}

	public ArrayList<Card> getSeen() {
		return seen;
	}

	public void clearUnseen() {
		unseen.clear();
	}

	public ArrayList<Card> getUnseen() {
		return unseen;
	}

	public void setRoom(Room room) {
		currentRoom = room;
	}

	public String getRoom() {
		return currentRoom.getName();
	}

	// recreates the unseenList before creating a new suggestion
	public ArrayList<Card> createSuggestion() {
		// Instead of using createUnseenList(), our unit tests specifically set up
		// different unseenLists for testing, so we have this commented out for now.
		// createUnseenList();
		ArrayList<Card> suggestion = suggest();
		return suggestion;
	}

	// generate ComputerPlayer suggestion
	public ArrayList<Card> suggest() {
		ArrayList<Card> suggestion = new ArrayList<Card>();

		ArrayList<Card> deck = Board.getInstance().getCards();
		BoardCell currLoc = Board.getInstance().getCell(getRow(), getCol());
		String RoomName = Board.getInstance().getRoom(currLoc).getName();

		// if the card name matches the room name, add it to the suggestion
		for (Card card : deck) {
			if (card.getName() == RoomName) {
				suggestion.add(card);
			}
		}

		// look at all the unseen PERSON and WEAPON cards and choose one randomly to add
		// to the suggestion
		ArrayList<Card> people = Card.getCardsOfType(unseen, CardType.PERSON);
		int x = (int) Math.floor(Math.random() * (people.size()));
		if (people.size() != 0) {
			Card personCard = people.get(x);
			suggestion.add(personCard);
		}

		ArrayList<Card> weapon = Card.getCardsOfType(unseen, CardType.WEAPON);
		int y = (int) Math.floor(Math.random() * (weapon.size()));
		if (weapon.size() != 0) {
			Card weaponCard = weapon.get(y);
			suggestion.add(weaponCard);
		}

		return suggestion;
	}

	// choose a target from the ComputerPlayer target set
	public BoardCell selectTargets(int pathlength) {
		Set<BoardCell> targets = new HashSet<BoardCell>();
		ArrayList<BoardCell> moveTile = new ArrayList<BoardCell>();
		ArrayList<Card> unseenRooms = Card.getCardsOfType(unseen, CardType.ROOM);
		BoardCell currCell = Board.getInstance().getCell(getRow(), getCol());

		// take in all the targets
		Board.getInstance().calcTargets(currCell, pathlength);
		targets = Board.getInstance().getTargets();
		int size = targets.size();

		// if there are no targets
		if (size == 0) {
			return currCell;
		}

		int rand = new Random().nextInt(size);

		// adding cells to the moveTile if they are in unseenRooms
		for (BoardCell cell : targets) {
			String roomName = "";
			if (cell.isRoomCenter()) {
				roomName = Board.getInstance().getRoom(cell).getName();
				for (Card card : unseenRooms) {
					if (roomName == card.cardName) {
						moveTile.add(cell);
					}
				}
			}
		}

		// randomly selects one of the moveTile cells to return if there are cells in
		// moveTile
		if (moveTile.size() > 0) {
			int randReturn = new Random().nextInt(moveTile.size());
			return moveTile.get(randReturn);
		}

		BoardCell ret = null;

		// otherwise we will return a random target cell
		int i = 0;
		for (BoardCell cell : targets) {
			if (rand == i) {
				ret = cell;
			}
			i++;
		}
		return ret;
	}

	public boolean computerAccusation() {
		createUnseenList();
		// if there are exactly 3 unknown cards, the computerAI knows the solution
		if (unseen.size() <= 3) {
			return true;
		}
		return false;
	}
}
