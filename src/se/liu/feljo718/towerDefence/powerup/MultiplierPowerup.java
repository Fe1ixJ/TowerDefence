package se.liu.feljo718.towerdefence.powerup;

import se.liu.feljo718.towerdefence.tower.Tower;

/**
 * A unified powerup implementation that applies multiplier effects to different tower attributes.
 * <p>
 * This class provides a flexible implementation for powerups that temporarily enhance tower capabilities through numerical multipliers. It
 * supports modifying different tower attributes including damage output, attack speed, and attack range.
 * <p>
 * The design follows a unified approach where a single class handles different powerup types through the PowerupAttribute enum, replacing
 * multiple specialized powerup classes. Factory methods provide convenient creation of standard powerup types with predefined multiplier
 * values.
 *
 * @author feljo718
 */
public class MultiplierPowerup extends Powerup
{
    /** Standard multipliers from original implementations */
    private static final double DOUBLE_DAMAGE_MULTIPLIER = 2.0;
    private static final double DOUBLE_FIRE_RATE_MULTIPLIER = 2.0;
    private static final double RANGE_BOOST_MULTIPLIER = 1.5;

    private final PowerupAttribute attribute;

    /**
     * Creates a powerup that applies a multiplier to a specific tower attribute.
     *
     * @param tower       The tower to apply the powerup to
     * @param duration    The duration in milliseconds that the powerup remains active
     * @param powerupType The type of powerup
     * @param multiplier  The multiplier value for this powerup
     * @param attribute   The tower attribute to apply the multiplier to
     */
    public MultiplierPowerup(Tower tower, long duration, PowerupType powerupType, double multiplier, PowerupAttribute attribute) {
	super(tower, duration, powerupType, multiplier, false);
	this.attribute = attribute;
	applyEffect();
    }

    /**
     * Factory method for creating a double damage powerup.
     *
     * @param tower    The tower to apply the powerup to
     * @param duration The duration in milliseconds
     *
     * @return A new double damage powerup
     */
    public static MultiplierPowerup createDoubleDamage(Tower tower, long duration) {
	return new MultiplierPowerup(tower, duration, PowerupType.DOUBLE_DAMAGE, DOUBLE_DAMAGE_MULTIPLIER, PowerupAttribute.DAMAGE);
    }

    /**
     * Factory method for creating a double fire rate powerup.
     *
     * @param tower    The tower to apply the powerup to
     * @param duration The duration in milliseconds
     *
     * @return A new double fire rate powerup
     */
    public static MultiplierPowerup createDoubleFireRate(Tower tower, long duration) {
	return new MultiplierPowerup(tower, duration, PowerupType.DOUBLE_FIRERATE, DOUBLE_FIRE_RATE_MULTIPLIER, PowerupAttribute.FIRE_RATE);
    }

    /**
     * Factory method for creating a range boost powerup.
     *
     * @param tower    The tower to apply the powerup to
     * @param duration The duration in milliseconds
     *
     * @return A new range boost powerup
     */
    public static MultiplierPowerup createRangeBoost(Tower tower, long duration) {
	return new MultiplierPowerup(tower, duration, PowerupType.RANGE_BOOST, RANGE_BOOST_MULTIPLIER, PowerupAttribute.RANGE);
    }

    @Override protected void applyEffect() {
	switch (attribute) {
	    case DAMAGE:
		tower.applyDamageMultiplier(multiplier);
		break;
	    case FIRE_RATE:
		tower.applyFireRateMultiplier(multiplier);
		break;
	    case RANGE:
		tower.applyRangeMultiplier(multiplier);
		break;
	}
    }

    @Override protected void removeEffect() {
	switch (attribute) {
	    case DAMAGE:
		tower.resetDamageMultiplier();
		break;
	    case FIRE_RATE:
		tower.resetFireRateMultiplier();
		break;
	    case RANGE:
		tower.resetRangeMultiplier();
		break;
	}
    }

    public enum PowerupAttribute
    {
	DAMAGE, FIRE_RATE, RANGE
    }
}