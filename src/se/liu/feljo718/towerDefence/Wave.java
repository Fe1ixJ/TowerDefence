package se.liu.feljo718.towerDefence;

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
