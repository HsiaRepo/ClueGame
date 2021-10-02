package cluegame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class GameControlPanel extends JPanel {
	private static JTextField whoseTurn, roll, guess, guessResult;
	private static JButton nextButton = new JButton("NEXT");
	private static JButton accusationButton = new JButton("Make Accusation");
	private static String diceRoll = new String();
	private static int dice = 0;
	private static Player current;
	private static Board board = Board.getInstance();

	public GameControlPanel() {
		setLayout(new GridLayout(2, 0));
		JPanel panel = new JPanel();
		panel = guess();
		add(panel);
		panel = create();
		add(panel);
		board.addMouseListener(null);
	}

	/*
	 * public static GameControlPanel getInstance() { return thisGameControlPanel; }
	 */

	public static Player getCurrent() {
		return current;
	}

	// updates text fields for GuessResult
	public static void setGuessResult(String string) {
		guessResult.setBackground(Color.GREEN);
		guessResult.setText(string);
	}

	// updates text fields for Guess
	public static void setGuess(String string) {
		guess.setBackground(Color.YELLOW);
		guess.setText(string);
	}

	// updates text fields for roll and whoseTurn
	private static void setTurn(Player p, int i) {
		roll.setText(Integer.toString(i));
		whoseTurn.setText(p.getName());
	}

	// creates the buttons and the fields for WhoseTurn and the fields for Roll
	private static JPanel create() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		panel.add(nextButton, BorderLayout.EAST);
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (board.getHasMoved()) {
					nextTurn();
				} else {
					JOptionPane pane = new JOptionPane();
					pane.setMessage("             Please make a move first!");
					JDialog dialog = pane.createDialog(pane, "Move Required!");
					dialog.setVisible(true);
				}
			}
		});

		panel.add(accusationButton, BorderLayout.EAST);
		accusationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (board.getPlayers().get(board.playerCount).isHuman()) {
					Accusation accusation = new Accusation();
				} else {
					JOptionPane pane = new JOptionPane();
					pane.setMessage("Please wait until it's your turn to make an accusation");
					JDialog dialog = pane.createDialog(pane, "Can't make accusation");
					dialog.setVisible(true);
				}
			}
		});
		panel.add(whoseTurn());
		panel.add(roll());
		return panel;
	}

	// creates the fields for whoseTurn
	private static JPanel whoseTurn() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		JLabel whoseTurnButton = new JLabel("Whose Turn?");
		whoseTurn = new JTextField(5);
		whoseTurn.setEditable(false);
		panel.add(whoseTurnButton, BorderLayout.EAST);
		panel.add(whoseTurn, BorderLayout.EAST);
		return panel;
	}

	// creates the fields for roll
	private static JPanel roll() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		JLabel rollLabel = new JLabel("Roll:");
		roll = new JTextField(diceRoll);
		roll.setEditable(false);
		panel.add(rollLabel);
		panel.add(roll);
		return panel;
	}

	// creates fields for guesses and guess result
	private static JPanel guess() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		JLabel guessLabel = new JLabel("Guess:");
		guess = new JTextField(2);
		guess.setEditable(false);
		panel.add(guessLabel, BorderLayout.CENTER);
		panel.add(guess, BorderLayout.CENTER);

		JLabel guessResultLabel = new JLabel("Guess Result:");
		guessResult = new JTextField(2);
		guessResult.setEditable(false);
		panel.add(guessResultLabel);
		panel.add(guessResult);
		return panel;
	}

	// randomly generates a dice roll
	public static int randDiceRoll() {
		int x = (int) Math.floor(Math.random() * (6) + 1);
		dice = x;
		return x;
	}

	// returns dice roll value
	public static int getDice() {
		return dice;
	}

	// updates current player
	// generates error if player has not moved
	public static void nextTurn() {
		current = board.getPlayers().get(board.playerCount());
		// testing lines that are needed currently
		if (current.isHuman()) {
			if (!board.getHasMoved()) {
				JOptionPane pane = new JOptionPane();
				pane.setMessage("Please make a move first!");
				JDialog dialog = pane.createDialog(pane, "Move Required!");
				dialog.setVisible(true);
			}
		}

		// sets the turn in the GameControlPanel
		int dieRoll = randDiceRoll();
		setTurn(current, dieRoll);
		BoardCell startCell = board.getCell(current.getRow(), current.getCol());
		board.calcTargets(startCell, dieRoll);

		// do the right turns for human/computer
		if (current.isHuman()) {
			// check if we can't move
			board.setHasMoved(false);
			if (board.getTargets().isEmpty()) {
				JOptionPane pane = new JOptionPane();
				pane.setMessage("No moves available this turn!");
				JDialog dialog = pane.createDialog(pane, "Error");
				dialog.setVisible(true);
				board.setHasMoved(true);
			} else {
				// processes human turn
				doHumanTurn(dieRoll);
			}
		} else {
			// processes computer turn
			doComputerTurn(dieRoll);
		}
		board.repaint();
	}

	// processes the human turn
	private static void doHumanTurn(int dieRoll) {
		board.calcTargets(board.getCell(current.getRow(), current.getCol()), dieRoll);
		for (BoardCell x : new ArrayList<BoardCell>(board.getTargets())) {
			x.targetCell(true);
		}
		// TODO Check that this works properly when our suggestion moves them to a new
		// cell.
		// if the player was previously moved by a suggestion, add their current cell as
		// a valid target
		if (current.getMovedbySuggestion()) {
			board.addTarget(board.getCell(current.getRow(), current.getCol()));
			board.getCell(current.getRow(), current.getCol()).targetCell(true);
		}
	}

	// processes the computer turn
	private static void doComputerTurn(int dieRoll) {
		if (board.getHasMoved()) {
			if (board.getPlayers().get(board.playerCount).isHuman() == false) {
				// checks computerAccusation at the beginning of the turn
				if (board.getPlayers().get(board.playerCount).isDisproven() == false) {
					if (((ComputerPlayer) board.getPlayers().get(board.playerCount)).computerAccusation()) {
						JOptionPane pane = new JOptionPane();
						pane.setMessage("player: " + board.getPlayers().get(board.playerCount).getName() + " wins"); // Configure
						JDialog dialog = pane.createDialog(board, "winner");
						dialog.setVisible(true);
					}
				}
				// gets the current players location and randomly moves them to one of generated
				// targets
				BoardCell playerLoc = new BoardCell(board.getPlayers().get(board.playerCount).getRow(),
						board.getPlayers().get(board.playerCount).getCol());
				board.calcTargets(playerLoc, GameControlPanel.getDice());
				Set<BoardCell> targetSet = board.getTargets();
				ArrayList<BoardCell> targetArray = new ArrayList<>(targetSet);
				int x = (int) Math.floor(Math.random() * (targetArray.size()));
				if (targetArray.size() > 0) {
					board.drawPlayer(targetArray.get(x));
				}
				// if the target was a room, handle the computer suggestion
				if (targetArray.size() > 0) {
					if (targetArray.get(x).isRoom()) {
						if (board.handleSuggestion(board.getPlayers().get(board.playerCount),
								((ComputerPlayer) board.getPlayers().get(board.playerCount))
										.createSuggestion()) != null) {
							board.getPlayers().get(board.playerCount).setDisproven(false);
						}
					}
				}
			}
		}
	}
}
