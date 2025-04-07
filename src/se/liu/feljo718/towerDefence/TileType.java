package se.liu.feljo718.towerDefence;

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
