package se.liu.feljo718.towerdefence.tower;

import se.liu.feljo718.towerdefence.enemy.Enemy;
import se.liu.feljo718.towerdefence.handler.LogHandler;

import java.awt.*;

/**
 * A tower that deals high splash damage with a low fire rate.
 * <p>
 * The bomber tower is specialized for area-of-effect damage, dealing high damage
 * to multiple enemies at once with explosive attacks.
 *
 * @author feljo718
 */
public class BomberTower extends Tower
{
    private static final int SPLASH_RADIUS = 1;

    public BomberTower(Point position) {
	super(position);
	LogHandler.info(BomberTower.class, "Bomber tower created at position: " + position);
    }

    @Override public TowerType getType() {
	return TowerType.BOMBER;
    }

    @Override public Color getColor() {
	return new Color(139, 69, 19);
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
	performSplashAttack(target, SPLASH_RADIUS, "Bomber");
    }
}