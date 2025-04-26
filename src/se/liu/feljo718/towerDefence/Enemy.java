package se.liu.feljo718.towerDefence;

import java.awt.*;
import java.util.Map;

/**
 * Represents an enemy unit in the Tower Defense game.
 * <p>
 * This class manages enemy movement along the game path, health tracking,
 * damage handling, and state information. Different enemy types have varying
 * attributes such as health and movement speed.
 *
 * @author feljo718
 * @see EnemyType
 * @see EnemyPathfinding
 */
public class Enemy {
    private static final Map<EnemyType, EnemyAttributes> ENEMY_ATTRIBUTES = Map.of(
            EnemyType.BASIC, new EnemyAttributes(100, 0.05),
            EnemyType.FAST, new EnemyAttributes(50, 0.2),
            EnemyType.TANK, new EnemyAttributes(200, 0.075)
    );
    private EnemyAttributes DEFAULT_ATTRIBUTES = new EnemyAttributes(100, 1);
    private final EnemyType type;
    private final EnemyPathfinding pathfinding;
    private final double speed;
    private int health;
    private double xPos;
    private double yPos;

    /**
     * Constructs a new enemy of the specified type.
     * <p>
     * The enemy is positioned at the start point of the path on the given board
     * and initialized with health and speed values based on its type.
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

        EnemyAttributes attributes = ENEMY_ATTRIBUTES.getOrDefault(type, DEFAULT_ATTRIBUTES);
        this.health = attributes.health();
        this.speed = attributes.speed();
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
        return new Point((int) (xPos * BoardComponent.TILE_SIZE),
                (int) (yPos * BoardComponent.TILE_SIZE));
    }

    /**
     * Updates the enemy's position as it moves along the path.
     * <p>
     * Calculates movement toward the next waypoint based on the enemy's speed.
     * When a waypoint is reached, the pathfinding system is updated to target
     * the next point in the path.
     */
    public void move() {
        Point target = pathfinding.getNextPosition();
        if (target != null) {
            double dx = target.x - xPos;
            double dy = target.y - yPos;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance <= speed) {
                xPos = target.x;
                yPos = target.y;
                pathfinding.reachedTargetPoint();
            } else {
                xPos += (dx / distance) * speed;
                yPos += (dy / distance) * speed;
            }
        }
    }

    public void takeDamage(int damage) {
        health -= damage;
        System.out.println("Enemy health: " + health);
    }

    public boolean isDead() {
        return health <= 0;
    }

    public boolean hasReachedEnd() {
        return pathfinding.hasReachedEnd();
    }

    public EnemyType getType() {
        return type;
    }

    /**
     * Record containing the health and speed attributes for different enemy types.
     */
    private record EnemyAttributes(int health, double speed) {
    }
}