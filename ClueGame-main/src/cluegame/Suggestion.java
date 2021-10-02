package cluegame;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Suggestion extends JFrame {

	private JComboBox <String> peopleCards, weaponCards, roomCards;
	private JTextField peopleLabel, weaponLabel, roomLabel;
	private JButton submit, cancel;
	private Card roomCard, disproveSuggestion;
	private ArrayList<Card> suggest = new ArrayList<Card>();
	private Board board = Board.getInstance();

	public Suggestion (Card roomCard) {
		setVisible(true);
		setTitle("Make A Suggestion");
		setSize(500,150);
		setLayout(new GridLayout(0,2));
		this.roomCard = roomCard;
		create(roomCard);
	}

	private void create(Card roomCard) {
		//setup the current room and labels
		roomLabel = new JTextField("Current Room");
		roomLabel.setEditable(false);
		peopleLabel = new JTextField("Person");
		weaponLabel = new JTextField("Weapon");
		roomCards = new JComboBox<String>();
		//setup the room suggestion
		add(new JPanel().add(roomLabel));
		roomCards.setEditable(false);
		add(new JLabel(roomCard.cardName), BorderLayout.CENTER);

		//setup the person suggestion
		peopleCards = new JComboBox <String>();
		for (Card p: board.getPeople()) {
			peopleCards.addItem(p.getName());
		}
		add(new JPanel().add(peopleLabel));
		add(peopleCards);

		//sets up the weapon suggestion
		weaponCards = new JComboBox<String>();
		for (Card p: board.getWeapon()) {
			weaponCards.addItem(p.getName());
		}
		add(new JPanel().add(weaponLabel));
		add(weaponCards);

		submit = new JButton("Submit");
		cancel = new JButton("Cancel");

		//when submit is pressed the suggestion gets handled and then printed on game control panel
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				suggest.add(roomCard);
				Card person = new Card(peopleCards.getSelectedItem().toString(), CardType.PERSON);
				Card weapon = new Card(weaponCards.getSelectedItem().toString(), CardType.WEAPON);
				board.getPlayers();
				suggest.add(person);
				suggest.add(weapon);
				Player current = board.getPlayers().get(0);
				disproveSuggestion = board.handleSuggestion(current, suggest); 
				GameControlPanel.setGuess(roomCard.cardName + ", " + peopleCards.getSelectedItem().toString() + ", " + weaponCards.getSelectedItem().toString());

				if (disproveSuggestion == null && current.seen.contains(disproveSuggestion) == false) {
					GameControlPanel.setGuessResult("No New Clue");
				}else {
//					current.updateSeen(disproveSuggestion);
					current.seen.add(disproveSuggestion);
					GameControlPanel.setGuessResult("Card: " + disproveSuggestion.getName().toString() + " By Player: " + board.disprovedBy.getName());
					current.removeFromUnseen(disproveSuggestion);
					board.updateCardPanel();
				}
				
				//TODO moved accused player to room and do not draw on top of players
				for (Player p : board.getPlayers()) {
					if (p.getName().equals(person.getName())) {
						p.setRow(current.getRow());
						p.setCol(current.getCol());
						board.repaint();
						BoardCell cell = new BoardCell(current.getRow(),current.getCol());
						board.calcTargets(cell, GameControlPanel.getDice());
					}
				}
				setVisible(false);
			}
		});

		//exits when cancel is selected
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		add(submit);
		add(cancel);
	}
}
