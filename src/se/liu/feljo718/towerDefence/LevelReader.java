package se.liu.feljo718.towerDefence;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LevelReader {
    private static final String LEVELS_FILE = "levels.json";

    public List<Level> loadLevels() {
        List<Level> levels = new ArrayList<>();
        Gson gson = new Gson();
        Type levelType = new TypeToken<List<LevelData>>(){}.getType();
        Reader reader = null;

        try {
            // First try to load from classpath resources
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(LEVELS_FILE);

            // If not found in classpath, try to load from project resources directory
            if (inputStream == null) {
                File file = new File("resources" + File.separator + LEVELS_FILE);
                if (file.exists()) {
                    reader = new FileReader(file);
                } else {
                    System.err.println("Could not find levels.json file");
                    return createDefaultLevels();
                }
            } else {
                reader = new InputStreamReader(inputStream);
            }

            // Parse JSON into DTOs
            List<LevelData> levelDataList = gson.fromJson(reader, levelType);
            reader.close();

            // Convert DTOs to Level objects
            for (LevelData data : levelDataList) {
                levels.add(convertDataToLevel(data));
            }
        } catch (IOException e) {
            System.err.println("Error loading levels: " + e.getMessage());
            e.printStackTrace();
            return createDefaultLevels();
        }

        // If no levels were loaded, create default levels
        if (levels.isEmpty()) {
            return createDefaultLevels();
        }

        return levels;
    }

    private List<Level> createDefaultLevels() {
        System.out.println("Creating default levels as fallback");
        List<Level> levels = new ArrayList<>();
        Level backup = new Level("Backup Level", 1000);
        backup.addWave(new Wave(EnemyType.BASIC, 50, 100));
        backup.addWave(new Wave(EnemyType.TANK, 0, 2000));
        backup.addWave(new Wave(EnemyType.FAST, 0, 500));
        levels.add(backup);
        return levels;
    }

    private Level convertDataToLevel(LevelData data) {
        Level level = new Level(data.name, data.waveCooldown);
        for (WaveData waveData : data.waves) {
            EnemyType enemyType = EnemyType.valueOf(waveData.enemyType);
            level.addWave(new Wave(enemyType, waveData.count, waveData.spawnDelay));
        }
        return level;
    }

    // Data Transfer Objects for Gson deserialization
    // These classes are instantiated by Gson through reflection
    private static class LevelData {
        private String name = "";
        private int waveCooldown = 0;
        private List<WaveData> waves = new ArrayList<>();
    }

    private static class WaveData {
        private String enemyType = "";
        private int count = 0;
        private int spawnDelay = 0;
    }
}