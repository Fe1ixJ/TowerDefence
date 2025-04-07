package se.liu.feljo718.towerDefence;

public enum EnemyType {
    BASIC(0),
    FAST(1),
    TANK(2);

    private final int id;

    EnemyType(int id) {
        this.id = id;
    }
}
