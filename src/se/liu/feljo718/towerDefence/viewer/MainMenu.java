package se.liu.feljo718.towerdefence.viewer;

import com.google.gson.JsonSyntaxException;
import se.liu.feljo718.towerdefence.TowerDefenceViewer;
import se.liu.feljo718.towerdefence.board.Board;
import se.liu.feljo718.towerdefence.handler.LogHandler;
import se.liu.feljo718.towerdefence.highscore.HighscoreList;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Main menu interface for the Tower Defense game.
 * <p>
 * This class manages the primary user interface before entering the game, providing options for starting a new game, selecting maps,
 * configuring options, viewing high scores, and exiting the application.
 * <p>
 * The menu features a graphical background, styled buttons, and a responsive layout.
 *
 * @author feljo718
 * @see Board
 * @see TowerDefenceViewer
 */
public class MainMenu
{
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int BUTTON_PANEL_WIDTH = 300;
    private static final int BUTTON_PANEL_HEIGHT = 300;
    private static final int BUTTON_PANEL_OFFSET = 150;
    private static final int TITLE_LABEL_Y = 50;
    private static final int TITLE_LABEL_HEIGHT = 50;
    private static final int MENU_FONT_SIZE = 16;
    private static final int TITLE_FONT_SIZE = 36;
    private static final Color BUTTON_BACKGROUND = new Color(70, 70, 120);
    private static final int BUTTON_BORDER_THICKNESS = 2;
    private static final int BUTTON_PADDING = 10;
    private final JFrame frame;
    private JPanel buttonPanel = null;
    private String selectedMapName = "Default Map";

    /**
     * Creates and initializes the main menu interface.
     * <p>
     * Sets up the window dimensions, loads graphical resources, initializes the background, and arranges UI components.
     */
    public MainMenu() {
	LogHandler.info(MainMenu.class, "Creating MainMenu");
	frame = new JFrame();
    }

    /**
     * Initializes the UI components and sets up the menu. Call this method after constructing the MainMenu.
     */
    public void initialize() {
	LogHandler.info(MainMenu.class, "Initializing MainMenu UI components");
	frame.setTitle("Tower Defense");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
	frame.setLocationRelativeTo(null);

	// Create a custom JPanel with background painting
	frame.setContentPane(new JPanel()
	{
	    @Override protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Just draw the solid color background directly
		g.setColor(new Color(30, 30, 60));
		g.fillRect(0, 0, getWidth(), getHeight());
	    }
	});

	// Use a layout that respects size and position
	frame.getContentPane().setLayout(null);

