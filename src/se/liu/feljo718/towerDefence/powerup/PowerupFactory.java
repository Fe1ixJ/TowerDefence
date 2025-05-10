package se.liu.feljo718.towerdefence.powerup;

import se.liu.feljo718.towerdefence.tower.Tower;

/**
 * Factory for creating different types of powerups in the Tower Defense game.
 * <p>
 * This factory class centralizes the creation of all powerup types, abstracting the implementation details and specific powerup classes
 * from client code. It provides a simple interface to create various powerup instances based on the requested PowerupType.
 * <p>
 * The factory handles the initialization details of different powerup implementations, ensuring that towers receive properly configured
 * enhancements with appropriate multiplier values and behaviors according to the powerup type.
 *
 * @author feljo718
 */
public class PowerupFactory
{

    /**
     * Creates a powerup of the specified type.
     *
     * @param type     The type of powerup to create
     * @param tower    The tower to apply the powerup to
     * @param duration The duration in milliseconds
     *
     * @return A new powerup instance
     */
    public static Powerup createPowerup(PowerupType type, Tower tower, long duration) {
	return switch (type) {
	    case DOUBLE_DAMAGE -> MultiplierPowerup.createDoubleDamage(tower, duration);
	    case DOUBLE_FIRERATE -> MultiplierPowerup.createDoubleFireRate(tower, duration);
	    case RANGE_BOOST -> MultiplierPowerup.createRangeBoost(tower, duration);
	};
    }
}