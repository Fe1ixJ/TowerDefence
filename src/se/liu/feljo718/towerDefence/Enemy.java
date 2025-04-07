package se.liu.feljo718.towerDefence;

import java.awt.*;
import java.util.Map;

public class Enemy {
    private static final Map<EnemyType, EnemyAttributes> ENEMY_ATTRIBUTES = Map.of(
            EnemyType.BASIC, new EnemyAttributes(100, 0.05),
            EnemyType.FAST, new EnemyAttributes(50, 0.2),
            EnemyType.TANK, new EnemyAttributes(200, 0.075)
    );
    private static final EnemyAttributes DEFAULT_ATTRIBUTES = new EnemyAttributes(100, 1);
    private final EnemyType type;
    private final EnemyPathfinding pathfinding;
    private final double speed;
    private int health;
    private double xPos;
    private double yPos;

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

    public Point getPosition() {
        return new Point((int) Math.round(xPos), (int) Math.round(yPos));
    }

    public Point getPixelPosition() {
        return new Point((int) (xPos * BoardComponent.TILE_SIZE),
                (int) (yPos * BoardComponent.TILE_SIZE));
    }

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

    private record EnemyAttributes(int health, double speed) {
    }
}