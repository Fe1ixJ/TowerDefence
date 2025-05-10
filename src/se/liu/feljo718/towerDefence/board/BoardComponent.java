package se.liu.feljo718.towerdefence.board;

import se.liu.feljo718.towerdefence.enemy.Enemy;
import se.liu.feljo718.towerdefence.handler.LogHandler;
import se.liu.feljo718.towerdefence.tower.Tower;
import se.liu.feljo718.towerdefence.tower.TowerType;
import se.liu.feljo718.towerdefence.viewer.Explosion;
import se.liu.feljo718.towerdefence.viewer.TowerMenu;
import se.liu.feljo718.towerdefence.viewer.TowerShop;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

/**
 * A visual representation of the Tower Defense game board.
 * <p>
 * This component handles rendering of the game map, towers, enemies, and UI controls. It also manages user interactions such as tower
 * placement and selection.
 * <p>
 * The component implements BoardListener to receive updates from the game board and refresh the display when the game state changes.
 *
 * @author feljo718
 * @see Board
 * @see BoardListener
 */
public class BoardComponent extends JComponent implements BoardListener
{
    /** The size of each game board tile in pixels. */
    public static final int TILE_SIZE = 40;
    /** The size of enemy sprites in pixels. */
    public static final int ENEMY_SIZE = 30;

    private static final Color GRASS_COLOR = Color.decode("#228B22");
    private static final Color PATH_COLOR = Color.decode("#996600");
    private static final Color START_END_COLOR = Color.decode("#FF0000");
    private static final Color WATER_COLOR = Color.BLUE;
    private static final Color INTERFACE_COLOR = Color.LIGHT_GRAY;
    private static final Color SAND_COLOR = Color.decode("#D2B48C");
    private static final Color RANGE_COLOR = new Color(173, 216, 230, 128);
    private static final Color POWERUP_GLOW = new Color(255, 255, 0, 100);

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
    private Image explosionImage = null;


    public BoardComponent(Board board) {
	this.board = board;
	this.board.addBoardListener(this);

	// Initialize UI components
	livesLabel = new JLabel("Lives: " + board.getLives());
	coinsLabel = new JLabel("Coins: " + board.getCoins());
	buyMenuButton = new JButton("Buy Tower");
	nextRoundButton = new JButton("Next Round");
	roundLabel = new JLabel("Round: " + board.getRound());
	viewCircleButton = new JButton("View Range");

	setUpUI();
	setUpListeners();
	loadExplosionImage();
    }

    /**
     * Sets up the UI layout using proper layout managers
     */
    private void setUpUI() {
	// Create a panel for the UI controls at the top
	JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
	controlPanel.add(livesLabel);
	controlPanel.add(buyMenuButton);
	controlPanel.add(nextRoundButton);
	controlPanel.add(roundLabel);
	controlPanel.add(viewCircleButton);
	controlPanel.add(coinsLabel);

	// Use null layout for the main component since we need precise positioning for the game grid
	setLayout(null);

	// Position control panel at the top
	controlPanel.setBounds(0, 0, board.getWidth() * TILE_SIZE, TILE_SIZE);
	add(controlPanel);
    }

    /**
     * Sets up event listeners for UI components and mouse interactions
     */
    private void setUpListeners() {
	buyMenuButton.addActionListener(e -> showTowerShop());
	nextRoundButton.addActionListener(e -> board.startNextRound());
	viewCircleButton.addActionListener(e -> {
	    showRangeCircles = !showRangeCircles;
	    viewCircleButton.setText(showRangeCircles ? "Hide Ranges" : "View Ranges");
	    repaint();
	});

	addMouseListener(new MouseAdapter()
	{
	    @Override public void mouseClicked(MouseEvent e) {
		if (placingTower) {
		    placeTower(e.getX(), e.getY());
		} else {
		    // Check if a tower was clicked
		    Tower clickedTower = getTowerAt(e.getX(), e.getY());
		    if (clickedTower != null) {
			TowerMenu menu = new TowerMenu((JFrame) SwingUtilities.getWindowAncestor(BoardComponent.this), clickedTower, board);
			menu.show();
		    }
		}
	    }
	});
    }

