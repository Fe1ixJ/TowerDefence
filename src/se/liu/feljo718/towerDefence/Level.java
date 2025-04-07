package se.liu.feljo718.towerDefence;

import java.util.ArrayList;
import java.util.List;

public class Level {
    private final String name;
    private final List<Wave> waves;
    private final int waveCooldown;
    private int currentWave;
    private int enemiesSpawned;
    private long lastSpawnTime;

    public Level(String name, int waveCooldown) {
        this.name = name;
        this.waves = new ArrayList<>();
        this.waveCooldown = waveCooldown;
        this.currentWave = 0;
        this.enemiesSpawned = 0;
        this.lastSpawnTime = System.currentTimeMillis();
    }

    public void addWave(Wave wave) {
        waves.add(wave);
    }

    public boolean shouldSpawnEnemy(long currentTime) {
        if (currentWave >= waves.size()) return false;

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


    public EnemyType getCurrentEnemyType() {
        return waves.get(currentWave).getEnemyType();
    }

    public boolean isCompleted() {
        return currentWave >= waves.size();
    }

}