	// Create button panel centered on screen
	createButtonPanel();
    }

    public void show() {
	LogHandler.info(MainMenu.class, "Displaying MainMenu");
	frame.setVisible(true);
    }

    /**
     * Creates and configures the panel containing menu buttons.
     * <p>
     * Sets up the main navigation buttons with consistent styling and positions them in the center of the window. Also adds a title label.
     */
    private void createButtonPanel() {
	LogHandler.fine(MainMenu.class, "Creating button panel");
	buttonPanel = new JPanel();
	buttonPanel.setLayout(new GridLayout(5, 1, 10, 20));
	buttonPanel.setOpaque(false);

	// Create game buttons with consistent style
	JButton newGameButton = createMenuButton("New Game");
	JButton mapSelectButton = createMenuButton("Select Map");
	JButton highScoresButton = createMenuButton("High Scores");
	JButton exitButton = createMenuButton("Exit");

	// Add action listeners
	newGameButton.addActionListener(e -> startNewGame());
	mapSelectButton.addActionListener(e -> selectMap());
	highScoresButton.addActionListener(e -> showHighScores());
	exitButton.addActionListener(e -> {
	    LogHandler.info(MainMenu.class, "User requested application exit");
	    System.exit(0);
	});

	// Add buttons to panel
	buttonPanel.add(newGameButton);
	buttonPanel.add(mapSelectButton);
	buttonPanel.add(highScoresButton);
	buttonPanel.add(exitButton);

	// Position the button panel
	buttonPanel.setBounds(WINDOW_WIDTH / 2 - BUTTON_PANEL_OFFSET, WINDOW_HEIGHT / 2 - BUTTON_PANEL_OFFSET, BUTTON_PANEL_WIDTH,
			      BUTTON_PANEL_HEIGHT);
	frame.getContentPane().add(buttonPanel);

	// Add a title label
	JLabel titleLabel = new JLabel("Tower Defense");
	titleLabel.setFont(new Font("Arial", Font.BOLD, TITLE_FONT_SIZE));
	titleLabel.setForeground(Color.WHITE);
	titleLabel.setHorizontalAlignment(JLabel.CENTER);
	titleLabel.setBounds(0, TITLE_LABEL_Y, WINDOW_WIDTH, TITLE_LABEL_HEIGHT);
	frame.getContentPane().add(titleLabel);
    }

    /**
     * Creates a styled button with the given text using the menu's visual theme.
     * <p>
     * Applies consistent font, color, background, and border styling to maintain visual consistency across menu buttons.
     *
     * @param text The text to display on the button
     *
     * @return A styled JButton component
     */
    private JButton createMenuButton(String text) {
	if (LogHandler.isLoggable(MainMenu.class, Level.FINE)) {
	    LogHandler.fine(MainMenu.class, "Creating menu button: " + text);
	}
	JButton button = new JButton(text);
	button.setFont(new Font("Arial", Font.BOLD, MENU_FONT_SIZE));
	button.setForeground(Color.WHITE);
	button.setBackground(BUTTON_BACKGROUND);
	button.setFocusPainted(false);
	button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, BUTTON_BORDER_THICKNESS),
							    BorderFactory.createEmptyBorder(BUTTON_PADDING, BUTTON_PADDING, BUTTON_PADDING,
											    BUTTON_PADDING)));
	return button;
    }

    /**
     * Starts a new game session with the currently selected map.
     * <p>
     * Closes the menu window and initializes the game components including the board and viewer.
     */
    private void startNewGame() {
	LogHandler.info(MainMenu.class, "Starting new game with map: " + selectedMapName);
	frame.dispose();

	// Start game with selected map name
	SwingUtilities.invokeLater(() -> {
	    Board board = new Board(20, 15, selectedMapName);
	    TowerDefenceViewer viewer = new TowerDefenceViewer(board);
	    viewer.show();
	});
    }

    /**
     * Displays a dialog for selecting the game map.
     * <p>
     * Allows the user to choose from available map options and stores the selection for use when starting a new game.
     */
    private void selectMap() {
	LogHandler.info(MainMenu.class, "Opening map selection dialog");
	String[] mapOptions = { "Default Map", "Forest Map", "Desert Map" };
	String selectedOption =
		(String) JOptionPane.showInputDialog(frame, "Choose a map:", "Map Selection", JOptionPane.PLAIN_MESSAGE, null, mapOptions,
						     mapOptions[0]);

	if (selectedOption != null) {
	    selectedMapName = selectedOption; // Store the selected map name
	    LogHandler.info(MainMenu.class, "Map selected: " + selectedMapName);
	    JOptionPane.showMessageDialog(frame, selectedMapName + " selected", "Map Selected", JOptionPane.INFORMATION_MESSAGE);
	} else {
	    LogHandler.info(MainMenu.class, "User canceled map selection");
	}
    }

    /**
     * Displays the high scores to the user.
     * <p>
     * Attempts to load and display high scores in a dialog window. If running in a headless environment, falls back to displaying scores in
     * the console log.
     */
    private void showHighScores() {
	LogHandler.info(MainMenu.class, "Displaying highscores");
	HighscoreList highscoreList = new HighscoreList();

	try {
	    highscoreList.loadFromJson();
	    JOptionPane.showMessageDialog(frame, createHighscoreDisplay(highscoreList), "Highscores", JOptionPane.INFORMATION_MESSAGE);
	} catch (HeadlessException e) {
	    LogHandler.severe(MainMenu.class, "Cannot display highscores in headless environment", e);
	    displayHighscoresInConsole();
	    return;
	} catch (JsonSyntaxException e) {
	    LogHandler.severe(MainMenu.class, "Invalid highscore file format: " + e.getMessage(), e);
	    JOptionPane.showMessageDialog(frame, "Highscore file is corrupted or invalid.", "Format Error", JOptionPane.ERROR_MESSAGE);
	    return;
	} catch (IOException e) {
	    LogHandler.severe(MainMenu.class, "Failed to load highscores file: " + e.getMessage(), e);
	    JOptionPane.showMessageDialog(frame, "Could not load highscores file.", "Error", JOptionPane.ERROR_MESSAGE);
	    return;
	}
    }

    /**
     * Creates a formatted string display of the highscores
     *
     * @param highscoreList The list of highscores to display
     *
     * @return Formatted string representation of highscores
     */
    private String createHighscoreDisplay(HighscoreList highscoreList) {
	if (highscoreList == null || highscoreList.toString().trim().isEmpty()) {
	    return "No highscores available yet. Be the first to set a record!";
	}
	return "Highscores:\n\n" + highscoreList;
    }

    /**
     * Displays highscores in the console as a fallback method
     */
    private void displayHighscoresInConsole() {
	LogHandler.info(MainMenu.class, "Displaying highscores in console");
	HighscoreList highscoreList = new HighscoreList();

	try {
	    highscoreList.loadFromJson();

	    String highscoresText = highscoreList.toString().trim().isEmpty() ? "No highscores available yet." : highscoreList.toString();

	    LogHandler.info(MainMenu.class, "\n=== HIGHSCORES ===\n" + highscoresText + "\n=================\n");
	} catch (JsonSyntaxException e) {
	    LogHandler.severe(MainMenu.class, "Invalid highscore file format for console display: " + e.getMessage(), e);
	    LogHandler.info(MainMenu.class, "\n=== HIGHSCORES ===\nNo highscores available (format error)\n=================\n");
	    return;
	} catch (IOException e) {
	    LogHandler.severe(MainMenu.class, "Failed to load highscores file for console display: " + e.getMessage(), e);
	    LogHandler.info(MainMenu.class, "\n=== HIGHSCORES ===\nNo highscores available (file error)\n=================\n");
	    return;
	}
    }
}