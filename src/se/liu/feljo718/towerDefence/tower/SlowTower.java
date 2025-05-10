package se.liu.feljo718.towerdefence.tower;

import se.liu.feljo718.towerdefence.enemy.Enemy;
import se.liu.feljo718.towerdefence.handler.LogHandler;

import java.awt.*;

/**
 * A tower that slows down enemies while dealing light damage.
 * <p>
 * The slow tower specializes in hindering enemy movement, making other towers
 * more effective by giving them more time to attack.
 *
 * @author feljo718
 */
public class SlowTower extends Tower
{
    private static final double SLOW_FACTOR = 0.75;
    private static final int SLOW_DURATION = 2000;

    public SlowTower(Point position) {
	super(position);
	LogHandler.info(SlowTower.class, "Slow tower created at position: " + position);
    }

    @Override public TowerType getType() {
	return TowerType.SLOW;
    }

    @Override public Color getColor() {
	return new Color(0, 191, 255);
    }

    @Override protected void performAttack(Enemy target) {
	// Apply both damage and slowing effect
	target.takeDamage(getDamage());
	target.applySpeedModifier(SLOW_FACTOR, SLOW_DURATION);
	LogHandler.fine(SlowTower.class,
			"Slow tower applied " + getDamage() + " damage and reduced speed to " +
			(SLOW_FACTOR * 100) + "% for " + (SLOW_DURATION / 1000) + " seconds");
    }
}