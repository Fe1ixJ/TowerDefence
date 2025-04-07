package se.liu.feljo718.towerDefence;

public enum TowerType {
    BASIC(0, new TowerStats[]{
            new TowerStats(3, 20, 4.0),  // Level 1
            new TowerStats(4, 30, 5.0),  // Level 2
            new TowerStats(5, 45, 6.0)   // Level 3
    }),
    SNIPER(1, new TowerStats[]{
            new TowerStats(6, 25, 0.5),  // Level 1
            new TowerStats(8, 40, 0.7),  // Level 2
            new TowerStats(10, 60, 1.0)  // Level 3
    }),
    SPLASH(2, new TowerStats[]{
            new TowerStats(2, 15, 1.5),  // Level 1
            new TowerStats(3, 25, 2.0),  // Level 2
            new TowerStats(4, 40, 2.5)   // Level 3
    });

    private final int id;
    private final TowerStats[] levelStats;

    TowerType(int id, TowerStats[] levelStats) {
        this.id = id;
        this.levelStats = levelStats;
    }

    public int getRange(int level) {
        return levelStats[level - 1].range();
    }

    public int getDamage(int level) {
        return levelStats[level - 1].damage();
    }

    public double getFireRate(int level) {
        return levelStats[level - 1].fireRate();
    }

    public int getMaxLevel() {
        return levelStats.length;
    }

    // Helper record to store tower statistics for each level
    private record TowerStats(int range, int damage, double fireRate) {}
}