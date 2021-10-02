package cluegame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;

public class Player {
	private String name;
	private Color color;
	private int row;
	private int col;
	private boolean isHuman;
	private Card person;
	private Card room;
	private Card weapon;
	private BoardCell occupyingCell;
	private boolean wasDisproven = true;
	private boolean wasMovedbySuggestion = false;
	protected ArrayList<Card> seen = new ArrayList<Card>();
	protected ArrayList<Card> unseen = new ArrayList<Card>();
	protected ArrayList<Card> hand = new ArrayList<Card>();


	public Player(String name, Color color, int row, int col) {
		super();
		this.name = name;
		this.color = color;
		this.row = row;
		this.col = col;
	}

	public ArrayList<Card> getHand() {
		return hand;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public boolean isHuman() {
		return isHuman;
	}

	public void setHuman(boolean isHuman) {
		this.isHuman = isHuman;
	}

	public boolean isDisproven() {
		return wasDisproven;
	}

	public void setDisproven(boolean wasDisproven) {
		this.wasDisproven = wasDisproven;
	}

	// sets specific cards in players hand
	public void setCards(Card person, Card room, Card weapon) {
		this.person = person;
		this.room = room;
		this.weapon = weapon;
		hand.add(person);
		hand.add(room);
		hand.add(weapon);
	}

	// sets hand
	public void setHand(ArrayList<Card> h) {
		seen.addAll(h);
		hand = h;
	}

	public void updateHand(Card card) {
		hand.add(card);
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

	public void clearHand() {
		hand.clear();
	}

	public ArrayList<Card> getUnseen() {
		return unseen;
	}

	public void setUnseenList(ArrayList<Card> u) {
		unseen = u;
	}
	
	public void createUnseenList() {
		ArrayList<Card> deck = Board.getInstance().getCards();
		unseen.clear();
		// add all cards in the deck that are not in our current hand to the unseen list
		for (Card c : deck) {
			if (!hand.contains(c)) {
				unseen.add(c);
			}
		}

		// remove any of our hand cards from the unseen list
		for (Card c : seen) {
			if (unseen.contains(c)) {
				unseen.remove(c);
			}
		}
	}
	
	public void removeFromUnseen(Card c) {
		unseen.remove(c);
	}

	@Override
	public String toString() {
		return "Player [name=" + name + ", color=" + color + ", row=" + row + ", col=" + col + ", isHuman=" + isHuman
				+ "]";
	}

	public ArrayList<Card> getCards() {
		return hand;
	}

	public Card disproveSuggestion(ArrayList<Card> suggest) {
		// look at every suggested card, and if the player owns it they return that card
		for (Card c : suggest) {
			for (Card h :hand) {
				if (h.cardName.equals(c.cardName)) {
					return c;
				}
			}
		}
		return null;
	}

	// player draw function
	public void draw(Graphics g) {
		Board board = Board.getInstance();
		int len = board.getCellLength();

		// calculating coordinates
		int x = len * col;
		int y = len * row;
		
		g.setColor(getColor());
		
		// if room already has players
		if(board.getCell(row, col).isRoom()) {
			// get every boardCell in the same room
			ArrayList<BoardCell> roomCellOptions = new ArrayList<BoardCell>();
			for (int i = 0; i < board.getNumRows(); i++) {
				for (int j = 0; j < board.getNumColumns(); j++) {
					if(board.getCell(i, j).isRoom() && board.getCell(i, j).isOccupied() == false &&  board.getCell(i, j).getRoomChar()==board.getCell(row, col).getRoomChar()) {
						roomCellOptions.add(board.getCell(i, j));
					}
				}
			}
			// pick one and draw yourself there
			int select = (int) Math.floor(Math.random() * (roomCellOptions.size()));
			if(getOccupyingCell()!= null) {
				getOccupyingCell().setOccupied(false);
			}
			setOccupyingCell(board.getCell(roomCellOptions.get(select).getRow(), roomCellOptions.get(select).getCol()));
			getOccupyingCell().setOccupied(true);
			g.fillOval(roomCellOptions.get(select).getCol() * len, roomCellOptions.get(select).getRow() * len, len, len);
		}else {
			// fill an oval with the player's color using our coordinates
			g.fillOval(col * len, row * len, len, len);
		}
	}

	public boolean getMovedbySuggestion() {
		return wasMovedbySuggestion;
	}

	public void setMovedbySuggestion(boolean moved) {
		this.wasMovedbySuggestion = moved;
	}

	public BoardCell getOccupyingCell() {
		return occupyingCell;
	}

	public void setOccupyingCell(BoardCell occupyingCell) {
		this.occupyingCell = occupyingCell;
	}
}
