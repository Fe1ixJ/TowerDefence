package se.liu.feljo718.towerdefence.highscore;

/**
 * Represents a single highscore entry in the Tower Defense game.
 * <p>
 * This class stores information about a player's achievement, including their name and score value. Highscore objects are immutable once
 * created and are used by the HighscoreList class to track and display player performances.
 *
 * @author feljo718
 * @see HighscoreList
 */
public class Highscore
{
    private final String name;
    private final int score;

    public Highscore(String name, int score) {
	this.name = name;
	this.score = score;
    }

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
