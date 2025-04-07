package se.liu.feljo718.towerDefence;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Tower {
    private int level = 1;
    private final TowerType type;
    private final Point position;
    private double lastShotTime;
    private Board board = null;

    public Tower(TowerType type, Point position) {
        this.type = type;
        this.position = position;
        this.lastShotTime = 0;
    }

    public boolean upgrade() {
        if (level < type.getMaxLevel()) {
            level++;
            return true;
        }
        return false;
    }

    public int getLevel(){
        return level;
    }
    public void setBoard(Board board){
        this.board = board;
    }

    public Point getPosition() {
        return position;
    }

    public TowerType getType() {
        return type;
    }

    public double getRange() {
        return type.getRange(level);
    }

    public int getDamage() {
        return type.getDamage(level);
    }

    public double getFireRate() {
        return type.getFireRate(level);
    }

    public Color getColor() {
        return switch (type) {
            case BASIC -> Color.BLUE;
            case SNIPER -> Color.CYAN;
            case SPLASH -> Color.MAGENTA;
        };
    }

    public boolean canShoot(double currentTime) {
        return currentTime - lastShotTime >= 1.0 / getFireRate();
    }

    public void shoot(Enemy target, double currentTime) {
        if (type == TowerType.SPLASH) {
            // Splash damage affects target and nearby enemies
            List<Enemy> enemies = findEnemiesInRange();
            for (Enemy enemy : enemies) {
                enemy.takeDamage(getDamage());
            }
        } else {
            // Single target damage
            target.takeDamage(getDamage());
        }
        lastShotTime = currentTime;
    }

    public List<Enemy> findEnemiesInRange() {
        List<Enemy> enemiesInRange = new ArrayList<>();
        List<Enemy> allEnemies = board.getEnemyFactory().getEnemies();

        for (Enemy enemy : allEnemies) {
            double distance = getDistanceTo(enemy);
            if (distance <= getRange()) {
                enemiesInRange.add(enemy);
            }
        }
        return enemiesInRange;
    }

    public double getDistanceTo(Enemy enemy) {
        Point enemyPos = enemy.getPosition();
        return Math.sqrt(Math.pow(position.x - enemyPos.x, 2) +
                Math.pow(position.y - enemyPos.y, 2));
    }

    public Enemy findTarget(List<Enemy> enemies) {
        Enemy closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Enemy enemy : enemies) {
            double distance = getDistanceTo(enemy);
            if (distance <= getRange() && distance < minDistance) {
                minDistance = distance;
                closest = enemy;
            }
        }
        return closest;
    }
}