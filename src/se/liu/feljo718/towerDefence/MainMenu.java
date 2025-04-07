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

public class MainMenu extends JFrame {
    private JPanel buttonPanel;
    private BufferedImage backgroundImage;
    private final int WINDOW_WIDTH = 800;
    private final int WINDOW_HEIGHT = 600;
    private String selectedMap = "maps.json"; // Default map

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

    private void startNewGame() {
        dispose(); // Close menu

        // Start game with selected map
        SwingUtilities.invokeLater(() -> {
            Board board = new Board(20, 15);
            TowerDefenceViewer viewer = new TowerDefenceViewer(board);
            viewer.show();
        });
    }

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
        JOptionPane.showMessageDialog(this,
                "High scores will be implemented in a future update.",
                "High Scores",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        });
    }
}