package se.liu.feljo718.towerdefence.powerup;

import se.liu.feljo718.towerdefence.tower.Tower;

/**
 * Abstract base class for temporary tower enhancements in the Tower Defense game.
 * <p>
 * This class provides the foundation for various powerup types that can modify tower behavior and attributes for a limited duration.
 * Powerups manage their own lifecycle including application, duration tracking, and effect removal when expired.
 * <p>
 * Each powerup maintains a reference to its affected tower and applies specific enhancements like increased damage, range, or attack speed.
 * The class automatically handles timing and ensures effects are properly cleaned up when the powerup expires, returning towers to their
 * base state.
 *
 * @author feljo718
 */
public abstract class Powerup
{
    protected final long startTime;
    protected final long duration;
    protected final Tower tower;
    /** The type of this powerup, determining its behavior and effects */
    public final PowerupType powerupType;
    protected final double multiplier;
    protected boolean effectActive;

    /**
     * Creates a new powerup applied to a tower.
     *
     * @param tower                  The tower to apply the powerup to
     * @param duration               The duration in milliseconds that the powerup remains active
     * @param powerupType            The type of powerup
     * @param multiplier             The multiplier value for this powerup
     * @param applyEffectImmediately If true, the effect will be applied during initialization
     */
    protected Powerup(Tower tower, long duration, PowerupType powerupType, double multiplier, boolean applyEffectImmediately)
    {
	this.tower = tower;
	this.startTime = System.currentTimeMillis();
	this.duration = duration;
	this.effectActive = true;
	this.powerupType = powerupType;
	this.multiplier = multiplier;
    }

    /**
     * Checks if the powerup's duration has expired.
     *
     * @return true if the powerup has expired, false otherwise
     */
    public boolean isExpired() {
	boolean expired = System.currentTimeMillis() - startTime >= duration;
	if (expired && effectActive) {
	    removeEffect();
	    effectActive = false;
	}
	return expired;
    }

    /**
     * Gets the remaining duration of the powerup in milliseconds.
     *
     * @return the remaining time in milliseconds
     */
    public long getRemainingTime() {
	long elapsed = System.currentTimeMillis() - startTime;
	return Math.max(0, duration - elapsed);
    }

    /**
     * Apply the powerup effect to the tower.
     */
    protected abstract void applyEffect();

    /**
     * Remove the powerup effect from the tower.
     */
    protected abstract void removeEffect();
}