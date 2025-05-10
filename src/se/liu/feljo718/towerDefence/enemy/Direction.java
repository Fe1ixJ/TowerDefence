package se.liu.feljo718.towerdefence.enemy;

/**
 * Represents a visual explosion effect that appears when a tower hits an enemy.
 * <p>
 * This class models a temporary visual effect that appears at a specific position on the game board and disappears after a predefined
 * lifetime. Explosions are used to provide visual feedback when towers successfully hit enemies.
 * <p>
 * Each explosion has a position and a countdown lifetime that decreases each game tick until it reaches zero, at which point the explosion
 * is considered expired and should be removed from the game.
 *
 * @author feljo718
 * @see Enemy
 */
public enum Direction
{
    RIGHT(0, 1), DOWN(1, 0), LEFT(0, -1), UP(-1, 0);

    private final int xOffset;
    private final int yOffset;

    Direction(int xOffset, int yOffset) {
	this.xOffset = xOffset;
	this.yOffset = yOffset;
    }

    public int getXOffset() {
	return xOffset;
    }

    public int getYOffset() {
	return yOffset;
    }
}