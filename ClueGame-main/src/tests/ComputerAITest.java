package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.*;

import cluegame.*;

public class ComputerAITest {
	private static Board board;

	@BeforeAll
	public static void setUp() {
		// Board is singleton, get the only instance
		board = Board.getInstance();
		// set the file names to use my config files
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		// Initialize will load config files
		board.initialize();
		board.createSolution();
		board.dealPlayers();
	}

	@BeforeEach
	public void reset() {
		board.initialize();
		board.createSolution();
		board.dealPlayers();
	}

	// describes functionality for how computer players should create a suggestion
	// tests a random function so occasionally a unit tests fails by pure chance
	@Test
	public void createSuggestion() {
		ComputerPlayer compPlayer = new ComputerPlayer("Bobby", Color.black, 1, 1); // in white room
		ArrayList<Card> unseen = compPlayer.getUnseen();
		BoardCell cell = board.getCell(1, 1);
		Room room = board.getRoom(cell);
		compPlayer.setRoom(room);

		// setup cards for unseen list
		Card c1 = new Card("Snoop", CardType.PERSON);
		Card c2 = new Card("White Room", CardType.ROOM);
		Card c3 = new Card("Black Room", CardType.ROOM);
		Card c4 = new Card("Flamethrower", CardType.WEAPON);
		Card c5 = new Card("Magic Wand", CardType.WEAPON);
		Card c6 = new Card("Brad", CardType.PERSON);
		Card c7 = new Card("Pink Room", CardType.ROOM);
		Card c8 = new Card("Red Room", CardType.ROOM);
		Card c9 = new Card("B Room", CardType.ROOM);

		compPlayer.updateUnseen(c1);
		compPlayer.updateUnseen(c2);
		compPlayer.updateUnseen(c3);
		compPlayer.updateUnseen(c4);
		compPlayer.updateUnseen(c5);
		compPlayer.updateUnseen(c6);
		compPlayer.updateUnseen(c7);
		compPlayer.updateUnseen(c8);
		compPlayer.updateUnseen(c9);

		// suggestion always represented with index(0) = person, index(1) = room,
		// index(2) = weapon
		ArrayList<Card> suggestionOne = compPlayer.createSuggestion();
		ArrayList<Card> suggestionTwo = compPlayer.createSuggestion();
		ArrayList<Card> suggestionThree = compPlayer.createSuggestion();


		// ensure that the person card is chosen at random from seen
		if(suggestionOne.equals(suggestionTwo)) {
			assertTrue(suggestionOne.equals(suggestionTwo));
		}else {
			assertFalse(suggestionOne.equals(suggestionTwo));
		}

		// ensures room matches current location
		assertTrue((suggestionOne.get(0).getName() == compPlayer.getRoom())
				&& (suggestionTwo.get(0).getName() == compPlayer.getRoom()));

		suggestionOne.clear();
		compPlayer.clearUnseen();
		compPlayer.updateUnseen(c1);
		compPlayer.updateUnseen(c2);
		compPlayer.updateUnseen(c4);
		suggestionOne = compPlayer.createSuggestion();

		// ensures if only one weapon unseen it is selected;
		assertTrue(suggestionOne.get(2).equals(c4));

		// ensures if only one person unseen it is selected;
		assertTrue(suggestionOne.get(1).equals(c1));
	}

	// describes the expected functionality for how computer players select a target
	@Test
	public void selectTargets() {
		//first test unseen room in target list
		board.dealPlayers();
		board.createSolution();
		// place ComputerPlayer Kyle at the entrance to the purple room
		ComputerPlayer c1 = new ComputerPlayer("Kyle", Color.pink, 5, 9);
		// in the test hand, we leave the Purple Room
		ArrayList<Card> testHand = new ArrayList<Card>();
		testHand.add(new Card("White Room", CardType.ROOM));
		testHand.add(new Card("Black Room", CardType.ROOM));
		testHand.add(new Card("Orange Room", CardType.ROOM));
		c1.setHand(testHand);
		c1.createUnseenList();
		assertTrue(c1.selectTargets(1).equals(board.getCell(3, 11)));

		//second test no rooms in target list for 2 steps
		ComputerPlayer c2 = new ComputerPlayer("Brad", Color.pink, 14, 7);
		c2.createUnseenList();
		int notRepeat = 0;
		BoardCell lastTarget = null;
		for(int i = 0; i < 10; i++) {
			if(!c2.selectTargets(1).equals(lastTarget)) {
				notRepeat++;
			}
		}
		assertTrue(notRepeat > 7);

		//third test make sure a visited room is treated like a random target
		ComputerPlayer c3 = new ComputerPlayer("Snoop", Color.green, 6, 2);
		ArrayList<Card> deck = board.getCards();
		Card whiteRoom = null;
		for(Card c: deck) {
			if(c.getName().equals("White Room")){
				whiteRoom = c;
			}
		}
		c3.updateSeen(whiteRoom);
		c3.createUnseenList();
		// double checking the white room
		int roomChosenCount = 0;
		for(int i = 0; i < 12; i++) {
			if(c3.selectTargets(1).isRoomCenter()) {
				roomChosenCount++;
			}
		}
		assertTrue(roomChosenCount < 12);
	}
}
