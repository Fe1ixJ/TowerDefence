package se.liu.feljo718.towerDefence;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a defensive tower in the Tower Defense game.
 * <p>
 * Towers are placed on the game board by the player to attack incoming enemies.
 * Each tower has a specific type (basic, sniper, splash), which determines its
 * attack pattern, range, and damage capabilities. Towers can be upgraded to
 * increase their effectiveness.
 *
 * @author feljo718
 * @see TowerType
 * @see Board
 * @see Enemy
 */
public class Tower {
    private int level = 1;
    private final TowerType type;
    private final Point position;
    private double lastShotTime;
    private Board board = null;
    private List<Powerup> activePowerups = new ArrayList<>();

    /**
     * Creates a new tower of the specified type at the given position.
     * <p>
     * The tower is initially created at level 1 with no shots fired.
     *
     * @param type     The type of tower to create
     * @param position The position on the game board where the tower is located
     */
    public Tower(TowerType type, Point position) {
        this.type = type;
        this.position = position;
        this.lastShotTime = 0;
    }

    public void applyPowerup(PowerupType type, long duration) {
        activePowerups.add(new Powerup(type, this, duration));
    }

    public boolean hasActivePowerupOfType(PowerupType type) {
        for (Powerup powerup : activePowerups) {
            if (powerup.getType() == type && !powerup.isExpired()) {
                return true;
            }
        }
        return false;
    }

    public List<Powerup> getActivePowerups() {
        // Remove expired powerups
        Iterator<Powerup> iterator = activePowerups.iterator();
        while (iterator.hasNext()) {
            Powerup powerup = iterator.next();
            if (powerup.isExpired()) {
                iterator.remove();
            }
        }
        return activePowerups;
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
        double baseRange = type.getRange(level);
        for (Powerup powerup : getActivePowerups()) {
            if (powerup.getType() == PowerupType.RANGE_BOOST) {
                baseRange *= 1.5; // 50% range increase
            }
        }
        return baseRange;
    }

    public int getDamage() {
        int baseDamage = type.getDamage(level);
        for (Powerup powerup : getActivePowerups()) {
            if (powerup.getType() == PowerupType.DOUBLE_DAMAGE) {
                baseDamage *= 2; // Double damage
            }
        }
        return baseDamage;
    }

    public double getFireRate() {
        double baseFireRate = type.getFireRate(level);
        for (Powerup powerup : getActivePowerups()) {
            if (powerup.getType() == PowerupType.DOUBLE_FIRERATE) {
                baseFireRate *= 2; // Double fire rate
            }
        }
        return baseFireRate;
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

    /**
     * Performs an attack against the target enemy.
     * <p>
     * For splash-type towers, also damages enemies near the target.
     * Updates the last shot time to the current time.
     *
     * @param target      The primary enemy target
     * @param currentTime The current game time in seconds
     */
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

    /**
     * Identifies all enemies within this tower's attack range.
     * <p>
     * Scans all active enemies on the board and returns those within
     * the tower's attack radius.
     *
     * @return A list of enemies within range of this tower
     */
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

    /**
     * Calculates the distance from this tower to the specified enemy.
     * <p>
     * Uses the Euclidean distance formula.
     *
     * @param enemy The enemy to calculate distance to
     * @return The distance in game units
     */
    public double getDistanceTo(Enemy enemy) {
        Point enemyPos = enemy.getPosition();
        return Math.sqrt(Math.pow(position.x - enemyPos.x, 2) +
                Math.pow(position.y - enemyPos.y, 2));
    }

    /**
     * Selects the closest enemy within range as a target.
     * <p>
     * Examines all provided enemies and returns the one closest to the tower
     * that is still within attack range.
     *
     * @param enemies The list of candidate enemies to target
     * @return The selected enemy target, or null if no enemies are in range
     */
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