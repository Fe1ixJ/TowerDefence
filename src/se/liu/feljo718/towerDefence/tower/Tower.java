package se.liu.feljo718.towerdefence.tower;

import se.liu.feljo718.towerdefence.board.Board;
import se.liu.feljo718.towerdefence.enemy.Enemy;
import se.liu.feljo718.towerdefence.handler.LogHandler;
import se.liu.feljo718.towerdefence.handler.SoundManager;
import se.liu.feljo718.towerdefence.powerup.Powerup;
import se.liu.feljo718.towerdefence.powerup.PowerupFactory;
import se.liu.feljo718.towerdefence.powerup.PowerupType;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class representing a defensive tower in the Tower Defense game. Provides common functionality for all tower types.
 *
 * @author feljo718
 * @see TowerType
 * @see Board
 * @see Enemy
 */
public abstract class Tower
{
    private static final float SOUND_VOLUME_REDUCTION = -30.0f;
    private static final double DEFAULT_MULTIPLIER = 1.0;
    private final Point position;
    /** List to store active powerups */
    private final List<Powerup> activePowerups = new ArrayList<>();
    private int level = 1;
    private double lastShotTime;
    private Board board = null;
    private double damageMultiplier = DEFAULT_MULTIPLIER;
    private double fireRateMultiplier = DEFAULT_MULTIPLIER;
    private double rangeMultiplier = DEFAULT_MULTIPLIER;

    /**
     * Creates a new tower of the specified type at the given position.
     * <p>
     * The tower is initially created at level 1 with no shots fired.
     *
     * @param position The position on the game board where the tower is located
     */
    protected Tower(Point position) {
	this.position = position;
	this.lastShotTime = 0;
    }

    /**
     * Applies a powerup to this tower.
     *
     * @param type     The type of powerup to apply
     * @param duration The duration in milliseconds
     */
    public void applyPowerup(PowerupType type, long duration) {
	Powerup powerup = PowerupFactory.createPowerup(type, this, duration);
	activePowerups.add(powerup);
    }

    /**
     * Processes this tower's attack against enemies.
     *
     * @param enemies  List of enemies that can be targeted
     * @param gameTime Current game time
     */
    public void processAttack(List<Enemy> enemies, double gameTime) {
	Enemy target = findTarget(enemies);
	if (target != null) {
	    shoot(target, gameTime);
	}
    }

