package cluegame;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Accusation extends JFrame {

	private JComboBox <String> peopleCards, weaponCards, roomCards;
	private JTextField peopleLabel, weaponLabel, roomLabel;
	private JButton submit, cancel;
	private Card roomCard, disproveSuggestion;
	private ArrayList<Card> suggest = new ArrayList<Card>();
	private Board board = Board.getInstance();

	public Accusation () {
		setVisible(true);
		setTitle("Make A Accusation");
		setSize(500,150);
		setLayout(new GridLayout(0,2));
		create();
	}

	// TODO creating the options always leaves out the solution cards.  Definitely look into that!
	private void create() {
		//setup the current room and labels
		roomLabel = new JTextField("Room");
		peopleLabel = new JTextField("Person");
		weaponLabel = new JTextField("Weapon");
		
		//setup the room accusation
		roomCards = new JComboBox<String>();
		for (Card p: board.getRooms()) {
			roomCards.addItem(p.getName());
		}
		add(new JPanel().add(roomLabel));
		add(roomCards);
		
		//setup the person accusation
		peopleCards = new JComboBox <String>();
		for (Card p: board.getPeople()) {
			peopleCards.addItem(p.getName());
		}
		add(new JPanel().add(peopleLabel));
		add(peopleCards);

		//sets up the weapon accusation
		weaponCards = new JComboBox<String>();
		for (Card p: board.getWeapon()) {
			weaponCards.addItem(p.getName());
		}
		add(new JPanel().add(weaponLabel));
		add(weaponCards);
		
		submit = new JButton("Submit");
		cancel = new JButton("Cancel");
		
		//when submitted accusation is check to either be false or true
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Card selectedRoom =  board.getCard(roomCards.getItemAt(roomCards.getSelectedIndex()));
				Card selectedPerson =  board.getCard(peopleCards.getItemAt(peopleCards.getSelectedIndex()));
				Card selectedWeapon =  board.getCard(weaponCards.getItemAt(weaponCards.getSelectedIndex()));
				ArrayList<Card> selection = new ArrayList<Card>();
				selection.add(selectedRoom);
				selection.add(selectedPerson);
				selection.add(selectedWeapon);
				board.accusation(selection);
				setVisible(false);
			}
		});
		
		//closes window when selected cancel
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		add(submit);
		add(cancel);
	}
}
