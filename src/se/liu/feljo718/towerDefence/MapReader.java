package se.liu.feljo718.towerDefence;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads and parses map layouts from JSON files for the Tower Defense game.
 * <p>
 * This class is responsible for reading map configurations, which define the arrangement
 * of tiles (grass, path, water, etc.) that form the game board. It supports loading maps
 * from external JSON files and provides fallback mechanisms for generating a default map
 * when files cannot be accessed.
 *
 * @author feljo718
 * @see Board
 * @see TileType
 */
public class MapReader {
    private final String mapFile;
    private final TileType[][] map;
    private final int width;
    private final int height;

    /**
     * Creates a new map reader for the specified map file and dimensions.
     * <p>
     * Initializes the map array and immediately attempts to load the map data
     * from the specified JSON file.
     *
     * @param mapFile The filename of the JSON map file to load
     * @param width   The width of the map in tiles
     * @param height  The height of the map in tiles
     */
    public MapReader(String mapFile, int width, int height) {
        this.mapFile = mapFile;
        this.width = width;
        this.height = height;
        this.map = new TileType[height][width];
        loadMapFromJson();
    }

    /**
     * Loads map data from the JSON file.
     * <p>
     * Attempts to read the map file from the following locations in order:
     * <ol>
     *   <li>The local resources directory</li>
     *   <li>The application's classpath resources</li>
     * </ol>
     * Falls back to generating a default map if the file cannot be found or read.
     */
    private void loadMapFromJson() {
        Gson gson = new Gson();
        Reader reader = null;

        try {
            // First try to load from project resources directory
            File file = new File("resources" + File.separator + mapFile);
            if (file.exists()) {
                System.out.println("Loading map from file: " + file.getAbsolutePath());
                reader = new FileReader(file);
            } else {
                // Try to load from classpath resources
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream(mapFile);
                if (inputStream != null) {
                    System.out.println("Loading map from classpath: " + mapFile);
                    reader = new InputStreamReader(inputStream);
                } else {
                    System.err.println("Could not find " + mapFile + " file, generating default map");
                    generateDefaultMap();
                    return;
                }
            }

            // Parse JSON
            JsonObject mapJson = gson.fromJson(reader, JsonObject.class);
            reader.close();
            parseMapJson(mapJson);
        } catch (IOException e) {
            System.err.println("Error loading map: " + e.getMessage());
            e.printStackTrace();
            generateDefaultMap();
        }
    }

    /**
     * Parses the JSON map data into the tile grid.
     * <p>
     * Converts string representations in the JSON file to TileType enum values.
     * The expected format uses single character codes to represent different tile types:
     * <ul>
     *   <li>G - Grass tile</li>
     *   <li>P - Path tile</li>
     *   <li>W - Water tile</li>
     *   <li>S - Start tile</li>
     *   <li>E - End tile</li>
     * </ul>
     *
     * @param mapJson The parsed JSON object containing the map data
     */
    private void parseMapJson(JsonObject mapJson) {
        Map<String, TileType> tileMap = new HashMap<>();
        tileMap.put("G", TileType.GRASS);
        tileMap.put("P", TileType.PATH);
        tileMap.put("W", TileType.WATER);
        tileMap.put("S", TileType.START);
        tileMap.put("E", TileType.END);

        JsonArray rows = mapJson.getAsJsonArray("tiles");

        // Initialize map with grass (default)
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                map[row][col] = TileType.GRASS;
            }
        }

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
     * Creates a simple default map when the JSON file cannot be loaded.
     * <p>
     * Generates a map with a straight horizontal path in the middle row,
     * with start and end points at opposite ends of this path. All other
     * tiles are set to grass.
     */
    private void generateDefaultMap() {
        // Fill entire map with grass
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                map[row][col] = TileType.GRASS;
            }
        }

        // Create straight path in the middle
        int middleRow = map.length / 2;
        for (int col = 0; col < map[0].length; col++) {
            map[middleRow][col] = TileType.PATH;
        }
        map[middleRow][0] = TileType.START;
        map[middleRow][map[0].length - 1] = TileType.END;
    }

    public TileType[][] getMap() {
        return map;
    }
}