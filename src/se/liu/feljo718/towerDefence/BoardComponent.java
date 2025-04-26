package se.liu.feljo718.towerDefence;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A visual representation of the Tower Defense game board.
 * <p>
 * This component handles rendering of the game map, towers, enemies, and UI controls.
 * It also manages user interactions such as tower placement and selection.
 * <p>
 * The component implements BoardListener to receive updates from the game board
 * and refresh the display when the game state changes.
 *
 * @author feljo718
 * @see Board
 * @see BoardListener
 */
public class BoardComponent extends JComponent implements BoardListener {
    /** The size of each game board tile in pixels. */
    public static final int TILE_SIZE = 40;
    /** The size of enemy sprites in pixels. */
    public static final int ENEMY_SIZE = 30;
    /** The size of tower sprites in pixels. */
    public static final int TOWER_SIZE = 30;
    private final Board board;
    private final JLabel livesLabel;
    private final JLabel coinsLabel;
    private final JLabel roundLabel;
    private final JButton buyMenuButton;
    private final JButton nextRoundButton;
    private final JButton viewCircleButton;
    private boolean placingTower = false;
    private TowerType selectedTower = null;
    private boolean showRangeCircles = false;

    public BoardComponent(Board board) {
        this.board = board;
        this.board.addBoardListener(this);
        setLayout(null);

        livesLabel = new JLabel("Lives: " + board.getLives());
        coinsLabel = new JLabel("Coins: " + board.getCoins());
        buyMenuButton = new JButton("Buy Tower");
        nextRoundButton = new JButton("Next Round");
        roundLabel = new JLabel("Round: " + board.getRound());
        viewCircleButton = new JButton("View Circle");


        // MAGIC NUMBER GEOMETRY FIX
        livesLabel.setBounds(10, 10, 100, 20);
        coinsLabel.setBounds(getPreferredSize().width - 110, 10, 100, 20);
        buyMenuButton.setBounds(200 - 50, 10, 100, 20);
        nextRoundButton.setBounds(200 + 75, 10, 120, 20);
        roundLabel.setBounds(200 + 200, 10, 75, 20);
        viewCircleButton.setBounds(200 + 300, 10, 100, 20);

        add(livesLabel);
        add(coinsLabel);
        add(buyMenuButton);
        add(nextRoundButton);
        add(roundLabel);
        add(viewCircleButton);

        buyMenuButton.addActionListener(e -> showTowerShop());
        nextRoundButton.addActionListener(e -> board.startNextRound());
        viewCircleButton.addActionListener(e -> {
            showRangeCircles = !showRangeCircles;
            viewCircleButton.setText(showRangeCircles ? "Hide Ranges" : "View Ranges");
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (placingTower) {
                    placeTower(e.getX(), e.getY());
                } else {
                    // Check if a tower was clicked
                    Tower clickedTower = getTowerAt(e.getX(), e.getY());
                    if (clickedTower != null) {
                        TowerMenu menu = new TowerMenu(
                                (JFrame) SwingUtilities.getWindowAncestor(BoardComponent.this),
                                clickedTower,
                                board);
                        menu.show();
                    }
                }
            }
        });
    }

    /**
     * Creates a tower placement UI when the shop is accessed.
     * <p>
     * Opens a dialog allowing the user to select a tower type to purchase.
     * If a tower is selected, enters tower placement mode where the cursor
     * changes to a crosshair until the tower is placed or the action canceled.
     */
    private void showTowerShop() {
        TowerShop shop = new TowerShop(
                (JFrame) SwingUtilities.getWindowAncestor(this), board);
        shop.show();

        TowerType selected = shop.getSelectedTower();
        if (selected != null) {
            selectedTower = selected;
            placingTower = true;
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
    }

    /**
     * Places a tower at the specified pixel coordinates on the board.
     * <p>
     * Converts pixel coordinates to board grid coordinates and attempts to place
     * the currently selected tower. The tower is only placed if the target tile
     * is a valid placement location (grass) and the player has sufficient coins.
     *
     * @param x The x-coordinate in pixels where the tower should be placed
     * @param y The y-coordinate in pixels where the tower should be placed
     */
    private void placeTower(int x, int y) {
        int col = x / TILE_SIZE;
        int row = y / TILE_SIZE;
        Point pos = new Point(col, row);

        if (board.getTileAt(row, col) == TileType.GRASS) {
            board.getTowerFactory().placeTower(selectedTower, pos);
            board.gainCoins(-getTowerCost(selectedTower));
        }

        placingTower = false;
        selectedTower = null;
        setCursor(Cursor.getDefaultCursor());
    }

    private int getTowerCost(TowerType type) {
        return switch (type) {
            case BASIC -> 100;
            case SNIPER -> 150;
            case SPLASH -> 200;
        };
    }

    /**
     * Locates a tower at the specified pixel coordinates.
     * <p>
     * Converts pixel coordinates to board grid coordinates and checks if
     * any tower is positioned at that location.
     *
     * @param x The x-coordinate in pixels
     * @param y The y-coordinate in pixels
     * @return The tower at the specified position, or null if no tower exists there
     */
    private Tower getTowerAt(int x, int y) {
        int col = x / TILE_SIZE;
        int row = y / TILE_SIZE;

        // Check if any tower is at this position
        for (Tower tower : board.getTowerFactory().getTowers()) {
            Point pos = tower.getPosition();
            if (pos.x == col && pos.y == row) {
                return tower;
            }
        }
        return null;
    }

    /**
     * Renders the game board, towers, enemies and UI elements.
     * <p>
     * This method handles the complete visual rendering of the game state, including:
     * <ul>
     *   <li>The tile grid with different terrain types</li>
     *   <li>All active enemies with appropriate colors based on type</li>
     *   <li>All placed towers</li>
     *   <li>Tower range indicators (when enabled)</li>
     *   <li>Grid lines for visual clarity</li>
     * </ul>
     *
     * @param g The Graphics object used for drawing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        for (int row = 0; row < board.getHeight(); row++) {
            for (int col = 0; col < board.getWidth(); col++) {
                TileType tile = board.getTileAt(row, col);
                switch (tile) {
                    case GRASS -> g2d.setColor(Color.decode("#228B22"));
                    case PATH -> g2d.setColor(Color.decode("#996600"));
                    case START, END -> g2d.setColor(Color.decode("#FF0000"));
                    case WATER -> g2d.setColor(Color.BLUE);
                    case INTERFACE -> g2d.setColor(Color.LIGHT_GRAY);
                }
                g2d.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        // Draw enemies
        for (Enemy enemy : board.getEnemyFactory().getEnemies()) {
            Point pixelPos = enemy.getPixelPosition();
            switch (enemy.getType()) {
                case BASIC -> g2d.setColor(Color.BLACK);
                case FAST -> g2d.setColor(Color.RED);
                case TANK -> g2d.setColor(Color.GREEN);
            }
            int x = pixelPos.x + (TILE_SIZE - ENEMY_SIZE) / 2;
            int y = pixelPos.y + (TILE_SIZE - ENEMY_SIZE) / 2;
            g2d.fillOval(x, y, ENEMY_SIZE, ENEMY_SIZE);
        }

        // Draw towers
        for (Tower tower : board.getTowerFactory().getTowers()) {
            int tileSize = getSize().width / board.getWidth();
            int x = tower.getPosition().x * tileSize;
            int y = tower.getPosition().y * tileSize;

            // Draw the base tower
            g.setColor(tower.getColor());
            g.fillRect(x + tileSize/4, y + tileSize/4, tileSize/2, tileSize/2);

            // Draw a glowing effect for towers with active powerups
            if (!tower.getActivePowerups().isEmpty()) {
                g.setColor(new Color(255, 255, 0, 100)); // Semi-transparent yellow
                g.fillOval(x, y, tileSize, tileSize);
            }

        }


        // Draw grid lines
        g2d.setColor(Color.BLACK);

        for (int row = 1; row <= board.getHeight(); row++) {
            g2d.drawLine(0, row * TILE_SIZE, board.getWidth() * TILE_SIZE, row * TILE_SIZE);
        }
        for (int col = 0; col <= board.getWidth(); col++) {
            g2d.drawLine(col * TILE_SIZE, TILE_SIZE, col * TILE_SIZE, board.getHeight() * TILE_SIZE);
        }

        if (showRangeCircles) {
            g2d.setColor(new Color(173, 216, 230, 128)); // Light blue with transparency

            for (Tower tower : board.getTowerFactory().getTowers()) {
                Point pos = tower.getPosition();
                double range = tower.getRange();

                // Calculate circle center position
                int centerX = pos.x * TILE_SIZE + TILE_SIZE / 2;
                int centerY = pos.y * TILE_SIZE + TILE_SIZE / 2;

                // Draw circle with diameter = range * 2 * TILE_SIZE
                double diameter = range * 2 * TILE_SIZE;
                g2d.drawOval((int)(centerX - diameter / 2), (int)(centerY - diameter / 2), (int)diameter, (int)diameter);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(board.getWidth() * TILE_SIZE, board.getHeight() * TILE_SIZE);
    }

    /**
     * Handles updates when the board state changes.
     * <p>
     * Updates UI labels to display current lives, coins and round information,
     * then triggers a repaint of the component to reflect visual changes.
     */
    @Override
    public void boardChanged() {
        livesLabel.setText("Lives: " + board.getLives());
        coinsLabel.setText("Coins: " + board.getCoins());
        roundLabel.setText("Round: " + board.getRound());
        repaint();
    }


    @Override
    public void gameOver() {
    }
}

