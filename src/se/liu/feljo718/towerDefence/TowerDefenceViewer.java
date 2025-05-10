package se.liu.feljo718.towerdefence;

import se.liu.feljo718.towerdefence.board.Board;
import se.liu.feljo718.towerdefence.board.BoardComponent;
import se.liu.feljo718.towerdefence.board.BoardListener;
import se.liu.feljo718.towerdefence.handler.LogHandler;
import se.liu.feljo718.towerdefence.highscore.Highscore;
import se.liu.feljo718.towerdefence.highscore.HighscoreList;
import se.liu.feljo718.towerdefence.viewer.MainMenu;
import se.liu.feljo718.towerdefence.viewer.MenuOptions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.logging.Level;

/**
 * Main viewer component that handles the UI display for the Tower Defense game.
 * <p>
 * This class implements the BoardListener interface to receive notifications when the game state changes. It manages the game window, menu
 * system, and the animation timer that drives the game loop. The viewer also handles various UI-related events like pausing, restarting,
 * and exiting the game.
 *
 * @author feljo718
 * @see Board
 * @see BoardListener
 * @see MainMenu
 */
public class TowerDefenceViewer implements BoardListener
{
    private static final int FRAME_RATE = 60;
    private static final int TIMER_DELAY = 1000 / FRAME_RATE;
    private static final String GAME_TITLE = "Tower Defence";
    private static final String PAUSE_TEXT = "Pause";
    private static final String RESUME_TEXT = "Resume";

    private final Board board;
    private final HighscoreList highscoreList;
    private final JFrame frame;

    private Timer clockTimer = null;
    private JMenuItem pauseButton;

    /**
     * Creates a new game viewer for the specified board.
     * <p>
     * Initializes the game window, registers as a listener to the board, and starts the game loop.
     *
     * @param board The game board to display and control
     */
    public TowerDefenceViewer(Board board) {
	if (board == null) {
	    throw new IllegalArgumentException("Board cannot be null");
	}

	this.board = board;
	this.board.addBoardListener(this);
	this.highscoreList = new HighscoreList();

	// Initialize UI
	this.frame = createFrame();
	startGame();
    }

    /**
     * Main entry point for the game
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
	SwingUtilities.invokeLater(() -> {
	    MainMenu menu = new MainMenu();
	    menu.initialize(); // First initialize the UI components
	    menu.show();      // Then make the menu visible
	    LogHandler.info(TowerDefenceViewer.class, "Application started");
	});
    }

    @Override public void boardChanged() {
	SwingUtilities.invokeLater(() -> frame.repaint());
    }

    @Override public void gameOver() {
	stopTick();
	SwingUtilities.invokeLater(this::handleGameOver);
    }

    /**
     * Handles the game over scenario by saving the score, showing highscores, and prompting for restart.
     */
    private void handleGameOver() {
	int score = board.getRound() - 1; // Use last completed round as score

	String playerName =
		JOptionPane.showInputDialog(frame, "Game Over! You reached round " + score + ".\nEnter your name for the highscore:",
					    "Game Over", JOptionPane.INFORMATION_MESSAGE);

	if (playerName != null && !playerName.trim().isEmpty()) {
	    try {
		highscoreList.addScore(new Highscore(playerName, score));
		showHighscores();
	    } catch (IOException e) {
		LogHandler.severe(TowerDefenceViewer.class, "Error loading image: " + e.getMessage());
		throw new UncheckedIOException("Failed to load required game resources", e);
	    }
	}

	// Ask if player wants to restart
	int choice = JOptionPane.showConfirmDialog(frame, "Do you want to start a new game?", "Game Over", JOptionPane.YES_NO_OPTION);

	if (choice == JOptionPane.YES_OPTION) {
	    restartGame();
	} else {
	    returnToMainMenu();
	}
    }

    /**
     * Starts the game timer that drives the animation loop.
     * <p>
     * Creates and starts a Swing Timer that calls the board's tick method based on the configured frame rate.
     */
    public void tick() {
	if (clockTimer == null) {
	    Action doOneStep = new AbstractAction()
	    {
		@Override public void actionPerformed(ActionEvent e) {
		    board.tick();
		}
	    };
	    clockTimer = new Timer(TIMER_DELAY, doOneStep);
	    clockTimer.setCoalesce(true);
	}
	clockTimer.start();
	LogHandler.fine(TowerDefenceViewer.class, "Game loop started");
    }

    /**
     * Creates and configures the main game window and user interface components.
     * <p>
     * Sets up the game display, menu bar, and action listeners for user interaction.
     *
     * @return The configured JFrame
     */
    private JFrame createFrame() {
	JFrame gameFrame = new JFrame(GAME_TITLE);
	gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	gameFrame.setLayout(new BorderLayout());

	// Add board component
	BoardComponent boardComponent = new BoardComponent(board);
	gameFrame.add(boardComponent, BorderLayout.CENTER);

	// Create menu bar
	gameFrame.setJMenuBar(createMenuBar());

	gameFrame.pack();
	return gameFrame;
    }

    /**
     * Creates the menu bar with all game options.
     *
     * @return The configured menu bar
     */
    private JMenuBar createMenuBar() {
	JMenuBar menuBar = new JMenuBar();

	// File menu
	JMenu fileMenu = new JMenu("File");
	fileMenu.add(createMenuItem("New Game", MenuOptions.NEW_GAME));
	fileMenu.add(createMenuItem("Return to Menu", MenuOptions.RETURN_TO_MENU));
	fileMenu.addSeparator();
	fileMenu.add(createMenuItem("Exit", MenuOptions.QUIT));

	// Game menu
	JMenu gameMenu = new JMenu("Game");
	pauseButton = createMenuItem(PAUSE_TEXT, MenuOptions.PAUSE);
	gameMenu.add(pauseButton);
	gameMenu.add(createMenuItem("Restart", MenuOptions.RESTART));
	gameMenu.addSeparator();
	gameMenu.add(createMenuItem("Highscores", MenuOptions.HIGHSCORES));

	menuBar.add(fileMenu);
	menuBar.add(gameMenu);

	return menuBar;
    }

