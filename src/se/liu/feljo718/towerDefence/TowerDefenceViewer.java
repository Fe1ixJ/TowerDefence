package se.liu.feljo718.towerDefence;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TowerDefenceViewer implements BoardListener {
    private final Board board;
    private JFrame frame;
    private Timer clockTimer = null;
    private JMenuItem pauseButton;

    public TowerDefenceViewer(Board board) {
        this.board = board;
        board.addBoardListener(this);
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
            JOptionPane.showMessageDialog(null,
                    "Game Over! You ran out of lives!",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        });
    }

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
        gameMenu.add(pauseButton);
        gameMenu.add(restart);

        menuBar.add(fileMenu);
        menuBar.add(gameMenu);
        frame.setJMenuBar(menuBar);

        frame.pack();
    }

    public void show() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

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