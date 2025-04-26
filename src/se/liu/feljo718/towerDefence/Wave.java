package se.liu.feljo718.towerDefence;

/**
 * Represents a wave of enemies in the Tower Defense game.
 * <p>
 * Each wave defines a specific group of enemies that will be sent into the game map.
 * Waves are characterized by the type of enemy, the number of enemies to spawn,
 * and the time delay between spawning individual enemies.
 *
 * @author feljo718
 * @see EnemyType
 * @see EnemyFactory
 */
public class Wave {
    private final EnemyType enemyType;
    private final int count;
    private final int spawnDelay;

    public Wave(EnemyType enemyType, int count, int spawnDelay) {
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