    /**
     * Loads the explosion image from either the classpath or filesystem
     */
    private void loadExplosionImage() {
	try {
	    // Try classpath first
	    URL imageUrl = getClass().getClassLoader().getResource("images/explosion.png");
	    if (imageUrl != null) {
		explosionImage = ImageIO.read(imageUrl);
		if (explosionImage != null) {
		    LogHandler.info(BoardComponent.class, "Loaded explosion image from classpath: " + imageUrl);
		    return;
		} else {
		    LogHandler.logWarning(BoardComponent.class, "Failed to read explosion image from: " + imageUrl);
		}
	    }
	    LogHandler.logWarning(BoardComponent.class, "Explosion image not found in classpath or resources directory");
	    explosionImage = null; // Set default value when image not found
	} catch (IOException e) {
	    LogHandler.severe(BoardComponent.class, "Error loading explosion image: " + e.getMessage(), e);
	    explosionImage = null;
	    return;
	    // No need to crash the game if the image is not found
	}
    }

    /**
     * Creates a tower placement UI when the shop is accessed.
     * <p>
     * Opens a dialog allowing the user to select a tower type to purchase. If a tower is selected, enters tower placement mode where the
     * cursor changes to a crosshair until the tower is placed or the action canceled.
     */
    private void showTowerShop() {
	TowerShop shop = new TowerShop((JFrame) SwingUtilities.getWindowAncestor(this), board);
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
     * Converts pixel coordinates to board grid coordinates and attempts to place the currently selected tower. The tower is only placed if
     * the target tile is a valid placement location (grass) and the player has sufficient coins.
     *
     * @param x The x-coordinate in pixels where the tower should be placed
     * @param y The y-coordinate in pixels where the tower should be placed
     */
    private void placeTower(int x, int y) {
	int col = x / TILE_SIZE;
	int row = y / TILE_SIZE;

	// Validate grid bounds
	if (col < 0 || col >= board.getWidth() || row < 0 || row >= board.getHeight()) {
	    LogHandler.logWarning(BoardComponent.class, "Attempted to place tower outside board bounds");
	    return;
	}

	if (board.getTileAt(row, col) == TileType.GRASS || board.getTileAt(row, col) == TileType.SAND) {
	    // Only deduct money if tower placement was successful
	    Tower tower = board.getTowerFactory().createTower(selectedTower, row, col);
	    if (tower != null) {
		board.gainCoins(-getTowerCost(selectedTower));
	    }
	}


	placingTower = false;
	selectedTower = null;
	setCursor(Cursor.getDefaultCursor());
    }

    private int getTowerCost(TowerType type) {
	return switch (type) {
	    case BASIC -> 100;
	    case SNIPER, SPLASH -> 150;
	    case BOMBER -> 250;
	    case SLOW -> 175;
	};
    }


    /**
     * Locates a tower at the specified pixel coordinates.
     * <p>
     * Converts pixel coordinates to board grid coordinates and checks if any tower is positioned at that location.
     *
     * @param x The x-coordinate in pixels
     * @param y The y-coordinate in pixels
     *
     * @return The tower at the specified position, or null if no tower exists there
     */
    private Tower getTowerAt(int x, int y) {
	int col = x / TILE_SIZE;
	int row = y / TILE_SIZE;

	// Add bounds checking
	if (col < 0 || col >= board.getWidth() || row < 0 || row >= board.getHeight()) {
	    return null;
	}

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
    @Override protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	Graphics2D g2d = (Graphics2D) g;

	// Draw board tiles
	drawTiles(g2d);

	// Draw enemies
	drawEnemies(g2d);

	// Draw towers
	drawTowers(g2d);

	// Draw grid lines
	drawGrid(g2d);

	// Draw tower range circles if enabled
	if (showRangeCircles) {
	    drawRangeCircles(g2d);
	}

	// Draw explosions
	drawExplosions(g2d);
    }

    private void drawTiles(Graphics2D g2d) {
	for (int row = 0; row < board.getHeight(); row++) {
	    for (int col = 0; col < board.getWidth(); col++) {
		TileType tile = board.getTileAt(row, col);
		switch (tile) {
		    case GRASS -> g2d.setColor(GRASS_COLOR);
		    case PATH -> g2d.setColor(PATH_COLOR);
		    case START, END -> g2d.setColor(START_END_COLOR);
		    case WATER -> g2d.setColor(WATER_COLOR);
		    case INTERFACE -> g2d.setColor(INTERFACE_COLOR);
		    case SAND -> g2d.setColor(SAND_COLOR);
		}
		g2d.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
	    }
	}
    }

    private void drawEnemies(Graphics2D g2d) {
	for (Enemy enemy : board.getEnemyFactory().getEnemies()) {
	    Point pixelPos = enemy.getPixelPosition();
	    g2d.setColor(enemy.getColor());
	    int x = pixelPos.x + (TILE_SIZE - ENEMY_SIZE) / 2;
	    int y = pixelPos.y + (TILE_SIZE - ENEMY_SIZE) / 2;
	    g2d.fillOval(x, y, ENEMY_SIZE, ENEMY_SIZE);
	}
    }

    private void drawTowers(Graphics2D g2d) {
	for (Tower tower : board.getTowerFactory().getTowers()) {
	    int tileSize = TILE_SIZE;
	    int x = tower.getPosition().x * tileSize;
	    int y = tower.getPosition().y * tileSize;

	    // Draw the base tower
	    g2d.setColor(tower.getColor());
	    g2d.fillRect(x + tileSize / 4, y + tileSize / 4, tileSize / 2, tileSize / 2);

	    // Draw a glowing effect for towers with active powerups
	    if (!tower.getActivePowerups().isEmpty()) {
		g2d.setColor(POWERUP_GLOW);
		g2d.fillOval(x, y, tileSize, tileSize);
	    }
	}
    }

    private void drawGrid(Graphics2D g2d) {
	g2d.setColor(Color.BLACK);

	for (int row = 1; row <= board.getHeight(); row++) {
	    g2d.drawLine(0, row * TILE_SIZE, board.getWidth() * TILE_SIZE, row * TILE_SIZE);
	}
	for (int col = 0; col <= board.getWidth(); col++) {
	    g2d.drawLine(col * TILE_SIZE, TILE_SIZE, col * TILE_SIZE, board.getHeight() * TILE_SIZE);
	}
    }

    private void drawRangeCircles(Graphics2D g2d) {
	g2d.setColor(RANGE_COLOR);

	for (Tower tower : board.getTowerFactory().getTowers()) {
	    Point pos = tower.getPosition();
	    double range = tower.getRange();

	    // Calculate circle center position
	    int centerX = pos.x * TILE_SIZE + TILE_SIZE / 2;
	    int centerY = pos.y * TILE_SIZE + TILE_SIZE / 2;

	    // Draw circle with diameter = range * 2 * TILE_SIZE
	    double diameter = range * 2 * TILE_SIZE;
	    g2d.drawOval((int) (centerX - diameter / 2), (int) (centerY - diameter / 2), (int) diameter, (int) diameter);
	}
    }

    private void drawExplosions(Graphics2D g2d) {
	if (explosionImage != null) {
	    for (Explosion explosion : board.getExplosions()) {
		Point pos = explosion.getPosition();
		// Center the explosion on the enemy
		int x = pos.x - explosionImage.getWidth(null) / 2;
		int y = pos.y - explosionImage.getHeight(null) / 2;
		g2d.drawImage(explosionImage, x, y, null);
	    }
	}
    }

    @Override public Dimension getPreferredSize() {
	return new Dimension(board.getWidth() * TILE_SIZE, board.getHeight() * TILE_SIZE);
    }

    /**
     * Handles updates when the board state changes.
     * <p>
     * Updates UI labels to display current lives, coins and round information, then triggers a repaint of the component to reflect visual
     * changes.
     */
    @Override public void boardChanged() {
	livesLabel.setText("Lives: " + board.getLives());
	coinsLabel.setText("Coins: " + board.getCoins());
	roundLabel.setText("Round: " + board.getRound());
	repaint();
    }

    @Override public void gameOver() {
    }

    @Override public void gameCompleted() {

    }
}