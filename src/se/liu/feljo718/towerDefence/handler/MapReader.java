package se.liu.feljo718.towerdefence.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import se.liu.feljo718.towerdefence.board.TileType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Loads and parses map layouts from JSON files for the Tower Defense game.
 * <p>
 * This class is responsible for reading map data from a JSON configuration file and converting it into a grid of tile types that can be
 * used by the game board. It handles file reading, JSON parsing, and mapping string representations to actual tile type enumerations.
 * <p>
 * If the specified map file cannot be found or read, a default map layout will be generated automatically as a fallback.
 *
 * @author feljo718
 * @see JsonFileHandler
 * @see TileType
 */
public class MapReader extends JsonFileHandler
{
    private static final String GRASS_KEY = "G";
    private static final String PATH_KEY = "P";
    private static final String WATER_KEY = "W";
    private static final String START_KEY = "B";
    private static final String END_KEY = "E";
    private static final String SAND_KEY = "S";


    private final TileType[][] map;
    private final int width;
    private final int height;
    private final String mapName;

    /**
     * Creates a new map reader for the specified map file and dimensions.
     * <p>
     * Attempts to load the map immediately upon construction. If the load fails, a default map will be generated instead.
     *
     * @param mapFile The filename of the JSON map file to load
     * @param width   The width of the map in tiles
     * @param height  The height of the map in tiles
     *
     * @throws IllegalArgumentException if width or height are less than 1
     */
    public MapReader(String mapFile, int width, int height, String mapName) {
	super(mapFile);
	this.width = width;
	this.height = height;
	this.mapName = mapName != null ? mapName : "Default Map";
	this.map = new TileType[height][width];

	initializeMap();
    }

    /**
     * Creates a new map reader with default map name.
     */
    public MapReader(String mapFile, int width, int height) {
	this(mapFile, width, height, "Default Map");
    }

    private void initializeMap() {
	try {
	    loadFromJson();
	} catch (FileNotFoundException | JsonSyntaxException e) {
	    LogHandler.severe(MapReader.class, "Error loading map: " + e.getMessage(), e);
	    generateDefaultMap();
	    return;
	}
    }

    /**
     * Loads map data from the JSON file.
     * <p>
     * Reads and parses the JSON map file, converting its contents into a grid of tile types. If the operation fails, a default map is
     * generated.
     *
     * @return The loaded tile grid
     * @throws FileNotFoundException If the map file cannot be found
     * @throws JsonSyntaxException   If the JSON is malformed
     */
    public TileType[][] loadFromJson() throws FileNotFoundException, JsonSyntaxException {
	Reader reader;
	try {
	    reader = getJsonReader();
	} catch (FileNotFoundException e) {
	    LogHandler.severe(MapReader.class, "Map file not found: " + e.getMessage(), e);
	    throw e; // Rethrow to match your signature
	} catch (IOException e) {
	    LogHandler.logWarning(MapReader.class, "Could not read map file: " + e.getMessage());
	    generateDefaultMap();
	    return map;
	}

	if (reader == null) {
	    LogHandler.logWarning(MapReader.class, "Could not find map file, generating default map");
	    generateDefaultMap();
	    return map;
	}

	try (Reader r = reader) {
	    // Rest of the method remains unchanged
	    JsonArray maps = gson.fromJson(r, JsonArray.class);
	    boolean mapFound = false;

	    // Search for the requested map by name
	    for (int i = 0; i < maps.size(); i++) {
		JsonObject mapData = maps.get(i).getAsJsonObject();
		String currentMapName = mapData.get("name").getAsString();

		if (currentMapName.equals(mapName)) {
		    parseMapData(mapData);
		    mapFound = true;
		    LogHandler.info(MapReader.class, "Loaded map: " + mapName);
		    break;
		}
	    }

	    if (!mapFound) {
		LogHandler.logWarning(MapReader.class, "Map '" + mapName + "' not found, loading default map");
		// Try to load the first map in the array
		if (!maps.isEmpty()) {
		    parseMapData(maps.get(0).getAsJsonObject());
		} else {
		    generateDefaultMap();
		}
	    }
	} catch (IOException e) {
	    LogHandler.log(MapReader.class, Level.WARNING, "Error reading map file: " + e.getMessage(), e);
	    generateDefaultMap();
	    return map;
	}
	return map;
    }

    /**
     * Parses the JSON map data into the tile grid.
     * <p>
     * Converts string representations in the JSON into actual TileType values and populates the map grid.
     *
     * @param mapJson The parsed JSON object containing the map data
     */
    private void parseMapData(JsonObject mapData) {
	Map<String, TileType> tileMap = createTileTypeMap();
	JsonArray rows = mapData.getAsJsonArray("tiles");

	// Initialize map with grass (default)
	fillMapWithTile(TileType.GRASS);

	// Parse tile data from JSON
	int rowIndex = 0;
	for (int i = 0; i < rows.size() && rowIndex < height; i++) {
	    String rowStr = rows.get(i).getAsString();
	    for (int col = 0; col < Math.min(rowStr.length(), width); col++) {
		String tileKey = String.valueOf(rowStr.charAt(col));
		TileType tileType = tileMap.getOrDefault(tileKey, TileType.GRASS);
		map[rowIndex][col] = tileType;
	    }
	    rowIndex++;
	}
    }

    /**
     * Creates the mapping between string keys and tile types.
     */
    private Map<String, TileType> createTileTypeMap() {
	Map<String, TileType> tileMap = new HashMap<>();
	tileMap.put(GRASS_KEY, TileType.GRASS);
	tileMap.put(PATH_KEY, TileType.PATH);
	tileMap.put(WATER_KEY, TileType.WATER);
	tileMap.put(START_KEY, TileType.START);
	tileMap.put(END_KEY, TileType.END);
	tileMap.put(SAND_KEY, TileType.SAND);
	return tileMap;
    }

    /**
     * Fills the entire map with a single tile type.
     */
    private void fillMapWithTile(TileType tileType) {
	for (int row = 0; row < height; row++) {
	    for (int col = 0; col < width; col++) {
		map[row][col] = tileType;
	    }
	}
    }

    /**
     * Creates a simple default map when the JSON file cannot be loaded.
     */
    private void generateDefaultMap() {
	// Fill entire map with grass
	fillMapWithTile(TileType.GRASS);

	// Create straight path in the middle
	int middleRow = height / 2;
	for (int col = 0; col < width; col++) {
	    map[middleRow][col] = TileType.PATH;
	}
	map[middleRow][0] = TileType.START;
	map[middleRow][width - 1] = TileType.END;
    }

    public TileType[][] getMap() {
	return map;
    }
}