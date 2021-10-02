package cluegame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
 
public class CardPanel extends JPanel {
	private Player humanPlayer;
	private Board board;

	public CardPanel() {
		HumanPlayer p = new HumanPlayer("Player", Color.WHITE, 0, 0);
		initialize(p);
		JPanel cardPanel = new JPanel();
		createCardsPanel();
		//add(cardPanel);
	}

	public CardPanel(Player p) {
		initialize(p);
		JPanel cardPanel = new JPanel();
		createCardsPanel();
		//add(cardPanel);
		humanPlayer = p;
	}

	// setup 3 panels for the 3 types of cards we can have in hand
	public void createCardsPanel() {
		JPanel knownPanel = new JPanel();
		JLabel cardLabel = new JLabel("Known Cards");
		knownPanel.add(cardLabel, BorderLayout.CENTER);
		JPanel cardPanel = new JPanel();
		cardPanel.setLayout(new GridLayout(3, 0));
		cardPanel.add(createTypePanel("People: ", CardType.PERSON));
		cardPanel.add(createTypePanel("Rooms: ", CardType.ROOM));
		cardPanel.add(createTypePanel("Weapons: ", CardType.WEAPON));
		// found setPreferredSize from stack overflow
		cardPanel.setPreferredSize(new Dimension(150, 500));
		knownPanel.setPreferredSize(new Dimension(160, 600));
		knownPanel.add(cardPanel);
		removeAll();
		add(knownPanel);
		revalidate();
		//return knownPanel;
	}

	// method to create the card panel depending on CardType
	private JPanel createTypePanel(String border, CardType type) {
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new EtchedBorder(), border));
		panel.setLayout(new GridLayout(0, 1));

		int numCards = 0;
		panel.add(new JLabel("In Hand: "));
		// get all people in player hand
		for (Card c : humanPlayer.getCards()) {
			// for every card the human player holds of the chosen card type, we create a
			// new text field with its name and increment numCards
			if (c.type == type) {
				JTextField text = new JTextField(10);
				text.setText(c.getName());
				text.setEditable(false);
				// set the color of the cards in hand to player color
				Color col = humanPlayer.getColor();
				text.setBackground(col);
				panel.add(text);
				numCards++;
			}
		}
		// check if we don't have any cards of the chosen CardType
		if (numCards == 0) {
			JTextField text = new JTextField(10);
			text.setText("None");
			text.setEditable(false);
			panel.add(text);
		}

		panel.add(new JLabel("Seen: "));

		// reset numCards to do it again
		numCards = 0;
		for (Card c : humanPlayer.getSeen()) {
			// similar to above, check type matches and add it to the panel, but color is
			// different
			if (c.type == type) {
				JTextField text = new JTextField(10);
				text.setText(c.getName());
				text.setEditable(false);
				// to get the color of the seen card, we check every player to see if any are
				// holding it
				for (Player p : board.getPlayers()) {
					// get the player holding it
					if (p.getCards().contains(c)) {
						Color col = p.getColor();
						text.setBackground(col);
					}
				}
				// TODO testing print
				System.out.println("Adding card to seen");
				panel.add(text);
				numCards++;
			}
		}
		// check if there aren't any seen cards of the chosen CardType
		if (numCards == 0) {
			JTextField text = new JTextField(10);
			text.setText("None");
			text.setEditable(false);
			panel.add(text);
		}

		return panel;
	}

	public void updateCardPanel() {
		// used to remake the CardPanel
		removeAll();
		createCardsPanel();
		board.revalidate();
		board.repaint();
	}

	// added an initialize method because I can't set the board Instance in main
	public void initialize(Player p) {
		board = Board.getInstance();
		humanPlayer = p;
	}

	// main used for testing
	public static void main(String[] args) {
		Board board = Board.getInstance();
		HumanPlayer p = new HumanPlayer("Kyle", Color.pink, 0, 0);
		ComputerPlayer c1 = new ComputerPlayer("Chad", Color.orange, 1, 1);
		ComputerPlayer c2 = new ComputerPlayer("Jake", Color.red, 1, 2);
		// testing adding cards
		Card testWeapon = new Card("Flamethrower", CardType.WEAPON);
		Card testSecondWeapon = new Card("Da Glock", CardType.WEAPON);
		Card testThirdWeapon = new Card("Rusty Fishing Rod", CardType.WEAPON);
		Card testPerson = new Card("Kyle", CardType.PERSON);
		Card testRoom = new Card("White Room", CardType.ROOM);
		Card testSecondRoom = new Card("Blue Room", CardType.ROOM);
		// put the cards in the players hands
		c1.updateHand(testRoom);
		c2.updateHand(testWeapon);
		c2.updateHand(testSecondWeapon);
		// add them to the board
		board.addPlayer(c1);
		board.addPlayer(c2);
		// update humanPlayer's seen cards and hand
		p.updateSeen(testWeapon);
		p.updateSeen(testSecondWeapon);
		p.updateHand(testThirdWeapon);
		p.updateSeen(testPerson);
		p.updateSeen(testRoom);
		p.updateHand(testSecondRoom);

		Player current = board.getPlayers().get(board.playerCount());
		if (board.getPlayers().get(board.playerCount()).isHuman()) {
			for (Card c : board.getPlayers().get(board.playerCount()).getSeen()) {
				board.getPlayers().get(board.playerCount()).updateSeen(c);
			}

		}

		CardPanel panel = new CardPanel(p); // create the panel
		JFrame frame = new JFrame(); // create the frame
		frame.setContentPane(panel); // put the panel in the frame
		frame.setSize(180, 840); // size the frame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // allow it to close
		frame.setVisible(true); // make it visible
		panel.setLayout(new GridLayout(1, 4));
	}

}
	