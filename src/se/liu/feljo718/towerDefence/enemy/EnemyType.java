package se.liu.feljo718.towerdefence.enemy;

/**
 * Represents different types of enemies in the Tower Defense game.
 * <p>
 * This enum defines the various enemy classifications, each with unique attributes affecting their behavior, appearance, and difficulty.
 * Each enemy type has an associated ID for internal reference.
 * <p>
 * The available enemy types are:
 * <ul>
 *   <li>BASIC - Standard enemy with balanced attributes</li>
 *   <li>FAST - Quicker enemy that moves more rapidly along the path</li>
 *   <li>TANK - High-health enemy that can absorb more damage</li>
 * </ul>
 *
 * @author feljo718
 * @see Enemy
 */
public enum EnemyType
{
    BASIC, FAST, TANK, SLOW, BOSS,
}