    /**
     * Checks if this tower has an active powerup of the specified type.
     *
     * @param queryType The powerup type to check for
     *
     * @return true if the tower has an active powerup of the specified type
     */
    public boolean hasActivePowerupOfType(PowerupType type) {
	for (Powerup powerup : activePowerups) {
	    if (powerup.powerupType == type) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Updates powerups and removes expired ones.
     */
    public void updatePowerups() {
	activePowerups.removeIf(Powerup::isExpired);
    }

    /**
     * Gets the list of currently active powerups.
     *
     * @return List of active powerups
     */
    public List<Powerup> getActivePowerups() {
	return new ArrayList<>(activePowerups);
    }

    public boolean upgrade() {
	if (level < getType().getMaxLevel()) {
	    level++;
	    return true;
	}
	return false;
    }

    public int getLevel() {
	return level;
    }

    public void setBoard(Board board) {
	this.board = board;
    }

    public Point getPosition() {
	return position;
    }

    /**
     * Applies a damage multiplier to this tower.
     *
     * @param multiplier The damage multiplier to apply
     */
    public void applyDamageMultiplier(double multiplier) {
	this.damageMultiplier = multiplier;
    }

    /**
     * Applies a fire rate multiplier to this tower.
     *
     * @param multiplier The fire rate multiplier to apply
     */
    public void applyFireRateMultiplier(double multiplier) {
	this.fireRateMultiplier = multiplier;
    }

    /**
     * Applies a range multiplier to this tower.
     *
     * @param multiplier The range multiplier to apply
     */
    public void applyRangeMultiplier(double multiplier) {
	this.rangeMultiplier = multiplier;
    }

    /**
     * Resets the damage multiplier to 1.0 (normal damage).
     */
    public void resetDamageMultiplier() {
	this.damageMultiplier = DEFAULT_MULTIPLIER;
    }

    /**
     * Resets the fire rate multiplier to 1.0 (normal fire rate).
     */
    public void resetFireRateMultiplier() {
	this.fireRateMultiplier = DEFAULT_MULTIPLIER;
    }

    /**
     * Resets the range multiplier to 1.0 (normal range).
     */
    public void resetRangeMultiplier() {
	this.rangeMultiplier = DEFAULT_MULTIPLIER;
    }

    public boolean canShoot(double currentTime) {
	return currentTime - lastShotTime >= 1.0 / getFireRate();
    }

    /**
     * Performs an attack against the target enemy.
     * <p>
     * For splash-type towers, also damages enemies near the target. Updates the last shot time to the current time.
     *
     * @param target      The primary enemy target
     * @param currentTime The current game time in seconds
     */
    public void shoot(Enemy target, double currentTime) {
	playShootSound();
	board.addExplosion(target.getPixelPosition());

	// Call the subclass implementation
	performAttack(target);
	lastShotTime = currentTime;
    }

    /**
     * Returns the tower type.
     *
     * @return The tower type
     */
    public abstract TowerType getType();

    /**
     * Get the color representation for this tower type.
     *
     * @return Color for rendering the tower
     */
    public abstract Color getColor();

    /**
     * Perform tower-specific attack logic.
     *
     * @param target The primary target
     */
    protected abstract void performAttack(Enemy target);


    /**
     * Identifies all enemies within this tower's attack range.
     * <p>
     * Scans all active enemies on the board and returns those within the tower's attack radius.
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
     *
     * @return The distance in game units
     */
    public double getDistanceTo(Enemy enemy) {
	Point enemyPos = enemy.getPosition();
	return Math.sqrt(Math.pow(position.x - enemyPos.x, 2) + Math.pow(position.y - enemyPos.y, 2));
    }

    /**
     * Selects the closest enemy within range as a target.
     * <p>
     * Examines all provided enemies and returns the one closest to the tower that is still within attack range.
     *
     * @param enemies The list of candidate enemies to target
     *
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

    protected void performSplashAttack(Enemy target, double splashRadius, String towerType) {
	LogHandler.fine(getClass(), towerType + " tower performing area attack");

	// Get the main target's position for the splash center
	Point splashCenter = target.getPosition();

	// Find all enemies near the target and apply damage to them
	List<Enemy> enemies = findEnemiesInRange();
	int enemiesHit = 0;

	for (Enemy enemy : enemies) {
	    // Calculate distance from splash center
	    double distance = Math.sqrt(
		    Math.pow(splashCenter.x - enemy.getPosition().x, 2) +
		    Math.pow(splashCenter.y - enemy.getPosition().y, 2)
	    );

	    // Only affect enemies within splash radius
	    if (distance <= splashRadius) {
		enemy.takeDamage(getDamage());
		enemiesHit++;
	    }
	}

	LogHandler.fine(getClass(), towerType + " tower hit " + enemiesHit + " enemies with " + getDamage() + " damage");
    }


    /**
     * Plays a sound effect when the tower attacks.
     * <p>
     * Note: Resource paths in Java always use forward slashes (/) regardless of operating system, as they reference resources in the
     * classpath, not the file system. This is different from file system paths which would require File.separator.
     */
    private void playShootSound() {
	String resourcePath = "/audio/shot.wav";
	SoundManager.playSound(resourcePath, SOUND_VOLUME_REDUCTION);
    }

    public double getRange() {
	double baseRange = getType().getRange(level);
	return baseRange * rangeMultiplier;
    }

    public int getDamage() {
	int baseDamage = getType().getDamage(level);
	return (int) (baseDamage * damageMultiplier);
    }

    public double getFireRate() {
	double baseFireRate = getType().getFireRate(level);
	return baseFireRate * fireRateMultiplier;
    }
}