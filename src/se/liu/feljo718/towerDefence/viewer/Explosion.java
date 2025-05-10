package se.liu.feljo718.towerdefence.viewer;

import se.liu.feljo718.towerdefence.enemy.Enemy;
import se.liu.feljo718.towerdefence.tower.Tower;

import java.awt.*;

/**
 * Represents a visual explosion effect that appears when a tower hits an enemy.
 * <p>
 * This class models a temporary visual effect that appears at a specific position on the game board and disappears after a predefined
 * lifetime. Explosions are used to provide visual feedback when towers successfully hit enemies.
 * <p>
 * Each explosion has a position and a countdown lifetime that decreases each game tick until it reaches zero, at which point the explosion
 * is considered expired and should be removed from the game.
 *
 * @author feljo718
 * @see Tower
 * @see Enemy
 */
public class Explosion
{
    /** Shows for 170ms at 60fps **/
    private static final int INITIAL_LIFETIME = 10;
    private final Point position;
    private int lifetime;

    public Explosion(Point position) {
	if (position == null) {
	    throw new IllegalArgumentException("Position cannot be null");
	}
	this.position = new Point(position);
	this.lifetime = INITIAL_LIFETIME;
    }

    public Point getPosition() {
	return new Point(position);
    }

    public void decreaseLifetime() {
	lifetime--;
    }

    public boolean isExpired() {
	return lifetime <= 0;
    }
}