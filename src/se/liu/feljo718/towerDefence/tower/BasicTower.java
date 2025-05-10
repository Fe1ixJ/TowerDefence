package se.liu.feljo718.towerdefence.tower;

import se.liu.feljo718.towerdefence.enemy.Enemy;

import java.awt.*;

/**
 * A basic tower implementation with simple single-target attacks.
 * <p>
 * The basic tower is an entry-level defense option with balanced stats.
 */
public class BasicTower extends Tower
{
    public BasicTower(Point position) {
	super(position);
    }

    @Override public TowerType getType() {
	return TowerType.BASIC;
    }

    @Override public Color getColor() {
	return Color.BLUE;
    }

    @Override protected void performAttack(Enemy target) {
	// Basic tower has simple single-target attack
	target.takeDamage(getDamage());
    }
}