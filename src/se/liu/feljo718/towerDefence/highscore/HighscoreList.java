package se.liu.feljo718.towerdefence.highscore;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import se.liu.feljo718.towerdefence.handler.JsonFileHandler;
import se.liu.feljo718.towerdefence.handler.LogHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

/**
 * Manages and persists player highscores for the Tower Defense game.
 * <p>
 * This class is responsible for storing, loading, and sorting player highscores. It extends JsonFileHandler to handle persistence of score
 * data to a JSON file. The class automatically loads any existing highscores on instantiation and provides methods to add new scores and
 * display the top performers.
 * <p>
 * Scores are automatically sorted in descending order (highest score first), and a configurable maximum number of scores can be displayed
 * in the string representation.
 *
 * @author feljo718
 * @see JsonFileHandler
 * @see Highscore
 */
public class HighscoreList extends JsonFileHandler
{
    private static final int MAX_PLAYERS_SHOWN = 10;
    private List<Highscore> scores;

    /**
     * Creates a new highscore list and loads existing scores from persistent storage.
     * <p>
     * Initializes the list with scores loaded from the highscore.json file, if it exists. If the file cannot be read or contains invalid
     * data, an empty list will be used.
     */
    public HighscoreList() {
	super("highscore.json");
	this.scores = initializeScores();
    }

    /**
     * Initializes the scores list by loading from JSON file or creating an empty list on error.
     *
     * @return The loaded scores list or an empty list if loading fails
     */
    private List<Highscore> initializeScores() {
	try {
	    return loadFromJson();
	} catch (FileNotFoundException | JsonSyntaxException e) {
	    LogHandler.log(HighscoreList.class, Level.WARNING, "Error reading highscores", e);
	    return new ArrayList<>();
	}
    }

    /**
     * Adds a new score to the highscore list and persists the updated list.
     * <p>
     * The new score is added to the list, which is then sorted in descending order by score value. The updated list is immediately saved to
     * the JSON file.
     *
     * @param score The highscore to add
     *
     * @throws IOException              If an error occurs when saving the updated list to file
     * @throws IllegalArgumentException If the score parameter is null
     */
    public void addScore(Highscore score) throws IOException {
	scores.add(score);
	scores.sort(Comparator.comparingInt(Highscore::getScore).reversed());
	saveToJson(scores);
    }

    /**
     * Loads the highscore list from the JSON file.
     * <p>
     * Reads and parses the highscore data from the configured JSON file. If the file doesn't exist or contains invalid data, an empty list
     * is returned. The loaded scores are automatically sorted in descending order.
     *
     * @return A list of highscores sorted by score (highest first)
     * @throws FileNotFoundException If the highscore file cannot be found
     * @throws JsonSyntaxException   If the JSON is malformed
     */
    public List<Highscore> loadFromJson() throws FileNotFoundException, JsonSyntaxException {
	try (Reader reader = getJsonReader()) {
	    if (reader == null) {
		return new ArrayList<>();
	    }

	    Type highscoreListType = new TypeToken<List<Highscore>>()
	    {
	    }.getType();
	    List<Highscore> loadedScores = gson.fromJson(reader, highscoreListType);

	    if (loadedScores == null) {
		return new ArrayList<>();
	    }

	    loadedScores.sort(Comparator.comparingInt(Highscore::getScore).reversed());
	    return loadedScores;
	} catch (FileNotFoundException e) {
	    LogHandler.log(HighscoreList.class, Level.WARNING, "Highscore file not found", e);
	    throw e;
	} catch (JsonSyntaxException e) {
	    LogHandler.log(HighscoreList.class, Level.WARNING, "Error parsing highscore JSON", e);
	    throw e;
	} catch (IOException e) {
	    LogHandler.log(HighscoreList.class, Level.WARNING, "Error reading highscore file", e);
	    return new ArrayList<>();
	}
    }

    @Override public String toString() {
	if (scores.isEmpty()) {
	    return "No highscores yet!";
	}

	StringBuilder sb = new StringBuilder("Top Highscores:\n");
	int numberOfPlayersShown = Math.min(MAX_PLAYERS_SHOWN, scores.size());

	for (int i = 0; i < numberOfPlayersShown; i++) {
	    Highscore score = scores.get(i);
	    sb.append(i + 1).append(". ").append(score.getName()).append(" - ").append(score.getScore()).append("\n");
	}

	return sb.toString();
    }
}