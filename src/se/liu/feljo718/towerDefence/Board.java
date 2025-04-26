package se.liu.feljo718.towerDefence;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The core game board that manages the state and logic of the Tower Defense game.
 * <p>
 * This class coordinates the game elements including the map grid, enemy waves,
 * tower placement, player resources, and game progression. The board serves as
 * the central component connecting different game subsystems.
 * <p>
 * The board uses a coordinate system with [row, column] indexing where [0,0] is
 * at the top-left. The first row (row 0) is reserved for interface elements,
 * with the playable map starting at row 1.
 *
 * @author feljo718
 */
public class Board {
    private static final String MAP_FILE = "maps.json";
    private final List<BoardListener> boardListeners;
    private final int width;
    private final int height;
    private TileType[][] tiles;
    private final EnemyFactory enemyFactory;
    private final TowerFactory towerFactory;
    private Level currentLevel = null;
    private List<Level> levels;
    private int currentLevelIndex = -1;
    private int lives = 9; // Starting lives
    private int coins = 1500; // Starting coins


    public Board(final int width, final int height) {
        this.width = width;
        this.height = height + 1;
        this.boardListeners = new ArrayList<>();
        this.levels = new ArrayList<>();
        loadMap();
        this.enemyFactory = new EnemyFactory(this);
        this.towerFactory = new TowerFactory(this);
        loadLevels();

    }

    private void loadLevels() {
        LevelReader levelReader = new LevelReader();
        this.levels = levelReader.loadLevels();
    }

    /**
     * Initiates the next game level or wave when appropriate.
     * <p>
     * If the current level is completed or no level is active, this method
     * advances to the next level in the sequence. If all levels have been
     * completed, no further action is taken.
     */
    public void startNextRound() {
        if (currentLevel == null || currentLevel.isCompleted()) {
            // If a level was just completed (and it wasn't the initial state)
            if (currentLevel != null && currentLevel.isCompleted()) {
                // Award bonus coins for completing the level
                int levelCompletionBonus = 100; // Base bonus amount
                gainCoins(levelCompletionBonus);
                System.out.println("Level completed! Bonus: " + levelCompletionBonus + " coins");
            }

            currentLevelIndex++;
            if (currentLevelIndex < levels.size()) {
                startLevel(levels.get(currentLevelIndex));
            }
        }
    }

    /**
     * Updates the game state for a single frame.
     * <p>
     * This method is called repeatedly as part of the game loop and performs
     * several key operations:
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
        if (currentLevel != null && !currentLevel.isCompleted()) {
            if (currentLevel.shouldSpawnEnemy(currentTime)) {
                enemyFactory.spawnEnemy(currentLevel.getCurrentEnemyType());
            }
        }

        enemyFactory.moveEnemies();
        towerFactory.tick();
        enemyFactory.removeFinishedEnemies();

        notifyListeners();
    }

    /**
     * Loads the game map from the configured file.
     * <p>
     * Creates a tile grid with an additional interface row at the top.
     * The method reads a map layout from the JSON file and adds an interface
     * row at position 0, shifting the actual game map down by one row.
     */
    private void loadMap() {
        // Use the MAP_FILE constant instead of hardcoding "maps.json"
        MapReader mapReader = new MapReader(MAP_FILE, width, height - 1);
        TileType[][] gameMap = mapReader.getMap();

        // Create new tiles array with interface row
        tiles = new TileType[height][width];

        // Fill first row with interface tiles
        for (int col = 0; col < width; col++) {
            tiles[0][col] = TileType.INTERFACE;
        }

        // Copy game map below interface row
        for (int row = 0; row < height - 1; row++) {
            System.arraycopy(gameMap[row], 0, tiles[row + 1], 0, width);
        }
    }

    public TileType getTileAt(int row, int col) {
        return tiles[row][col];
    }

    // Listener management
    public void addBoardListener(BoardListener listener) {
        boardListeners.add(listener);
    }

    // Notifies UI components when board state changes
    private void notifyListeners() {
        for (BoardListener listener : boardListeners) {
            listener.boardChanged();
        }
    }

    public void startLevel(Level level) {
        this.currentLevel = level;
        notifyListeners();
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
        System.out.println("Lives: " + lives);
        notifyListeners();

        if (lives <= 0) {
            showGameOverDialog();
        }
    }

    private void showGameOverDialog() {
        for (BoardListener listener : boardListeners) {
            listener.gameOver();
        }
    }


    public void gainCoins(int amount) {
        coins += amount;
        System.out.println("Coins: " + coins);
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
}
