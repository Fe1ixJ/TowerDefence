package se.liu.feljo718.towerdefence.handler;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import se.liu.feljo718.towerdefence.board.Level;
import se.liu.feljo718.towerdefence.enemy.EnemyType;
import se.liu.feljo718.towerdefence.enemy.Wave;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reads and parses level data from JSON files for the Tower Defense game.
 * <p>
 * This class is responsible for loading level configurations from JSON files, converting the data into Level objects that can be used by
 * the game. It handles file reading, JSON parsing, and provides fallback default levels in case the loading process fails.
 * <p>
 * Each level consists of multiple waves of enemies with different types, counts, and spawn timings, which are all managed by this reader.
 *
 * @author feljo718
 * @see Level
 * @see JsonFileHandler
 * @see Wave
 */
public class LevelReader extends JsonFileHandler
{
    private static final String DEFAULT_LEVEL_FILE = "levels.json";
    private final String fileName;

    /**
     * Creates a new level reader that loads level data from the levels.json file.
     * <p>
     * Uses the default level file name defined in the class constants.
     */
    public LevelReader() {
	super(DEFAULT_LEVEL_FILE);
	this.fileName = DEFAULT_LEVEL_FILE;
    }

    /**
     * Creates a new level reader that loads level data from the specified file.
     * <p>
     * Allows custom level files to be used instead of the default.
     *
     * @param levelFile The JSON file containing level definitions
     */
    public LevelReader(String levelFile) {
	super(levelFile);
	this.fileName = levelFile;
    }

    /**
     * Loads level configurations from the configured JSON file.
     * <p>
     * This method attempts to load and parse the level data from the JSON file. If the loading process fails, it returns a set of default
     * levels as a fallback.
     *
     * @return A list of configured levels for the game
     */
    public List<Level> getLevels() {
	try {
	    return loadFromJson();
	} catch (IOException e) {
	    LogHandler.severe(LevelReader.class, "Error loading levels: " + e.getMessage());
	    return createDefaultLevels();
	}
    }

    /**
     * Loads and parses level data from JSON file.
     *
     * @return A list of Level objects based on the JSON data
     * @throws FileNotFoundException If the levels file cannot be found
     * @throws JsonSyntaxException   If the JSON is malformed
     */
    public List<Level> loadFromJson() throws FileNotFoundException, JsonSyntaxException {
	try (Reader reader = getJsonReader()) {
	    if (reader == null) {
		return createDefaultLevels();
	    }

	    // Parse and convert the levels
	    List<LevelData> levelData = parseJsonData(reader);
	    List<Level> levels = new ArrayList<>();

	    // Convert each LevelData to a Level
	    for (LevelData data : levelData) {
		levels.add(createLevelFromData(data));
	    }

	    // Use defaults if no levels were loaded
	    if (levels.isEmpty()) {
		return createDefaultLevels();
	    }

	    return levels;
	} catch (FileNotFoundException | JsonSyntaxException e) {
	    LogHandler.severe(LevelReader.class, "Failed to load level file '" + fileName + "': " + e.getMessage());
	    // Rethrow specific exceptions we've declared
	    throw e;
	} catch (IOException e) {
	    // Handle other IO exceptions internally
	    LogHandler.logWarning(LevelReader.class, "Encountered IO exception: " + e.getMessage());
	    return createDefaultLevels();
	}
    }

    /**
     * Parses the JSON data from the reader into LevelData objects.
     * <p>
     * Uses Gson to convert the JSON into a list of transfer objects that represent the raw level configuration data.
     *
     * @param reader The reader providing the JSON content
     *
     * @return A list of parsed LevelData objects
     */
    private List<LevelData> parseJsonData(Reader reader) {
	Type levelType = new TypeToken<List<LevelData>>()
	{
	}.getType();
	List<LevelData> levelData = gson.fromJson(reader, levelType);
	return levelData != null ? levelData : new ArrayList<>();
    }


    /**
     * Creates a set of default levels as a fallback when configuration is unavailable.
     * <p>
     * Generates a predefined set of levels with varying difficulty to ensure the game can still function even when level data cannot be
     * loaded.
     *
     * @return A list of default Level objects
     */
    private Level createLevelFromData(LevelData data) {
	Level level = new Level(data.waveCooldown);

	for (WaveData waveData : data.waves) {
	    EnemyType enemyType = getValidEnemyType(waveData.enemyType);
	    level.addWave(new Wave(enemyType, waveData.count, waveData.spawnDelay));
	}

	return level;
    }

    private List<Level> createDefaultLevels() {
	LogHandler.info(LevelReader.class, "Creating default levels as fallback");
	List<Level> levels = new ArrayList<>();

	Level level = new Level(1500);
	level.addWave(new Wave(EnemyType.BASIC, 10, 200));
	level.addWave(new Wave(EnemyType.FAST, 5, 300));
	levels.add(level);

	return levels;
    }

    /**
     * Validates an enemy type string and returns the corresponding enum value or a default.
     *
     * @param typeString The enemy type string to validate
     *
     * @return The matching EnemyType or BASIC as fallback
     */
    private EnemyType getValidEnemyType(String typeString) {
	if (typeString == null || typeString.isEmpty() || !isValidEnemyType(typeString)) {
	    LogHandler.logWarning(LevelReader.class, "Invalid enemy type: " + typeString);
	    return EnemyType.BASIC;
	}
	return EnemyType.valueOf(typeString);
    }

    private boolean isValidEnemyType(String typeString) {
	for (EnemyType type : EnemyType.values()) {
	    if (type.name().equals(typeString)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Level data transfer object from JSON.
     */
    private static class LevelData
    {
	private int waveCooldown = 0;
	private List<WaveData> waves = Collections.emptyList();
    }

    /**
     * Data transfer object representing a wave configuration from JSON. Instantiated by GSON during deserialization.
     */
    private static class WaveData
    {
	private String enemyType = "";
	private int count = 0;
	private int spawnDelay = 0;
    }
}