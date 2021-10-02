package cluegame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class ClueGame extends JFrame {

	static Board board = Board.getInstance();
	
	public ClueGame() {
		JOptionPane.showMessageDialog(null, "                            You are Drake \n "
				+ "Can you find the solution before the Computer Players? "
				+ "\n        Your color is red, click next to begin playing!");

		// game setup default operations
		//Board board = Board.getInstance();
		board.addMouseListener(board);
		setSize(800, 800);
		setTitle("Clue Game");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		// draws board
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		board.initialize();
		board.repaint();
		add(board, BorderLayout.CENTER);
		add(addCardPanel(), BorderLayout.EAST);
		add(addGameControlPanel(), BorderLayout.SOUTH);
		// add the known cards panel to your board

	}

	// draws card panel
	public static JPanel addCardPanel() {
		// TODO adds human player Kyle in order to draw cards panel.  Move to initialize before final submission?
		Player p = board.getPlayers().get(0);
		CardPanel card = new CardPanel(p);
		// formats the card panel
		card.setLayout(new GridLayout(0, 1));
		card.setBorder(BorderFactory.createLineBorder(Color.black));
		board.setPan(card);
		return card;
	}

	// draws the control panel
	public static JPanel addGameControlPanel() {
		GameControlPanel game1 = new GameControlPanel();
		game1.setLayout(new GridLayout(0, 3));
		return game1;
	}
	
	public static void main(String[] args) {
		ClueGame game = new ClueGame();
		game.setVisible(true);
	}
}