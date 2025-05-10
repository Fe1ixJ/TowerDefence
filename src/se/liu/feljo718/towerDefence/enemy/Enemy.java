package se.liu.feljo718.towerdefence.enemy;

import se.liu.feljo718.towerdefence.board.Board;
import se.liu.feljo718.towerdefence.board.BoardComponent;
import se.liu.feljo718.towerdefence.handler.LogHandler;

import java.awt.*;
import java.util.Map;

/**
 * Represents an enemy unit in the Tower Defense game.
 * <p>
 * This class manages enemy movement along the game path, health tracking, damage handling, and state information. Different enemy types
 * have varying attributes such as health and movement speed.
 *
 * @author feljo718
 * @see EnemyType
 * @see EnemyPathfinding
 */
public class Enemy
{
    private static final int DEFAULT_HEALTH = 100;
    private static final double DEFAULT_SPEED = 1.0;
    private static final Map<EnemyType, EnemyAttributes> ENEMY_ATTRIBUTES =
	    Map.of(EnemyType.BASIC, new EnemyAttributes(100, 0.1),
		   EnemyType.FAST, new EnemyAttributes(150, 0.2),
		   EnemyType.TANK, new EnemyAttributes(500, 0.075),
		   EnemyType.SLOW, new EnemyAttributes(350, 0.05),
		   EnemyType.BOSS, new EnemyAttributes(2500, 0.06));

    private final EnemyType type;
    private final EnemyPathfinding pathfinding;
    private final double speed;
    private int health;
    private double xPos;
    private double yPos;
    private double currentSpeedModifier = 1.0;
    private long speedModifierEndTime = 0;

    /**
     * Constructs a new enemy of the specified type.
     * <p>
     * The enemy is positioned at the start point of the path on the given board and initialized with health and speed values based on its
     * type.
     *
     * @param type  The type of enemy to create
     * @param board The game board containing the path for this enemy
     */
    public Enemy(EnemyType type, Board board) {
	this.type = type;
	this.pathfinding = new EnemyPathfinding(board);
	Point startPos = pathfinding.getCurrentPos();
	this.xPos = startPos.x;
	this.yPos = startPos.y;

	EnemyAttributes attributes = ENEMY_ATTRIBUTES.getOrDefault(type, new EnemyAttributes(DEFAULT_HEALTH, DEFAULT_SPEED));
	this.health = attributes.health();
	this.speed = attributes.speed();
    }

    /**
     * Applies a speed modifier to the enemy for a specific duration.
     * Lower values of factor mean slower movement (0.5 = half speed).
     *
     * @param factor The speed multiplier to apply (between 0 and 1)
     * @param duration The duration in milliseconds that this effect lasts
     */
    public void applySpeedModifier(double factor, int duration) {
	// Only apply if this modifier is stronger than current one
	if (factor < currentSpeedModifier) {
	    currentSpeedModifier = factor;
	    speedModifierEndTime = System.currentTimeMillis() + duration;

	    LogHandler.fine(Enemy.class,
			    "Speed reduced to " + (factor * 100) + "% for " +
			    (duration / 1000.0) + " seconds");
	}
    }

    private void updateSpeedModifier() {
	// Check if speed modifier has expired
	if (System.currentTimeMillis() > speedModifierEndTime && currentSpeedModifier < 1.0) {
	    currentSpeedModifier = 1.0; // Reset to normal speed
	    LogHandler.fine(Enemy.class, "Speed returned to normal");
	}
    }

    /**
     * Returns the current board grid position of this enemy.
     *
     * @return A Point containing the enemy's current grid coordinates
     */
    public Point getPosition() {
	return new Point((int) Math.round(xPos), (int) Math.round(yPos));
    }

    /**
     * Returns the current pixel position of this enemy on the screen.
     * <p>
     * Converts the internal grid position to pixel coordinates based on tile size.
     *
     * @return A Point containing the enemy's current pixel coordinates
     */
    public Point getPixelPosition() {
	return new Point((int) (xPos * BoardComponent.TILE_SIZE), (int) (yPos * BoardComponent.TILE_SIZE));
    }

    /**
     * Updates the enemy's position as it moves along the path.
     * <p>
     * Calculates movement toward the next waypoint based on the enemy's speed. When a waypoint is reached, the pathfinding system is
     * updated to target the next point in the path.
     */
    public void move() {
	updateSpeedModifier();
	double adjustedSpeed = speed * currentSpeedModifier;
	Point target = pathfinding.getNextPosition();
	if (target != null) {
	    if (moveTowardsTarget(target, adjustedSpeed)) {
		pathfinding.reachedTargetPoint();
	    }
	}
    }

    private boolean moveTowardsTarget(Point target, double currentSpeed) {
	double dx = target.x - xPos;
	double dy = target.y - yPos;
	double distance = Math.sqrt(dx * dx + dy * dy);

	if (distance <= currentSpeed) {
	    xPos = target.x;
	    yPos = target.y;
	    return true;
	} else {
	    xPos += (dx / distance) * currentSpeed;
	    yPos += (dy / distance) * currentSpeed;
	    return false;
	}
    }

    public void takeDamage(int damage) {
	health -= damage;
    }

    public boolean isDead() {
	return health <= 0;
    }

    public boolean hasReachedEnd() {
	return pathfinding.hasReachedEnd();
    }

    public Color getColor() {
	return switch (type) {
	    case BASIC -> Color.BLACK;
	    case FAST -> Color.RED;
	    case TANK -> Color.GREEN;
	    case SLOW -> new Color(30, 144, 255);
	    case BOSS -> new Color(128, 0, 128);
	    default -> Color.GRAY; // Fallback color
	};
    }

    /**
     * Record containing the health and speed attributes for different enemy types.
     */
    private record EnemyAttributes(int health, double speed)
    {
    }
}