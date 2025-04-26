package se.liu.feljo718.towerDefence;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * Main viewer component that handles the UI display for the Tower Defense game.
 * <p>
 * This class implements the BoardListener interface to receive notifications when
 * the game state changes. It manages the game window, menu system, and the animation
 * timer that drives the game loop. The viewer also handles various UI-related events
 * like pausing, restarting, and exiting the game.
 *
 * @author feljo718
 * @see Board
 * @see BoardListener
 * @see MainMenu
 */
public class TowerDefenceViewer implements BoardListener {
    private final Board board;
    private JFrame frame;
    private Timer clockTimer = null;
    private JMenuItem pauseButton;
    private JMenuItem highscoreButton;
    private HighscoreList highscoreList;

    /**
     * Creates a new game viewer for the specified board.
     * <p>
     * Initializes the game window, registers as a listener to the board,
     * and starts the game loop.
     *
     * @param board The game board to display and control
     */
    public TowerDefenceViewer(Board board) {
        this.board = board;
        board.addBoardListener(this);
        this.highscoreList = new HighscoreList();
        createFrame();
        startGame();
    }

    @Override
    public void boardChanged() {
        // Update view when board changes
        frame.repaint();
    }

    @Override
    public void gameOver() {
        stopTick();
        SwingUtilities.invokeLater(() -> {
            int score = board.getRound() -1; // Use last compleated round as score

            String playerName = JOptionPane.showInputDialog(frame,
                    "Game Over! You reached round " + score + ".\nEnter your name for the highscore:",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);

            if (playerName != null && !playerName.trim().isEmpty()) {
                try {
                    highscoreList.addScore(new Highscore(playerName, score));
                    showHighscores();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(frame,
                            "Failed to save highscore: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            } else {
                System.exit(0);
            }
        });
    }

    /**
     * Starts the game timer that drives the animation loop.
     * <p>
     * Creates and starts a Swing Timer that calls the board's tick method
     * approximately 60 times per second (17ms intervals).
     */
    public void tick() {
        if (clockTimer == null) {
            Action doOneStep = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    board.tick();
                }
            };
            clockTimer = new Timer(17, doOneStep);
            clockTimer.setCoalesce(true);
            clockTimer.start();
        }
    }
    /**
     * Creates and configures the main game window and user interface components.
     * <p>
     * Sets up the game display, menu bar, and action listeners for user interaction.
     */
    private void createFrame() {
        frame = new JFrame("Tower Defence");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Add board component
        BoardComponent boardComponent = new BoardComponent(board);
        frame.add(boardComponent, BorderLayout.CENTER);

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newGame = createMenuItem("New Game", MenuOptions.NEW_GAME);

// Add the Return to Menu item here, after newGame but before exit
        JMenuItem returnToMenu = new JMenuItem("Return to Menu");
        returnToMenu.addActionListener(e -> {
            stopTick();
            frame.dispose();
            SwingUtilities.invokeLater(() -> {
                MainMenu menu = new MainMenu();
                menu.setVisible(true);
            });
        });
        fileMenu.add(returnToMenu);

// Add a separator between menu groups
        fileMenu.addSeparator();
        JMenuItem exit = createMenuItem("Exit", MenuOptions.QUIT);
        fileMenu.add(newGame);
        fileMenu.add(returnToMenu);  // Add the menu item to the fileMenu
        fileMenu.addSeparator();
        fileMenu.add(exit);

        // Game menu
        JMenu gameMenu = new JMenu("Game");
        pauseButton = createMenuItem("Pause", MenuOptions.PAUSE);
        JMenuItem restart = createMenuItem("Restart", MenuOptions.NEW_GAME);
        highscoreButton = createMenuItem("Highscores", MenuOptions.HIGHSCORES);

        gameMenu.add(pauseButton);
        gameMenu.add(restart);
        gameMenu.add(highscoreButton);

        menuBar.add(fileMenu);
        menuBar.add(gameMenu);
        frame.setJMenuBar(menuBar);

        frame.pack();
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
     * Action handler for menu options in the game interface.
     * <p>
     * Processes user selections from the menu bar, such as quitting,
     * starting a new game, or toggling the pause state.
     */
    private class MenuAction extends AbstractAction {
        private final MenuOptions option;

        private MenuAction(final MenuOptions option) {
            this.option = option;
        }

        @Override public void actionPerformed(ActionEvent e) {
            switch (option) {
                case QUIT:
                    System.exit(0);
                    break;
                case NEW_GAME:
                    // Get the source component to determine which action to take
                    Object source = e.getSource();
                    if (source instanceof JMenuItem) {
                        String text = ((JMenuItem) source).getText();
                        if ("Restart".equals(text)) {
                            // Restart the current game
                            restartGame();
                        } else {
                            // Start a new game
                            startNewGame();
                        }
                    }
                    break;
                case PAUSE:
                    if (clockTimer != null) {
                        if (clockTimer.isRunning()) {
                            clockTimer.stop();
                            pauseButton.setText("Resume");
                        } else {
                            clockTimer.start();
                            pauseButton.setText("Pause");
                        }
                    }
                    break;
                case HIGHSCORES:
                    showHighscores();
                    break;
            }
        }
        private void startNewGame() {
            stopTick();
            frame.dispose();
            SwingUtilities.invokeLater(() -> {
                Board newBoard = new Board(20, 15);
                TowerDefenceViewer viewer = new TowerDefenceViewer(newBoard);
                viewer.show();
            });
        }

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

    }

    private void showHighscores() {
        JOptionPane.showMessageDialog(frame,
                "Highscores:\n\n" + highscoreList.toString(),
                "Highscores",
                JOptionPane.INFORMATION_MESSAGE);

    }

    private JMenuItem createMenuItem(String text, MenuOptions option) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(new MenuAction(option));
        return menuItem;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        });


    }

    public void startGame() {
        tick();
    }


    public void stopTick() {
        if (clockTimer != null) {
            clockTimer.stop();
        }
    }

    private static void showStartImage() {
        // Will be implemented later
    }
}