    /**
     * Makes the game window visible on the screen.
     * <p>
     * Centers the window and brings it to the foreground.
     */
    public void show() {
	frame.setLocationRelativeTo(null);
	frame.setVisible(true);
    }

    /**
     * Closes the game and exits properly
     */
    private void closeGame() {
	stopTick();
	frame.dispose();
	LogHandler.info(TowerDefenceViewer.class, "Game closed");
    }

    /**
     * Starts a new game with default board dimensions
     */
    private void startNewGame() {
	stopTick();
	frame.dispose();
	SwingUtilities.invokeLater(() -> {
	    Board newBoard = new Board(20, 15);
	    TowerDefenceViewer viewer = new TowerDefenceViewer(newBoard);
	    viewer.show();
	});
    }

    /**
     * Restarts the game with the same board dimensions
     */
    private void restartGame() {
	stopTick();
	frame.dispose();
	SwingUtilities.invokeLater(() -> {
	    // Create a fresh board with the same dimensions as the current one
	    Board newBoard = new Board(board.getWidth(), board.getHeight() - 1); // Subtract 1 for interface row
	    TowerDefenceViewer viewer = new TowerDefenceViewer(newBoard);
	    viewer.show();
	});
    }

    /**
     * Returns to the main menu
     */
    private void returnToMainMenu() {
	stopTick();
	frame.dispose();
	SwingUtilities.invokeLater(() -> {
	    MainMenu menu = new MainMenu();
	    menu.initialize(); // First initialize the UI components
	    menu.show();      // Then make the menu visible
	});
    }

    /**
     * Toggles the pause state of the game
     */
    private void togglePause() {
	if (clockTimer != null) {
	    boolean isPausing = clockTimer.isRunning();

	    // Toggle timer state
	    if (isPausing) {
		clockTimer.stop();
	    } else {
		clockTimer.start();
	    }

	    // Update UI based on new state
	    pauseButton.setText(isPausing ? RESUME_TEXT : PAUSE_TEXT);
	    if (LogHandler.isLoggable(TowerDefenceViewer.class, Level.FINE)) {
		LogHandler.fine(TowerDefenceViewer.class, "Game " + (isPausing ? "paused" : "resumed"));
	    }
	}
    }

    /**
     * Shows the current highscores
     */
    private void showHighscores() {
	JOptionPane.showMessageDialog(frame, "Highscores:\n\n" + highscoreList, "Highscores", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Creates a menu item with the specified text and action
     *
     * @param text   The text to display
     * @param option The menu option to trigger
     *
     * @return The configured menu item
     */
    private JMenuItem createMenuItem(String text, MenuOptions option) {
	JMenuItem menuItem = new JMenuItem(text);
	menuItem.addActionListener(new MenuAction(option));
	return menuItem;
    }

    /**
     * Starts the game loop
     */
    public void startGame() {
	tick();
    }

    /**
     * Stops the game loop
     */
    public void stopTick() {
	if (clockTimer != null && clockTimer.isRunning()) {
	    clockTimer.stop();
	    LogHandler.fine(TowerDefenceViewer.class, "Game loop stopped");
	}
    }

    /**
     * Action handler for menu options in the game interface.
     * <p>
     * Processes user selections from the menu bar, such as quitting, starting a new game, or toggling the pause state.
     */
    private class MenuAction extends AbstractAction
    {
	private final MenuOptions option;

	private MenuAction(final MenuOptions option) {
	    this.option = option;
	}

	@Override public void actionPerformed(ActionEvent e) {
	    switch (option) {
		case QUIT:
		    closeGame();
		    break;
		case NEW_GAME:
		    startNewGame();
		    break;
		case RESTART:
		    restartGame();
		    break;
		case RETURN_TO_MENU:
		    returnToMainMenu();
		    break;
		case PAUSE:
		    togglePause();
		    break;
		case HIGHSCORES:
		    showHighscores();
		    break;
	    }
	}
    }

    @Override public void gameCompleted() {
	stopTick();
	SwingUtilities.invokeLater(this::handleGameCompletion);
    }

    /**
     * Handles the game completion scenario by saving the score, showing congratulations, and prompting for restart.
     */
    private void handleGameCompletion() {
	int score = board.getRound();

	String playerName =
		JOptionPane.showInputDialog(frame, "Congratulations! You've completed all levels!\nYour final score: " + score +
						   "\nEnter your name for the highscore:", "Victory!", JOptionPane.INFORMATION_MESSAGE);

	if (playerName != null && !playerName.trim().isEmpty()) {
	    try {
		highscoreList.addScore(new Highscore(playerName, score));
		showHighscores();
	    } catch (IOException e) {
		LogHandler.severe(TowerDefenceViewer.class, "Error saving highscore: " + e.getMessage());
		throw new UncheckedIOException("Failed to save highscore", e);
	    }
	}

	// Ask if player wants to restart
	int choice = JOptionPane.showConfirmDialog(frame, "Do you want to start a new game?", "Game Completed", JOptionPane.YES_NO_OPTION);

	if (choice == JOptionPane.YES_OPTION) {
	    restartGame();
	} else {
	    returnToMainMenu();
	}
    }
}