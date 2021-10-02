package cluegame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class Board extends JPanel implements MouseListener {

	// initializes instance variables
	private static Board theInstance = new Board();
	private static BoardCell[][] grid;
	private int rows;
	private int cols;
	private String layoutConfigFile;
	private String setupConfigFile;
	public static Player disprovedBy;
	private ArrayList<Card> rooms = new ArrayList<Card>();
	private ArrayList<Card> weapons = new ArrayList<Card>();
	public static Map<Character, Room> roomMap = new HashMap<>();
	private static Set<BoardCell> doorWay = new HashSet<>();
	private static Set<BoardCell> secretPassageCell = new HashSet<>();
	private Set<BoardCell> targets = new HashSet<>();
	private Set<BoardCell> visited = new HashSet<>();
	private static String dataPath = "src/data/";
	private static ArrayList<Player> allPlayers = new ArrayList<Player>();
	private ArrayList<Card> cardDeck = new ArrayList<Card>();
	private ArrayList<Card> weaponDeck = new ArrayList<Card>();
	private ArrayList<Card> roomDeck = new ArrayList<Card>();
	private ArrayList<Card> personDeck = new ArrayList<Card>();
	private ArrayList<Card> peopleDeck = new ArrayList<Card>();
	private Map<String, Color> colorMap = new HashMap<>();
	private static ArrayList<Card> solution = new ArrayList<Card>(3);
	private Map<Player, ArrayList<Card>> cardsDealt = new HashMap<>();
	private static int WEAPONS = 0;
	private static int PLAYERS = 0;
	private static int ROOMS = 0;
	private boolean hasMoved = true;
	public static int playerCount = 500;
	
	// Make known cards panel instance var w/ setter
	private CardPanel pan;
	

	public void setPan(CardPanel pan) {
		this.pan = pan;
	}

	// constructor is private to ensure only one can be created
	private Board() {
		super();
		setUpColorMap();
	}

	private void setUpColorMap() {
		this.colorMap.put("PINK", Color.pink);
		this.colorMap.put("RED", Color.red);
		this.colorMap.put("BLACK", Color.black);
		this.colorMap.put("WHITE", Color.white);
		this.colorMap.put("GREEN", Color.green);
		this.colorMap.put("YELLOW", Color.yellow);
		this.colorMap.put("ORANGE", Color.orange);
		this.colorMap.put("BLUE", Color.blue);
	}

	private void getSizeFromFile() {

		// get rows and columns
		FileReader reader;
		try {
			reader = new FileReader(layoutConfigFile);
			Scanner in = new Scanner(reader);
			// read in file to see board size
			rows = 0;
			cols = 0;
			String colStr;
			// every hasNextLine is a new row so we increment the row counter
			if (in.hasNextLine()) {
				colStr = in.nextLine();
				// counts how many items there are separated by a comma and sets that value to
				// cols
				cols = (int) colStr.chars().filter(ch -> ch == ',').count();
				rows++;
			}
			int colComparison;
			while (in.hasNextLine()) {
				colStr = in.nextLine();
				colComparison = (int) colStr.chars().filter(ch -> ch == ',').count();
				if (colComparison > cols) {
					cols = colComparison;
				}
				rows++;
			}
			cols++;
			in.close();
		} catch (FileNotFoundException e) {
			// could not load file, so instead set defaults
			return;
		}
	}

	// this method returns the only Board
	public static Board getInstance() {
		return theInstance;
	}

	private void createGrid() {
		// create the grid
		grid = new BoardCell[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				grid[i][j] = new BoardCell(i, j);
			}
		}
	}

	// initialize the board (since we are using singleton pattern)
	public void initialize() throws BadConfigFormatException {
		getSizeFromFile();
		createGrid();
		if (theInstance == null) {
			theInstance = new Board();
		}
		try {
			loadSetupConfig();
			loadLayoutConfig();
			if(!setupConfigFile.contains("306")) {
				createSolution();
				dealPlayers();
				for(Player p: getPlayers()) {
					p.createUnseenList();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// gets cell at (r, c) in the grid
	public BoardCell getCell(int r, int c) {
		return grid[r][c];
	}

	// sets up files that power our game and the data path for the files
	public void setConfigFiles(String string, String string2) {
		String str1 = dataPath + string;
		String str2 = dataPath + string2;
		layoutConfigFile = str1;
		setupConfigFile = str2;
	}

	// the room name and room character are read from the file and used to make the
	// roomMap
	public void loadSetupConfig() throws FileNotFoundException, BadConfigFormatException {
		roomDeck.clear();
		rooms.clear();
		peopleDeck.clear();
		cardDeck.clear();
		personDeck.clear();
		allPlayers.clear();
		// reads in the file
		File in = new File(setupConfigFile);
		Scanner scan = new Scanner(in);

		// scans every line of the file
		while (scan.hasNext()) {
			// separates the line of the file by ','
			String temp = scan.nextLine();
			String[] tempArr = temp.split(", ");
			int len = tempArr.length;
			String identifier = tempArr[0];

			// if the cell separated by a comma has more than 1 letter, we know its either a doorway or is a special cell in the room
			if (len > 1) {
				// adding to the roomMap with the character of the room
				if (identifier.equals("Room") || identifier.equals("Space")) {
					String name = tempArr[1];
					String initialCell = tempArr[2];
					Room r = new Room(name);

					roomMap.put(initialCell.charAt(0), r);
					// sets up room cards
					if (identifier.equals("Room")) {
						Card card = new Card(name, CardType.ROOM);
						roomDeck.add(card);
						rooms.add(card);
						cardDeck.add(card);
						ROOMS++;
					}
				}
				// takes in players from setup file
				if (identifier.equals("Player")) {
					PLAYERS++;
					String tempName = tempArr[2];
					Color tempColor = colorMap.get(tempArr[3]);
					boolean isHuman = false;
					char isHumanChar = tempArr[1].charAt(0);
					// sets up humanPlayer object for human player and person cards
					if (isHumanChar == 'H') {
						isHuman = true;
						HumanPlayer h1 = new HumanPlayer(tempName, tempColor, Integer.parseInt(tempArr[4]), Integer.parseInt(tempArr[5]));
						h1.setHuman(isHuman);
						allPlayers.add(h1);
						Card card = new Card(tempName, CardType.PERSON);
						personDeck.add(card);
						peopleDeck.add(card);
						cardDeck.add(card);
					}
					// sets up computerPlayer for computer players and person cards
					if (isHumanChar == 'C') {
						isHuman = false;
						ComputerPlayer c1 = new ComputerPlayer(tempName, tempColor, Integer.parseInt(tempArr[4]), Integer.parseInt(tempArr[5]));
						allPlayers.add(c1);
						c1.setHuman(isHuman);
						Card card = new Card(tempName, CardType.PERSON);
						cardDeck.add(card);
						personDeck.add(card);
						peopleDeck.add(card);
					}
					PLAYERS++;
				}
				// sets up weapon cards
				if (identifier.equals("Weapon")) {
					Card card = new Card(tempArr[1], CardType.WEAPON);
					weapons.add(card);
					cardDeck.add(card);
					weaponDeck.add(card);
					WEAPONS++;
				}
			} else {
				if (len > 1) {
					throw new BadConfigFormatException("error: setup file has improper format modifier");
				}
			}
		}
		scan.close();
	}

	// the room name and room character are read from the file and used to set room
	// attributes
	public void loadLayoutConfig() throws FileNotFoundException, BadConfigFormatException {
		File in = new File(layoutConfigFile);
		Scanner scan = new Scanner(in);
		int rowCount = 0;
		Set<Integer> dimentionCheck = new HashSet<>();
		Set<BoardCell> doorwayToRoom = new HashSet<>();
		Set<BoardCell> secretPassages = new HashSet<>();
		ArrayList<Character> doorSymbols = new ArrayList<>(4);
		doorSymbols.add('v');
		doorSymbols.add('<');
		doorSymbols.add('^');
		doorSymbols.add('>');

		// reads in "cells" separated by commas
		while (scan.hasNext()) {
			String temp = scan.nextLine();
			String[] tempArr = temp.split(",");
			int len = tempArr.length;

			if (len != cols) {
				throw new BadConfigFormatException("bad layout config: missing a row");
			}

			cols = len;
			dimentionCheck.add(len);

			// Setting BoardCell and Room variables based on layout file.
			for (int colCount = 0; colCount < len; colCount++) {
				BoardCell currentGridCell = grid[rowCount][colCount];
				String cell = tempArr[colCount];
				int cellLength = tempArr[colCount].length();
				char symbol = tempArr[colCount].charAt(0);
				currentGridCell.setRoomChar(symbol);
				currentGridCell.setDoorDirection(DoorDirection.NONE);
				currentGridCell.setIsDoorway(false);
				if (cell.charAt(0) == 'X') {
					currentGridCell.setUnused(true);
				} else {
					currentGridCell.setUnused(false);
				}
				ArrayList<Character> roomSymbols = new ArrayList<>(9);
				roomSymbols.add('O');
				roomSymbols.add('R');
				roomSymbols.add('B');
				roomSymbols.add('K');
				roomSymbols.add('L');
				roomSymbols.add('W');
				roomSymbols.add('P');
				roomSymbols.add('G');
				roomSymbols.add('Y');

				if (roomSymbols.contains(cell.charAt(0))) {
					currentGridCell.setIsRoom(true);
				}
				if (cellLength > 1) {
					if (doorSymbols.contains(tempArr[colCount].charAt(1))) {
						currentGridCell.setIsDoorway(true);
						doorwayToRoom.add(currentGridCell);
						if (cell.charAt(1) == '<') {
							currentGridCell.setDoorDirection(DoorDirection.LEFT);
						} else if (cell.charAt(1) == '^') {
							currentGridCell.setDoorDirection(DoorDirection.UP);
						} else if (cell.charAt(1) == '>') {
							currentGridCell.setDoorDirection(DoorDirection.RIGHT);
						} else if (cell.charAt(1) == 'v') {
							currentGridCell.setDoorDirection(DoorDirection.DOWN);
						}
					}

					if (cell.charAt(1) == '#') {
						currentGridCell.setIsRoomLabel(true);
						roomMap.get(symbol).setLabelCell(currentGridCell);
					} else {
						currentGridCell.setIsRoomLabel(false);
					}
					if (cell.charAt(1) == '*') {
						currentGridCell.setIsRoomCenter(true);
						roomMap.get(symbol).setCenterCell(currentGridCell);
					} else {
						currentGridCell.setIsRoomCenter(false);
					}
					if (Character.isLetter((cell.charAt(1))) && cell.charAt(1) != 'v') {
						currentGridCell.setSecretPassage(cell.charAt(1));
						secretPassages.add(currentGridCell);
						currentGridCell.boolSecretPassage(true);
					}
				}
			}
			rowCount++;
		}
		scan.close();
		if (dimentionCheck.size() > 1) {
			throw new BadConfigFormatException("error: does not have the same number of columns in each row");
		}
		Board.setDoorWay(doorwayToRoom);
		Board.setSecretPassageCell(secretPassages);
	}

	// returns the room that corresponds to character c
	public Room getRoom(char c) {
		return roomMap.get(c);
	}

	// returns the room a cell is in
	public Room getRoom(BoardCell cell) {
		char key = cell.getRoomChar();
		Room room = roomMap.get(key);
		return room;
	}

	public static Set<BoardCell> getDoorWay() {
		return doorWay;
	}

	public static void setDoorWay(Set<BoardCell> doorWay) {
		Board.doorWay = doorWay;
	}

	public static Set<BoardCell> getSecretPassageCell() {
		return secretPassageCell;
	}

	public static void setSecretPassageCell(Set<BoardCell> secretPassageCell) {
		Board.secretPassageCell = secretPassageCell;
	}

	public int getNumRows() {
		return rows;
	}

	public static Map<Character, Room> getMap(){
		return roomMap;
	}

	public int getNumColumns() {
		return cols;
	}

	public Set<BoardCell> getAdjList(int i, int j) {
		Board board = Board.getInstance();
		BoardCell cell = board.getCell(i, j);
		char roomChar = cell.getRoomChar();

		// checks the doorways to a room if the cell is a center cell
		if (cell.isRoomCenter()) {
			for (BoardCell current : doorWay) {
				switch (current.getDoorDirection()) {
				case LEFT:
					if (grid[current.getRow()][current.getCol() - 1].getRoomChar() == roomChar) {
						cell.addAdjacency(current);
					}
					break;
				case RIGHT:
					if (grid[current.getRow()][current.getCol() + 1].getRoomChar() == roomChar) {
						cell.addAdjacency(current);
					}
					break;
				case UP:
					if (grid[current.getRow() - 1][current.getCol()].getRoomChar() == roomChar) {
						cell.addAdjacency(current);
					}
					break;
				case DOWN:
					if (grid[current.getRow() + 1][current.getCol()].getRoomChar() == roomChar) {
						cell.addAdjacency(current);
					}
					break;
				case NONE:
					break;
				}
			}

			// checks the secret passages that should be added to the adjacency list
			for (BoardCell secret : secretPassageCell)
				if (secret.getRoomChar() == roomChar) {
					Room temp = roomMap.get(secret.getSecretPassage());
					cell.addAdjacency(temp.getCenterCell());
				}
		}

		// checking if we should add adjacent walkways if they exist
		Room temp = roomMap.get(roomChar);
		int rowBelow = cell.getRow() - 1;
		int rowAbove = cell.getRow() + 1;
		int colLeft = cell.getCol() - 1;
		int colRight = cell.getCol() + 1;

		if (temp.getName().equals("Walkway")) {
			if (rowBelow > -1 && grid[rowBelow][cell.getCol()] != null
					&& roomMap.get(grid[rowBelow][cell.getCol()].getRoomChar()).getName().equals("Walkway")) {
				cell.addAdjacency(grid[rowBelow][cell.getCol()]);
			}
			if (colLeft > -1 && grid[cell.getRow()][colLeft] != null
					&& roomMap.get(grid[cell.getRow()][colLeft].getRoomChar()).getName().equals("Walkway")) {
				cell.addAdjacency(grid[cell.getRow()][colLeft]);
			}
			if (cell.getCol() + 1 < cols && grid[cell.getRow()][colRight] != null
					&& roomMap.get(grid[cell.getRow()][colRight].getRoomChar()).getName().equals("Walkway")) {
				cell.addAdjacency(grid[cell.getRow()][colRight]);
			}
			if (cell.getRow() + 1 < rows && grid[rowAbove][cell.getCol()] != null
					&& roomMap.get(grid[rowAbove][cell.getCol()].getRoomChar()).getName().equals("Walkway")) {
				cell.addAdjacency(grid[rowAbove][cell.getCol()]);
			}
		}

		// adding room center cells if we are at a doorway
		if (cell.isDoorway()) {
			switch (cell.getDoorDirection()) {
			case LEFT:
				temp = roomMap.get(grid[cell.getRow()][cell.getCol() - 1].getRoomChar());
				cell.addAdjacency(temp.getCenterCell());
				break;
			case RIGHT:
				temp = roomMap.get(grid[cell.getRow()][cell.getCol() + 1].getRoomChar());
				cell.addAdjacency(temp.getCenterCell());
				break;
			case UP:
				temp = roomMap.get(grid[cell.getRow() - 1][cell.getCol()].getRoomChar());
				cell.addAdjacency(temp.getCenterCell());
				break;
			case DOWN:
				temp = roomMap.get(grid[cell.getRow() + 1][cell.getCol()].getRoomChar());
				cell.addAdjacency(temp.getCenterCell());
				break;
			case NONE:
				break;
			}
		}

		// returns the adjacency list
		return cell.getAdjList();
	}

	public Set<BoardCell> getTargets() {
		return targets;
	}

	public void calcTargets(BoardCell startCell, int steps) {
		// clear old targets and visited lists
		targets.clear();
		visited.clear();
		recursiveTargets(startCell, steps);
		if (allPlayers.size() > playerCount) {
			if (allPlayers.get(playerCount).getMovedbySuggestion() == true) {
				targets.add(getCell(allPlayers.get(playerCount).getRow(), allPlayers.get(playerCount).getCol()));
			} else {
				targets.remove(getCell(allPlayers.get(playerCount).getRow(), allPlayers.get(playerCount).getCol()));
			}
		}
	}

	private void recursiveTargets(BoardCell startCell, int pathlength) {
		Board board = Board.getInstance();
		int r = startCell.getRow();
		int c = startCell.getCol();

		// gets adjacent list of the startCell
		Set<BoardCell> adj = board.getAdjList(r, c);
		Iterator<BoardCell> it = adj.iterator();
		while (it.hasNext()) {
			BoardCell adjacentCell = it.next();

			// checks the visited list for the adjacentCell
			if (visited.contains(adjacentCell)) {
				continue;
			} else {
				visited.add(startCell);
			}

			// checks if pathlength is 1 and the space is not occupied to add it to the
			// target list
			if ((pathlength == 1 || adjacentCell.isRoomCenter())
					&& (!adjacentCell.isOccupied() || adjacentCell.isRoomCenter())) {
				targets.add(adjacentCell);

				// otherwise we go down a step and start again
			} else if (!adjacentCell.isRoomCenter() && !adjacentCell.isOccupied()) {
				recursiveTargets(adjacentCell, pathlength - 1);
			}
			visited.remove(adjacentCell);
		}
	}

	//GOOD: returns players
	public ArrayList<Card> getPeople(){
		return peopleDeck;
	}

	//GOOD: returns weapons
	public ArrayList<Card> getWeapon(){
		return weapons;
	}

	//GOOD: returns rooms
	public ArrayList<Card> getRooms() {
		return rooms;
	}

	public ArrayList<Card> getCards() {
		return cardDeck;
	}


	public ArrayList<Player> getPlayers() {
		return allPlayers;
	}

	// TODO for testing purposes only
	public void addPlayer(Player p) {
		allPlayers.add(p);
	}

	// TODO for testing purposes only
	public void setPlayers(ArrayList<Player> players) {
		this.allPlayers = players;
	}

	//TODO for testing purposes only
	public void setSolution(ArrayList<Card> arrCards) {
		solution.clear();
		solution.add(arrCards.get(0));
		solution.add(arrCards.get(1));
		solution.add(arrCards.get(2));
	}

	public void createSolution() {
		// use the shuffle function to shuffle the cards and distribute them among players and delete the card after assigned to player
		ArrayList<Card> personTempDeck = personDeck;
		ArrayList<Card> weaponTempDeck = weaponDeck;
		ArrayList<Card> roomTempDeck = roomDeck;

		Collections.shuffle(personTempDeck);
		solution.add(personTempDeck.get(0));
		personTempDeck.remove(0);

		Collections.shuffle(roomTempDeck);
		solution.add(roomTempDeck.get(0));
		roomTempDeck.remove(0);

		Collections.shuffle(weaponTempDeck);
		solution.add(weaponTempDeck.get(0));
		weaponTempDeck.remove(0);
	}

	public ArrayList<Card> getSolution() {
		return solution;
	}

	public Map<Player, ArrayList<Card>> getCardsDealt() {
		return cardsDealt;
	}

	// give all players their cards
	public void dealPlayers() {
		cardsDealt.clear();
		ArrayList<Card> allCards = new ArrayList<Card>();
		allCards.addAll(personDeck);
		allCards.addAll(weaponDeck);
		allCards.addAll(roomDeck);
		// use the shuffle function to shuffle the cards and distrbute them amoung
		// players
		// delete the card after assigned to player
		for (Player p : allPlayers) {
			ArrayList<Card> temp = new ArrayList<Card>();
			Collections.shuffle(allCards);
			temp.add(allCards.get(0));
			p.updateHand(allCards.get(0));
			allCards.remove(0);

			Collections.shuffle(allCards);
			temp.add(allCards.get(0));
			p.updateHand(allCards.get(0));
			allCards.remove(0);

			Collections.shuffle(allCards);
			temp.add(allCards.get(0));
			p.updateHand(allCards.get(0));
			allCards.remove(0);

			// put player and their cards into a map
			cardsDealt.put(p, temp);
		}

	}

	// check the cards in the accusation against our solution
	public Boolean checkAccusation(ArrayList<Card> accusation) {
		for(int i = 0; i < 3; i++) {
			if(!(solution.contains(accusation.get(i)))){
				return false;
			}
		}
		return true;
	}

	// has every player on the board handle a suggestion
	public Card handleSuggestion(Player accuser, ArrayList<Card> suggest) {
		// make an arrayList of the players without the accuser
		accuser.setMovedbySuggestion(false);
		ArrayList<Player> searchArr = new ArrayList<Player>(allPlayers);
		searchArr.remove(accuser);
		for (Player p : searchArr) {
			if (p.disproveSuggestion(suggest) != null) {
				// if they show a card to disprove the suggestion, add it to seen list and
				// return it
				this.disprovedBy = p;
				return p.disproveSuggestion(suggest);
			}
		}
		return null;
	}

	// draws the board and players
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				grid[i][j].draw(g);
			}
		}
		for (Player p : this.getPlayers()) {
			p.draw(g);
		}
	}

	// determine the length of the cells on the board
	public int getCellLength() {
		Board board = Board.getInstance();
		Dimension boardSize = board.getSize();
		int size = (int) Math.min(boardSize.getWidth(), boardSize.getHeight());
		int dimSize = Math.max(board.getNumColumns(), board.getNumRows());
		int length = size / dimSize;
		return length;
	}

	// increments the player count
	public int playerCount() {
		if (playerCount >= allPlayers.size() - 1) {
			playerCount = 0;
			return playerCount;
		} else {
			playerCount++;
		}
		return playerCount;
	}

	public void addTarget(BoardCell boardCell) {
		targets.add(boardCell);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		boardClicked(e.getX(), e.getY());
	}

	// moves the human player to selected location
	public void boardClicked(int x, int y) {
		if (allPlayers.get(playerCount).isHuman() == true) {

			Dimension boardSize = getSize();
			int size = (int) Math.min(boardSize.getWidth() / getNumColumns(), boardSize.getHeight() / getNumColumns());
			BoardCell cell = null;

			int r = (int) (y / size);
			int c = (int) (x / size);

			if (r < rows && c < cols) {
				cell = getCell(r, c);
			}
			if (cell != null) {
				// ensures that if a room cell is clicked on, the player will move to the room
				// center and make a common suggestion
				if (cell.isRoom()) {
					BoardCell center = getRoom(cell.getRoomChar()).getCenterCell();
					r = center.getRow();
					c = center.getCol();
					cell = getCell(r, c);
				}

				BoardCell playerLoc = new BoardCell(allPlayers.get(playerCount).getRow(), allPlayers.get(playerCount).getCol());
				calcTargets(playerLoc, GameControlPanel.getDice());
				targets.remove(playerLoc);

				// sets the drawPlayer to the room center cell regardless of which room cell is
				// selected
				if (cell.isRoom()) {
					BoardCell center = getRoom(cell.getRoomChar()).getCenterCell();
					r = center.getRow();
					c = center.getCol();
				}
				cell = getCell(r, c);

				if (getTargets().contains(cell)) {
					// indicate they moved by themselves and draw them
					allPlayers.get(playerCount).setMovedbySuggestion(false);
					drawPlayer(cell);
				} else {
					JOptionPane.showMessageDialog(this, "Not a valid move, please select a highlighted cell!");
				}
			}
		}
	}

	// redraws the players to the moved spot
	public void drawPlayer(BoardCell cell) {
		// TODO stored original cell just in case. remove before final submission?
		BoardCell originalCell = getCell(allPlayers.get(playerCount).getRow(), allPlayers.get(playerCount).getCol());
		originalCell.setNumPlayers(originalCell.getNumPlayers()-1);
		if (!originalCell.hasMorePlayers()) {
			originalCell.setOccupied(false);
		}else {
			if(originalCell.getNumPlayers()==1) {
				originalCell.setHasMorePlayers(false);
			}
		}

		allPlayers.get(playerCount).setCol(cell.getCol());
		allPlayers.get(playerCount).setRow(cell.getRow());
		BoardCell newCell = getCell(allPlayers.get(playerCount).getRow(), allPlayers.get(playerCount).getCol());
		originalCell.setNumPlayers(originalCell.getNumPlayers()+1);
		if (newCell.isRoom()) {
			newCell = roomMap.get(newCell.getRoomChar()).getCenterCell();
		}
		if (newCell.isOccupied()) {
			newCell.setHasMorePlayers(true);
		} else {
			newCell.setOccupied(true);
		}
		repaint();
		if (allPlayers.get(playerCount).isHuman() == true) {
			hasMoved = true;
		}

		
		
		// suggestion calling
		if (cell.isRoom() && allPlayers.get(playerCount).isHuman()) {
			Room c = getMap().get(cell.getRoomChar());
			Card card = new Card(c.getName(), CardType.ROOM);
			Suggestion suggest = new Suggestion(card);
			// call createCardsPanel on your instacne variable
		}

		if (cell.isRoom() && allPlayers.get(playerCount).isHuman() == false) {

			Room c = getMap().get(cell.getRoomChar());
			Card roomCard = new Card(c.getName(), CardType.ROOM);
			ArrayList<Card> suggest = new ArrayList<Card>();
			suggest.add(roomCard);

			Card person = null;
			Card weapon = null;

			for (Card p: getPlayers().get(playerCount).getUnseen()) {
				if (p.getType() == CardType.WEAPON) {
					weapon = p;
				}

				if (p.getType() == CardType.PERSON) {
					person = p;
				}

			}
			suggest.add(weapon);
			suggest.add(person);


			Player current = getPlayers().get(playerCount);
			Card disproveSuggestion = handleSuggestion(current, suggest); 
			GameControlPanel.setGuess(roomCard.cardName + ", " + person.getName() + ", " + weapon.getName());

			if (disproveSuggestion == null) {
				GameControlPanel.setGuessResult("No New Clue");
			}else {
				current.updateSeen(disproveSuggestion);
				GameControlPanel.setGuessResult("Suggestion Disproven");
			}

			//TODO moved accused player to room and do not draw on top of players
			for (Player p : getPlayers()) {
				if (p.getName().equals(person.getName())) {
					getCell(current.getRow(), current.getCol()).setHasMorePlayers(true);
					p.setRow(current.getRow());
					p.setCol(current.getCol());
					p.setMovedbySuggestion(true);
					calcTargets(getCell(current.getRow(), current.getCol()), GameControlPanel.getDice());
				}
			}
		}
	}
	
	

	public ArrayList<Card> getWeaponCards() {
		return weaponDeck;
	}

	public ArrayList<Card> getPeopleCards() {
		return personDeck;
	}

	public ArrayList<Card> getRoomCards() {
		return roomDeck;
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	public Card getCard(String cardName) {
		for(int i = 0; i<cardDeck.size(); i++) {
			if(cardDeck.get(i).getName() == cardName) {
				return cardDeck.get(i);
			}
		}
		return null;
	}

	public boolean getHasMoved() {
		return hasMoved;
	}

	public void setHasMoved(boolean hasMoved) {
		this.hasMoved = hasMoved;
	}

	// computer accusation handling
	public void accusation(ArrayList<Card> accusation) {
		// TODO Testing Line
		System.out.println(solution);
		if(allPlayers.get(playerCount).isHuman()) {
				if(checkAccusation(accusation)) {
					//accusation correct
					JOptionPane pane = new JOptionPane();
					pane.setMessage("Solution was: " + 
							getSolution().get(0).getName() + ", " +
							getSolution().get(1).getName() + ", " +
							getSolution().get(2).getName() + ", You win!");
					JDialog dialog = pane.createDialog(this, "You win!");
					dialog.setVisible(true);
				}else {
					//accusation incorrect
					JOptionPane pane = new JOptionPane();
					pane.setMessage("Solution was: " + 
							getSolution().get(0).getName() + ", " +
							getSolution().get(1).getName() + ", " +
							getSolution().get(2).getName() + ", You lose!");
					JDialog dialog = pane.createDialog(this, "you lose!");
					dialog.setVisible(true);
				}
		}
	}
	public void updateCardPanel() {
		pan.createCardsPanel();
	}

	public int getPlayerCount() {
		// TODO Auto-generated method stub
		return playerCount;
	}
}
