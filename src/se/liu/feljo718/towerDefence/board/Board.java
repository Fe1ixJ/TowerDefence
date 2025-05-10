package se.liu.feljo718.towerdefence.board;

import com.google.gson.JsonSyntaxException;
import se.liu.feljo718.towerdefence.enemy.EnemyFactory;
import se.liu.feljo718.towerdefence.handler.LevelReader;
import se.liu.feljo718.towerdefence.handler.LogHandler;
import se.liu.feljo718.towerdefence.handler.MapReader;
import se.liu.feljo718.towerdefence.tower.TowerFactory;
import se.liu.feljo718.towerdefence.viewer.Explosion;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * The core game board that manages the state and logic of the Tower Defense game.
 * <p>
 * This class coordinates the game elements including the map grid, enemy waves, tower placement, player resources, and game progression.
 * The board serves as the central component connecting different game subsystems.
 * <p>
 * The board uses a coordinate system with [row, column] indexing where [0,0] is at the top-left. The first row (row 0) is reserved for
 * interface elements, with the playable map starting at row 1.
 *
 * @author feljo718
 */
public class Board
{
    /** JSON file containing map configuration data. */
    private static final String MAP_FILE = "maps.json";
    private static final int STARTING_LIVES = 15;
    private static final int STARTING_COINS = 350;
    private static final int LEVEL_COMPLETION_BONUS = 100;

    private final List<BoardListener> boardListeners;
    private final int width;
    private final int height;
    private final EnemyFactory enemyFactory;
    private final TowerFactory towerFactory;
    private final List<Explosion> explosions = new ArrayList<>();
    private final String mapName;
    private TileType[][] tiles = null;
    private Level currentLevel = null;
    private List<Level> levels;
    private int currentLevelIndex = -1;
    private int lives = STARTING_LIVES;
    private int coins = STARTING_COINS;


    public Board(final int width, final int height, final String mapName) {
	if (width < 1 || height < 1) {
	    throw new IllegalArgumentException("Board dimensions must be positive values");
	}

	this.width = width;
	this.height = height + 1; // Add 1 for interface row
	this.mapName = mapName != null ? mapName : "Default Map";
	this.boardListeners = new ArrayList<>();
	this.levels = new ArrayList<>();

	loadMap();
	this.enemyFactory = new EnemyFactory(this);
	this.towerFactory = new TowerFactory(this);
	loadLevels();
    }

    public Board(final int width, final int height) {
	this(width, height, "Default Map");
    }


    /**
     * Loads game levels from configuration. If loading fails, initializes with an empty level list as fallback.
     */
    private void loadLevels() {
	try {
	    LevelReader levelReader = new LevelReader();
	    List<Level> loadedLevels = levelReader.getLevels();

	    if (loadedLevels == null || loadedLevels.isEmpty()) {
		// No levels found - create empty list as fallback
		this.levels = new ArrayList<>();
		LogHandler.logWarning(Board.class, "No levels found, using empty level list as fallback.");
	    } else {
		this.levels = loadedLevels;
		LogHandler.info(Board.class, "Loaded " + levels.size() + " levels");
	    }

	    notifyListeners();
	} catch (JsonSyntaxException e) {
	    LogHandler.severe(Board.class, "Failed to parse level data: " + e.getMessage());
	    this.levels = new ArrayList<>();
	    LogHandler.info(Board.class, "Using empty level list as fallback due to JSON parse error.");
	    return;
	}
    }

    /**
     * Initiates the next game level or wave when appropriate.
     * <p>
     * If the current level is completed or no level is active, this method advances to the next level in the sequence. If all levels have
     * been completed, no further action is taken.
     */
    public void startNextRound() {
	if (currentLevel == null || currentLevel.isCompleted()) {
	    // If a level was just completed (and it wasn't the initial state)
	    if (currentLevel != null && currentLevel.isCompleted()) {
		// Award bonus coins for completing the level
		gainCoins(LEVEL_COMPLETION_BONUS);
		LogHandler.info(Board.class, "Level completed! Bonus: " + LEVEL_COMPLETION_BONUS + " coins");

		// Check if player just completed the final level (level 47)
		if (currentLevelIndex == levels.size() - 45) {
		    showGameCompletionDialog();
		    return;
		}
	    }

	    currentLevelIndex++;
	    if (currentLevelIndex < levels.size()) {
		startLevel(levels.get(currentLevelIndex));
	    }
	}
    }

    private void showGameCompletionDialog() {
	synchronized (boardListeners) {
	    for (BoardListener listener : boardListeners) {
		listener.gameCompleted();
	    }
	}
    }

    /**
     * Updates the game state for a single frame.
     * <p>
     * This method is called repeatedly as part of the game loop and performs several key operations:
     * <ul>
     *   <li>Spawns new enemies based on the current level timing</li>
     *   <li>Processes enemy movement across the board</li>
     *   <li>Activates towers to attack enemies in range</li>
     *   <li>Cleans up defeated enemies</li>
     *   <li>Notifies listeners of state changes</li>
     * </ul>
     */
    public void tick() {
	long currentTime = System.currentTimeMillis();

	// Handle enemy spawning
	if (currentLevel != null && !currentLevel.isCompleted()) {
	    if (currentLevel.shouldSpawnEnemy(currentTime)) {
		enemyFactory.spawnEnemy(currentLevel.getCurrentEnemyType());
	    }
	}

	// Update explosions
	updateExplosions();

	// Process game logic
	enemyFactory.moveEnemies();
	towerFactory.tick();
	enemyFactory.removeFinishedEnemies();

	notifyListeners();
    }

