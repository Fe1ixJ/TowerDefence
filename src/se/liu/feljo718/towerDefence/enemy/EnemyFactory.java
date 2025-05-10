package se.liu.feljo718.towerdefence.enemy;

import se.liu.feljo718.towerdefence.board.Board;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory class responsible for creating and managing enemies in the Tower Defense game.
 * <p>
 * This class handles enemy lifecycle including spawning, movement, position tracking, and removal. It maintains a collection of all active
 * enemies on the game board.
 *
 * @author feljo718
 * @see Enemy
 * @see EnemyType
 */
public class EnemyFactory
{
    private static final int ENEMY_KILL_REWARD = 5;
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

    public void removeFinishedEnemies() {
	for (Enemy enemy : new ArrayList<>(enemies)) {
	    if (enemy.hasReachedEnd()) {
		enemies.remove(enemy);
	    } else if (enemy.isDead()) {
		board.gainCoins(ENEMY_KILL_REWARD);
		enemies.remove(enemy);
	    }
	}
    }

    public List<Enemy> getEnemies() {
	return enemies;
    }
}