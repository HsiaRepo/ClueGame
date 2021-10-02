package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import cluegame.*;

class GameSetupTests {

	private static Board board;

	@BeforeAll
	public static void setUp() {
		// Board is singleton, get the only instance
		board = Board.getInstance();
		// set the file names to use my config files
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		// Initialize will load config files
		board.initialize();
	}

	// test player attributes after reading ClueSetup.txt
	@Test
	public void testReadInPlayers() {
		ArrayList<Player> allPlayers = board.getPlayers();
		assertEquals(allPlayers.size(), 6);
		assertTrue(allPlayers.get(5).getName().contentEquals("Chad"));
		assertTrue(allPlayers.get(0).getName().contentEquals("Drake"));
		assertTrue(allPlayers.get(3).getColor() == Color.yellow);
		assertTrue(allPlayers.get(4).getColor() == Color.black);
	}

	// test if we have the right computer and human players
	@Test
	public void testPlayerType() {
		ArrayList<Player> allPlayers = board.getPlayers();
		int humans = 0;
		int computers = 0;
		for (Player p : allPlayers) {
			if (p instanceof HumanPlayer) {
				humans++;
			}else {
				computers++;
			}
		}

		// test for 1 human player
		assertEquals(humans, 1);
		// test for 5 computer players
		assertEquals(computers, 5);
		// the first player should a human player
		assertTrue(allPlayers.get(0) instanceof HumanPlayer);
		assertTrue(allPlayers.get(3) instanceof ComputerPlayer);
		assertTrue(allPlayers.get(1) instanceof ComputerPlayer);
	}
	
	// tests if the deck has the right number of cards of each type
	@Test
	public void testDeck() {
		ArrayList<Card> deck = board.getCards();
		int numPerson = 0;
		int numRooms = 0;
		int numWeapons = 0;

		for (Card c : deck) {
			if (c.type == CardType.PERSON) {
				numPerson++;
			}
			if (c.type == CardType.WEAPON) {
				numWeapons++;
			}
			if (c.type == CardType.ROOM) {
				numRooms++;
			}
		}

		assertEquals(deck.size(), 21);
		assertEquals(numPerson, 6);
		assertEquals(numRooms, 9);
		assertEquals(numWeapons, 6);

	}

	// tests to ensure cards are dealt properly
	@Test
	public void testDeal() {
		int cardCount = board.getCards().size();
		board.createSolution();
		
		// testing that the solution has all different card types
		ArrayList<Card> solution = board.getSolution();
		assertNotEquals(solution.get(0).type, solution.get(1).type);
		assertNotEquals(solution.get(0).type, solution.get(2).type);
		assertNotEquals(solution.get(1).type, solution.get(2).type);

		ArrayList<Player> allPlayers = board.getPlayers();
		int cardsPerPlayer = (cardCount - 3) / allPlayers.size();
		board.dealPlayers();

		Map<Player, ArrayList<Card>> cardsDealt = board.getCardsDealt();
		
		//test unique cards and ensure the correct number by using a set
		Set<Card>uniqueCards = new HashSet<Card>();
		// check that all players have the correct number of cards
		for (Player p: cardsDealt.keySet()) {
			assertEquals(cardsDealt.get(p).size(), 3);
			for (Card c: cardsDealt.get(p)) {
				uniqueCards.add(c);
			}
		}
		
		assertEquals(uniqueCards.size(), 18);
	}

}
