package se.liu.feljo718.towerdefence.tower;

import se.liu.feljo718.towerdefence.viewer.TowerShop;

/**
 * Defines the different types of towers available in the Tower Defense game.
 * <p>
 * Each tower type has unique characteristics including attack range, damage output, and fire rate. Tower statistics scale with level
 * upgrades, with each type specializing in different combat roles:
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
public enum TowerType
{
    BASIC(new TowerStats[] { new TowerStats(4, 25, 2.2), new TowerStats(4, 40, 2.6), new TowerStats(5, 60, 3.0) }),
    SNIPER(new TowerStats[] { new TowerStats(6, 60, 1.3), new TowerStats(7, 90, 1.6), new TowerStats(8, 130, 1.9) }),
    SPLASH(new TowerStats[] { new TowerStats(3, 20, 1.7), new TowerStats(3, 30, 2.0), new TowerStats(4, 40, 2.3) }),
    BOMBER(new TowerStats[] { new TowerStats(3, 75, 0.6), new TowerStats(3, 105, 0.7), new TowerStats(4, 145, 0.9) }),
    SLOW(new TowerStats[] { new TowerStats(3, 13, 3.3), new TowerStats(3, 20, 3.6), new TowerStats(4, 29, 4.0) });

    private final TowerStats[] levelStats;

    TowerType(TowerStats[] levelStats) {
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

    /** Helper record to store tower statistics for each level **/
    private record TowerStats(int range, int damage, double fireRate)
    {
    }
}