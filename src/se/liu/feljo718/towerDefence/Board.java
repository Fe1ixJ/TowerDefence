package se.liu.feljo718.towerDefence;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
    private int lives = 100; // Starting lives
    private int coins = 1000; // Starting coins


    public Board(final int width, final int height) {
        this.width = width;
        this.height = height + 1;
        this.boardListeners = new ArrayList<>();
        this.levels = new ArrayList<>();
        loadMap();
        this.enemyFactory = new EnemyFactory(this);
        this.towerFactory = new TowerFactory(this);
        loadLevels();

        //enemyFactory.spawnEnemy(EnemyType.FAST);
        towerFactory.placeTower(TowerType.BASIC, new Point(6, 7));
        //towerFactory.placeTower(TowerType.SNIPER, new Point(4, 6));
        //towerFactory.placeTower(TowerType.SPLASH, new Point(5, 6));
    }

    private void loadLevels() {
        LevelReader levelReader = new LevelReader();
        this.levels = levelReader.loadLevels();
    }

    public void startNextRound() {
        if (currentLevel == null || currentLevel.isCompleted()) {
            currentLevelIndex++;
            if (currentLevelIndex < levels.size()) {
                startLevel(levels.get(currentLevelIndex));
            }
        }
    }

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
