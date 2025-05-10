package se.liu.feljo718.towerdefence.board;

import se.liu.feljo718.towerdefence.enemy.EnemyType;
import se.liu.feljo718.towerdefence.enemy.Wave;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a game level in the Tower Defense game.
 * <p>
 * This class manages the progression of enemy waves, controls spawn timing, and tracks completion status of the level. Each level consists
 * of multiple waves of enemies with configurable delay between waves.
 *
 * @author feljo718
 * @see Wave
 * @see EnemyType
 */
public class Level
{
    private final List<Wave> waves;
    private final int waveCooldown;
    private int currentWave;
    private int enemiesSpawned;
    private long lastSpawnTime;

    /**
     * Creates a new level with the specified name and wave cooldown period.
     * <p>
     * Initializes an empty collection of waves and sets initial counters.
     *
     * @param name         The display name of this level
     * @param waveCooldown The time in milliseconds between consecutive waves
     */
    public Level(int waveCooldown) {
	this.waves = new ArrayList<>();
	this.waveCooldown = waveCooldown;
	resetLevel();
    }

    public void resetLevel() {
	this.currentWave = 0;
	this.enemiesSpawned = 0;
	this.lastSpawnTime = System.currentTimeMillis();
    }

    public void addWave(Wave wave) {
	if (wave != null) {
	    waves.add(wave);
	}
    }

    /**
     * Determines if an enemy should be spawned at the current time.
     *
     * @param currentTime The current system time in milliseconds
     *
     * @return {@code true} if an enemy should be spawned; {@code false} otherwise
     */
    public boolean shouldSpawnEnemy(long currentTime) {
	if (currentWave >= waves.size()) {
	    return false;
	}

	Wave currentWaveObj = waves.get(currentWave);

	// Check if wave is complete
	if (enemiesSpawned >= currentWaveObj.getCount()) {
	    if (currentTime - lastSpawnTime >= waveCooldown) {
		currentWave++;
		enemiesSpawned = 0;
		lastSpawnTime = currentTime;
	    }
	    return false;
	}

	// Check spawn delay
	if (currentTime - lastSpawnTime >= currentWaveObj.getSpawnDelay()) {
	    enemiesSpawned++;
	    lastSpawnTime = currentTime;
	    return true;
	}

	return false;
    }

    /**
     * Gets the enemy type for the current wave.
     *
     * @return The enemy type to spawn
     */
    public EnemyType getCurrentEnemyType() {
	if (currentWave < waves.size()) {
	    return waves.get(currentWave).getEnemyType();
	}
	return null;
    }

    /**
     * Checks if the level is completed (all waves processed).
     *
     * @return {@code true} if level is completed; {@code false} otherwise
     */
    public boolean isCompleted() {
	return currentWave >= waves.size();
    }
}