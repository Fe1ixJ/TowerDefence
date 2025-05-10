package se.liu.feljo718.towerdefence.enemy;

import se.liu.feljo718.towerdefence.board.Level;

/**
 * Represents a wave of enemies in the Tower Defense game.
 * <p>
 * Each wave defines a specific group of enemies that will be sent into the game map. Waves are characterized by the type of enemy, the
 * number of enemies to spawn, and the time delay between spawning individual enemies.
 *
 * @author feljo718
 * @see EnemyType
 * @see EnemyFactory
 * @see Level
 */
public class Wave
{
    private final EnemyType enemyType;
    private final int count;
    private final int spawnDelay;

    /**
     * Creates a new wave configuration with specified enemy type, count, and spawn delay.
     *
     * @param enemyType  The type of enemy to spawn in this wave
     * @param count      The number of enemies to spawn in this wave (must be positive)
     * @param spawnDelay The time in milliseconds between spawning each enemy (must not be negative)
     *
     * @throws IllegalArgumentException if enemyType is null, count is not positive, or spawnDelay is negative
     */
    public Wave(EnemyType enemyType, int count, int spawnDelay) {
	if (enemyType == null) {
	    throw new IllegalArgumentException("Enemy type cannot be null");
	}
	if (count <= 0) {
	    throw new IllegalArgumentException("Enemy count must be positive");
	}
	if (spawnDelay < 0) {
	    throw new IllegalArgumentException("Spawn delay cannot be negative");
	}

	this.enemyType = enemyType;
	this.count = count;
	this.spawnDelay = spawnDelay;
    }

    public EnemyType getEnemyType() {
	return enemyType;
    }

    public int getCount() {
	return count;
    }

    public int getSpawnDelay() {
	return spawnDelay;
    }


}
