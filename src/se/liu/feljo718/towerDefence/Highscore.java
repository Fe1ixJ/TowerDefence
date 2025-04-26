package se.liu.feljo718.towerDefence;

// Class representing a highscore entry in the game
public class Highscore
{
    // Name of the player who achieved the highscore
    private String name;
    private int score;

    // Constructor for creating a new highscore entry
    public Highscore(String name, int score) {
        this.name = name;
        this.score = score;
    }

    // Getters for the name and score of the highscore entry
    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public String toString() {
        return String.format("%s - %d", name, score);
    }
}
