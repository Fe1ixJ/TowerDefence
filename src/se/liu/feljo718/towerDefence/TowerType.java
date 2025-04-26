package se.liu.feljo718.towerDefence;

/**
 * Defines the different types of towers available in the Tower Defense game.
 * <p>
 * Each tower type has unique characteristics including attack range, damage output,
 * and fire rate. Tower statistics scale with level upgrades, with each type
 * specializing in different combat roles:
 * <ul>
 *   <li>BASIC: Balanced towers with moderate range, damage, and fire rate</li>
 *   <li>SNIPER: Long-range towers with high damage but slow fire rate</li>
 *   <li>SPLASH: Area-of-effect towers that can damage multiple enemies</li>
 * </ul>
 *
 * @author feljo718
 * @see Tower
 * @see TowerFactory
 * @see TowerShop
 */
public enum TowerType {
    BASIC(0, new TowerStats[]{
            new TowerStats(3, 20, 4.0),  // Level 1
            new TowerStats(3, 35, 4.5),  // Level 2
            new TowerStats(3, 45, 6.0)   // Level 3
    }),
    SNIPER(1, new TowerStats[]{
            new TowerStats(5, 40, 0.5),  // Level 1
            new TowerStats(6, 60, 0.7),  // Level 2
            new TowerStats(8, 90, 1.0)  // Level 3
    }),
    SPLASH(2, new TowerStats[]{
            new TowerStats(2, 15, 1.5),  // Level 1
            new TowerStats(2, 25, 2.0),  // Level 2
            new TowerStats(3, 40, 2.5)   // Level 3
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