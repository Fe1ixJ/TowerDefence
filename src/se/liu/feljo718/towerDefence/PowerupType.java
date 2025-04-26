package se.liu.feljo718.towerDefence;

/**
 * Represents different types of tower powerups in the Tower Defense game.
 * <p>
 * Each powerup provides a temporary boost to tower attributes like damage,
 * fire rate, or range. Powerups have associated costs and duration.
 */
public enum PowerupType {
    DOUBLE_DAMAGE(0, "Double Damage", "Doubles tower damage for 30 seconds", 100),
    DOUBLE_FIRERATE(1, "Double Fire Rate", "Doubles tower fire rate for 30 seconds", 150),
    RANGE_BOOST(2, "Range Boost", "Increases tower range by 50% for 30 seconds", 125);

    private final int id;
    private final String name;
    private final String description;
    private final int cost;

    PowerupType(int id, String name, String description, int cost) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.cost = cost;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCost() {
        return cost;
    }
}