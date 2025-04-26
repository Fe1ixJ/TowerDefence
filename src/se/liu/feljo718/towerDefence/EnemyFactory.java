package se.liu.feljo718.towerDefence;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class responsible for creating and managing enemies in the Tower Defense game.
 * <p>
 * This class handles enemy lifecycle including spawning, movement, position tracking,
 * and removal. It maintains a collection of all active enemies on the game board.
 *
 * @author feljo718
 * @see Enemy
 * @see EnemyType
 */
public class EnemyFactory {
    private final Board board;
    private final List<Enemy> enemies;

    /**
     * Creates a new enemy factory for the specified game board.
     * <p>
     * Initializes an empty collection to track enemies.
     *
     * @param board The game board where enemies will be placed
     */
    public EnemyFactory(Board board) {
        this.board = board;
        this.enemies = new ArrayList<>();
    }

    public void spawnEnemy(EnemyType type) {
        Enemy enemy = new Enemy(type, board);
        enemies.add(enemy);
    }

    public void moveEnemies() {
        for (Enemy enemy : enemies) {
            enemy.move();
        }
    }

    /**
     * Retrieves the current grid positions of all active enemies.
     *
     * @return A list of points representing enemy positions
     */
    //public List<Point> getEnemyPositions() {
    //    List<Point> positions = new ArrayList<>();
    //    for (Enemy enemy : enemies) {
    //        positions.add(enemy.getPosition());
    //    }
    //    return positions;
    //}

    public void removeFinishedEnemies() {
        for (Enemy enemy : new ArrayList<>(enemies)) {
            if (enemy.hasReachedEnd()) {
                enemies.remove(enemy);
            } else if (enemy.isDead()) {
                board.gainCoins(5); // How much coins to give for killing an enemy
                enemies.remove(enemy);
            }
        }

    }

    public List<Enemy> getEnemies() {
        return enemies;
    }
}