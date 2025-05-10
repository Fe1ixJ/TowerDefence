package se.liu.feljo718.towerdefence.tower;

import se.liu.feljo718.towerdefence.enemy.Enemy;

import java.awt.*;

/**
 * A splash tower implementation with area-of-effect damage.
 * <p>
 * The splash tower specializes in dealing moderate damage to multiple enemies
 * within a radius of the primary target. It has a faster attack rate but smaller
 * splash radius compared to the Bomber tower.
 *
 * @author feljo718
 */
public class SplashTower extends Tower
{
    private static final double SPLASH_RADIUS = 0.5;

    public SplashTower(Point position) {
	super(position);
    }

    @Override public TowerType getType() {
	return TowerType.SPLASH;
    }

    @Override public Color getColor() {
	return Color.MAGENTA;
    }

    /**
     * Performs an area-of-effect attack that damages multiple enemies around the target.
     * <p>
     * Damages all enemies within SPLASH_RADIUS of the target enemy.
     *
     * @param target The primary target that determines the center of the splash effect
     */
    @Override
    protected void performAttack(Enemy target) {
	performSplashAttack(target, SPLASH_RADIUS, "Splash");
    }
}