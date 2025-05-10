package se.liu.feljo718.towerdefence.tower;

import se.liu.feljo718.towerdefence.enemy.Enemy;

import java.awt.*;

/**
 * A sniper tower implementation with high single-target damage.
 * <p>
 * The sniper tower specializes in dealing high damage to individual targets.
 */
public class SniperTower extends Tower
{
    public SniperTower(Point position) {
	super(position);
    }

    @Override public TowerType getType() {
	return TowerType.SNIPER;
    }

    @Override public Color getColor() {
	return Color.CYAN;
    }

    @Override protected void performAttack(Enemy target) {
	// Sniper tower does high single-target damage
	target.takeDamage(getDamage());
    }
}