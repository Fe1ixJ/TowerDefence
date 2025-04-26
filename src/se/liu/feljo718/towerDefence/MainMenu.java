package se.liu.feljo718.towerDefence;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Main menu interface for the Tower Defense game.
 * <p>
 * This class manages the primary user interface before entering the game,
 * providing options for starting a new game, selecting maps, configuring options,
 * viewing high scores, and exiting the application.
 * <p>
 * The menu features a graphical background, styled buttons, and a responsive layout.
 *
 * @author feljo718
 * @see Board
 * @see TowerDefenceViewer
 */
public class MainMenu extends JFrame {
    private JPanel buttonPanel;
    private BufferedImage backgroundImage;
    private final int WINDOW_WIDTH = 800;
    private final int WINDOW_HEIGHT = 600;
    private String selectedMap = "maps.json"; // Default map

    /**
     * Creates and initializes the main menu interface.
     * <p>
     * Sets up the window dimensions, loads graphical resources,
     * initializes the background, and arranges UI components.
     */
    public MainMenu() {
        setTitle("Tower Defence");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);

        // Load background image
        loadBackgroundImage();

        // Create a custom JPanel with background painting
        setContentPane(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(30, 30, 60));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        });

        // Use a layout that respects size and position
        getContentPane().setLayout(null);

        // Create button panel centered on screen
        createButtonPanel();
    }

    /**
     * Attempts to load the background image for the menu.
     * <p>
     * Searches for the background image in the following locations:
     * <ol>
     *   <li>The local resources directory</li>
     *   <li>The application's classpath resources</li>
     * </ol>
     */
    private void loadBackgroundImage() {
        try {
            // Try to load from resources directory first
            File imageFile = new File("resources" + File.separator + "background.png");
            if (imageFile.exists()) {
                backgroundImage = ImageIO.read(imageFile);
            } else {
                // Try to load from classpath
                InputStream is = getClass().getClassLoader().getResourceAsStream("background.png");
                if (is != null) {
                    backgroundImage = ImageIO.read(is);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load background image: " + e.getMessage());
        }
    }

    /**
     * Creates and configures the panel containing menu buttons.
     * <p>
     * Sets up the main navigation buttons with consistent styling and
     * positions them in the center of the window. Also adds a title label.
     */
    private void createButtonPanel() {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 20));
        buttonPanel.setOpaque(false);

        // Create game buttons with consistent style
        JButton newGameButton = createMenuButton("New Game");
        JButton mapSelectButton = createMenuButton("Select Map");
        JButton optionsButton = createMenuButton("Options");
        JButton highScoresButton = createMenuButton("High Scores");
        JButton exitButton = createMenuButton("Exit");

        // Add action listeners
        newGameButton.addActionListener(e -> startNewGame());
        mapSelectButton.addActionListener(e -> selectMap());
        optionsButton.addActionListener(e -> showOptions());
        highScoresButton.addActionListener(e -> showHighScores());
        exitButton.addActionListener(e -> System.exit(0));

        // Add buttons to panel
        buttonPanel.add(newGameButton);
        buttonPanel.add(mapSelectButton);
        buttonPanel.add(optionsButton);
        buttonPanel.add(highScoresButton);
        buttonPanel.add(exitButton);

        // Position the button panel
        buttonPanel.setBounds(WINDOW_WIDTH/2 - 150, WINDOW_HEIGHT/2 - 150, 300, 300);
        getContentPane().add(buttonPanel);

        // Add a title label
        JLabel titleLabel = new JLabel("Tower Defence");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBounds(0, 50, WINDOW_WIDTH, 50);
        getContentPane().add(titleLabel);
    }

    /**
     * Creates a styled button with the given text using the menu's visual theme.
     * <p>
     * Applies consistent font, color, background, and border styling to maintain
     * visual consistency across menu buttons.
     *
     * @param text The text to display on the button
     * @return A styled JButton component
     */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 70, 120));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return button;
    }

    /**
     * Starts a new game session with the currently selected map.
     * <p>
     * Closes the menu window and initializes the game components
     * including the board and viewer.
     */
    private void startNewGame() {
        dispose(); // Close menu

        // Start game with selected map
        SwingUtilities.invokeLater(() -> {
            Board board = new Board(20, 15);
            TowerDefenceViewer viewer = new TowerDefenceViewer(board);
            viewer.show();
        });
    }

    /**
     * Displays a dialog for selecting the game map.
     * <p>
     * Allows the user to choose from available map options and
     * stores the selection for use when starting a new game.
     */
    private void selectMap() {
        String[] mapOptions = {"Default Map", "Forest Map", "Desert Map"};
        String selectedOption = (String) JOptionPane.showInputDialog(
                this,
                "Choose a map:",
                "Map Selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                mapOptions,
                mapOptions[0]
        );

        if (selectedOption != null) {
            switch (selectedOption) {
                case "Default Map" -> selectedMap = "maps.json";
                case "Forest Map" -> selectedMap = "maps.json";
                case "Desert Map" -> selectedMap = "maps.json";
            }
            JOptionPane.showMessageDialog(this,
                    selectedOption + " selected",
                    "Map Selected",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showOptions() {
        JOptionPane.showMessageDialog(this,
                "Options will be implemented in a future update.",
                "Options",
                JOptionPane.INFORMATION_MESSAGE);
    }


    private void showHighScores() {
        try {
            // Create a new highscore list, which will load existing scores
            HighscoreList highscoreList = new HighscoreList();

            // Display highscores in a dialog, similar to TowerDefenceViewer
            JOptionPane.showMessageDialog(this,
                    "Highscores:\n\n" + highscoreList.toString(),
                    "Highscores",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load highscores: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        });
    }
}