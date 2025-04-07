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

public class MapReader {
    private final String mapFile;
    private final TileType[][] map;
    private final int width;
    private final int height;

    public MapReader(String mapFile, int width, int height) {
        this.mapFile = mapFile;
        this.width = width;
        this.height = height;
        this.map = new TileType[height][width];
        loadMapFromJson();
    }

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