    /**
     * Updates all active explosion animations, removing those that have expired.
     */
    private void updateExplosions() {
	Iterator<Explosion> explosionIterator = explosions.iterator();
	while (explosionIterator.hasNext()) {
	    Explosion explosion = explosionIterator.next();
	    explosion.decreaseLifetime();
	    if (explosion.isExpired()) {
		explosionIterator.remove();
	    }
	}
    }

    /**
     * Loads the game map from the configured file.
     * <p>
     * Creates a tile grid with an additional interface row at the top. The method reads a map layout from the JSON file and adds an
     * interface row at position 0, shifting the actual game map down by one row.
     */
    private void loadMap() {
	try {
	    MapReader mapReader = new MapReader(MAP_FILE, width, height - 1, mapName);
	    TileType[][] gameMap = mapReader.getMap();

	    if (gameMap == null || gameMap.length != height - 1) {
		LogHandler.logWarning(Board.class, "Invalid game map dimensions");
		createDefaultMap();
		return;
	    }

	    // Validate row lengths
	    for (TileType[] row : gameMap) {
		if (row == null || row.length != width) {
		    LogHandler.logWarning(Board.class, "Invalid row width in game map");
		    createDefaultMap();
		    return;
		}
	    }

	    // Initialize tiles with interface row
	    tiles = new TileType[height][width];
	    Arrays.fill(tiles[0], TileType.INTERFACE);

	    // Copy game map into tiles array
	    for (int row = 0; row < height - 1; row++) {
		System.arraycopy(gameMap[row], 0, tiles[row + 1], 0, width);
	    }

	    LogHandler.info(Board.class, "Map '" + mapName + "' loaded successfully");

	} catch (JsonSyntaxException e) {
	    LogHandler.severe(Board.class, "Failed to parse map file: " + e.getMessage(), e);
	    createDefaultMap();
	    return;
	} finally {
	    if (tiles == null) {
		LogHandler.logWarning(Board.class, "Map loading failed completely, creating default map");
		createDefaultMap();
	    }
	}
    }

    /**
     * Creates a simple default map if map loading fails.
     */
    private void createDefaultMap() {
	tiles = new TileType[height][width];

	// Fill interface row
	for (int col = 0; col < width; col++) {
	    tiles[0][col] = TileType.INTERFACE;
	}

	// Fill game area with grass by default
	for (int row = 1; row < height; row++) {
	    for (int col = 0; col < width; col++) {
		tiles[row][col] = TileType.GRASS;
	    }
	}

	// Create a simple path
	for (int col = 0; col < width; col++) {
	    tiles[height / 2][col] = TileType.PATH;
	}

	// Start and end points
	tiles[height / 2][0] = TileType.START;
	tiles[height / 2][width - 1] = TileType.END;
    }

    /**
     * Gets the tile type at the specified position.
     *
     * @param row The row index
     * @param col The column index
     *
     * @return The tile type at the specified position, or null if out of bounds
     */
    public TileType getTileAt(int row, int col) {
	if (row < 0 || row >= height || col < 0 || col >= width) {
	    return null;
	}
	return tiles[row][col];
    }


    public void addBoardListener(BoardListener listener) {
	if (listener != null) {
	    synchronized (boardListeners) {
		boardListeners.add(listener);
	    }
	}
    }


    private void notifyListeners() {
	synchronized (boardListeners) {
	    for (BoardListener listener : boardListeners) {
		listener.boardChanged();
	    }
	}
    }


    public void startLevel(Level level) {
	if (level != null) {
	    this.currentLevel = level;
	    notifyListeners();
	}
    }


    public int getHeight() {
	return height;
    }


    public int getWidth() {
	return width;
    }


    public EnemyFactory getEnemyFactory() {
	return enemyFactory;
    }


    public TowerFactory getTowerFactory() {
	return towerFactory;
    }


    public void livesLost() {
	lives--;
	notifyListeners();

	if (lives <= 0) {
	    showGameOverDialog();
	}
    }


    private void showGameOverDialog() {
	synchronized (boardListeners) {
	    for (BoardListener listener : boardListeners) {
		listener.gameOver();
	    }
	}
    }


    public void gainCoins(int amount) {
	coins += amount;
	notifyListeners();
    }


    public int getLives() {
	return lives;
    }


    public int getCoins() {
	return coins;
    }


    public int getRound() {
	return currentLevelIndex + 1;
    }


    public void addExplosion(Point position) {
	if (position != null) {
	    explosions.add(new Explosion(position));
	}
    }


    public List<Explosion> getExplosions() {
	return Collections.unmodifiableList(explosions);
    }
}