package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.*;

import cluegame.*;

public class GameSolutionTest {
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


	// checks accusations made by players run as expected
	// solution always represented with index(0) = person, index(1) = room, index(2) = weapon
	@Test
	public void checkAccusation() {
		ArrayList<Card> soln = board.getSolution();

		// ensure false return when solution has wrong person
		Card badPerson = new Card("Bad Person", CardType.PERSON);
		ArrayList<Card> accusation1 = new ArrayList<Card>();
		accusation1.add(badPerson);
		accusation1.add(soln.get(1));
		accusation1.add(soln.get(2));
		assertFalse(board.checkAccusation(accusation1));

		// ensure false return when solution has wrong room
		Card badRoom = new Card("Bad Room", CardType.ROOM);
		ArrayList<Card> accusation2 = new ArrayList<Card>();
		accusation2.add(soln.get(0));
		accusation2.add(badRoom);
		accusation2.add(soln.get(2));
		assertFalse(board.checkAccusation(accusation2));	

		// ensure false return when solution has wrong weapon
		Card badWeapon = new Card("Bad Weapon", CardType.WEAPON);
		ArrayList<Card> accusation3 = new ArrayList<Card>();
		accusation3.add(soln.get(0));
		accusation3.add(soln.get(1));
		accusation3.add(badWeapon);
		assertFalse(board.checkAccusation(accusation3));

		// ensures that solution returns true
		assertTrue(board.checkAccusation(soln));	
	}

	// returns a players card to disprove a suggestion made by another player
	@Test
	public void disproveSuggestion() {
		
		//setSolution intentionally to ensure test functionality
		ArrayList<Card> adjustedSoln = new ArrayList<Card>();
		Card testPerson = new Card("Snoop", CardType.PERSON);
		adjustedSoln.add(testPerson);
		Card testRoom = new Card("White Room", CardType.ROOM);
		adjustedSoln.add(testRoom);
		Card testWeapon = new Card("Flamethrower", CardType.WEAPON);
		adjustedSoln.add(testWeapon);
		board.setSolution(adjustedSoln);
		ArrayList<Card> soln = board.getSolution();

		Player tempPlayer = new Player("Bobby", Color.black, 8, 0 );
		tempPlayer.setCards(new Card("Brad", CardType.PERSON), new Card("Pink Room", CardType.ROOM), new Card("Magic Wand", CardType.WEAPON));

		// player has no matching cards
		assertEquals(tempPlayer.disproveSuggestion(soln), null);
		
		
		tempPlayer.clearHand();
		// player has one matching card which is the person card: person snoop
		tempPlayer.setCards(testPerson, new Card("Blue Room", CardType.ROOM), new Card("Da Glock", CardType.WEAPON));
		assertEquals(tempPlayer.disproveSuggestion(soln), soln.get(0));

		tempPlayer.clearHand();
		// player has two matching cards which is the person card and the room card: person snoop, room white room
		tempPlayer.setCards(testPerson, testRoom, new Card("Magic Wand", CardType.WEAPON));
		assertTrue(tempPlayer.disproveSuggestion(soln) == soln.get(0) || tempPlayer.disproveSuggestion(soln) == soln.get(1));
	}

	// describes the functionality for how players should disprove a suggestion
	@Test
	public void handleSuggestion() {
		ArrayList<Card> soln = board.getSolution(); //holds same values as above test Snoop, White Room, Flamethrower
		
		// setup players and their cards
		Card bradCard = new Card("Brad", CardType.PERSON);
		Card wandCard = new Card("Magic Wand", CardType.WEAPON);
		Card redCard = new Card("Red Room", CardType.ROOM);
		Card pinkCard = new Card("Pink Room", CardType.ROOM);

		HumanPlayer anotherPlayer = new HumanPlayer("Bobby", Color.black, 8, 0 );
		anotherPlayer.setCards(bradCard, pinkCard, wandCard);
		
		Player accusingPlayer = new Player("Guy Ferri", Color.red, 25, 8 );
		accusingPlayer.setCards(new Card("Chad", CardType.PERSON), redCard, new Card("Orange Room", CardType.ROOM));
		
		Player nonAccusingPlayer = new Player("Koolaid Man", Color.pink, 0, 7 );
		nonAccusingPlayer.setCards(new Card("Jake", CardType.PERSON), new Card("Black Room", CardType.ROOM), new Card("Blue Room", CardType.ROOM));
		
		ArrayList<Player> poolOfPlayers = new ArrayList<Player>();
		
		poolOfPlayers.add(accusingPlayer);
		poolOfPlayers.add(nonAccusingPlayer);
		poolOfPlayers.add(anotherPlayer);
		
		board.setPlayers(poolOfPlayers);
		
		// ensure no players can disprove this suggestion
		assertEquals(null , board.handleSuggestion(accusingPlayer, soln));
		
		// ensure only accusing player can disprove
		accusingPlayer.setCards(soln.get(0), new Card("Green Room", CardType.ROOM), wandCard);
		assertEquals(board.handleSuggestion(accusingPlayer, soln), null);

		// ensure only non-accusing player can disprove
		nonAccusingPlayer.setCards(bradCard, pinkCard, soln.get(2));
		accusingPlayer.setCards(new Card("Chad", CardType.PERSON), redCard, new Card("Purple Room", CardType.ROOM));
		assertEquals(board.handleSuggestion(accusingPlayer, soln), soln.get(2));
		
		anotherPlayer.setCards(new Card("Oprah", CardType.PERSON), soln.get(1), wandCard);
		
		// ensure that two players can disprove but correct player returns answer both nonAccusingPlayer and anotherPlayer can disprove but anotherPlayer is correct 
		assertEquals(board.handleSuggestion(accusingPlayer, soln), soln.get(2));
		
	}
}
