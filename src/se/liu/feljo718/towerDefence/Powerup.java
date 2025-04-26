package se.liu.feljo718.towerDefence;

/**
 * Represents an active powerup applied to a tower.
 * <p>
 * Powerups provide temporary boosts to tower attributes and automatically
 * expire after their duration ends. They modify tower statistics like damage,
 * fire rate, or range for a limited time.
 *
 * @author feljo718
 * @see PowerupType
 * @see Tower
 */
public class Powerup {
    private final PowerupType type;
    private final long startTime;
    private final long duration;
    private final Tower tower;

    /**
     * Creates a new powerup of the specified type applied to a tower.
     *
     * @param type     The type of powerup to apply
     * @param tower    The tower to apply the powerup to
     * @param duration The duration in milliseconds that the powerup remains active
     */
    public Powerup(PowerupType type, Tower tower, long duration) {
        this.type = type;
        this.tower = tower;
        this.startTime = System.currentTimeMillis();
        this.duration = duration;
    }

    /**
     * Checks if the powerup's duration has expired.
     *
     * @return true if the powerup has expired, false otherwise
     */
    public boolean isExpired() {
        return System.currentTimeMillis() - startTime >= duration;
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

    public PowerupType getType() {
        return type;
    }

    public Tower getTower() {
        return tower;
    }
}