package se.liu.feljo718.towerDefence;

/**
 * Represents the types of tiles used in the Tower Defense game board.
 * <p>
 * Each tile type has a unique ID and represents a specific element in the game map:
 * <ul>
 *   <li>GRASS - Basic terrain where towers can be built</li>
 *   <li>PATH - The route that enemies travel along</li>
 *   <li>WATER - Impassable terrain that cannot be built upon</li>
 *   <li>START - The point where enemies spawn into the map</li>
 *   <li>END - The destination that enemies try to reach</li>
 *   <li>INTERFACE - Tiles used for UI elements on the game board</li>
 * </ul>
 *
 * @author feljo718
 * @see Board
 * @see MapReader
 */
public enum TileType {
    GRASS(0),
    PATH(1),
    WATER(2),
    START(3),
    END(4),
    INTERFACE(5);

    private final int id;

    TileType(int id) {
        this.id = id;
    }
}
