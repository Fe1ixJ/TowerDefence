package se.liu.feljo718.towerdefence.powerup;

/**
 * Represents different types of tower powerups in the Tower Defense game.
 * <p>
 * Each powerup provides a temporary boost to tower attributes like damage, fire rate, or range. Powerups have associated costs and
 * duration.
 */
public enum PowerupType
{
    DOUBLE_DAMAGE("Double Damage", "Doubles tower damage for 30 seconds", 100), DOUBLE_FIRERATE("Double Fire Rate",
												"Doubles tower fire rate for 30 seconds",
												150), RANGE_BOOST("Range Boost",
														  "Increases tower range by 50% for 30 seconds",
														  125);

    private final String name;
    private final String description;
    private final int cost;

    PowerupType(String name, String description, int cost) {
	this.name = name;
	this.description = description;
	this.cost = cost;
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