package se.liu.feljo718.towerDefence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

/**
 * Manages the high scores for the Tower Defense game.
 *
 * Handles loading, saving, sorting, and displaying high scores.
 * Stores scores in a JSON file in the project's resources directory.
 */
public class HighscoreList {
    private final String filename;
    private List<Highscore> scores;

    public HighscoreList() {
        // Use the same approach as MapReader for file path consistency
        String resourcesPath = "resources" + File.separator + "highscore.json";
        File file = new File(resourcesPath);
        this.filename = file.getAbsolutePath();

        System.out.println("Highscore file path: " + this.filename);

        this.scores = new ArrayList<>();
        try {
            readFromJson();
        } catch (IOException e) {
            System.err.println("Error reading highscores: " + e.getMessage());
            this.scores = new ArrayList<>();
        }
    }

    /**
     * Adds a new highscore to the list, sorts the list, and saves to file.
     *
     * @param score The new highscore to add
     * @throws IOException If there's an error saving to the file
     */
    public void addScore(Highscore score) throws IOException {
        scores.add(score);
        // Sort scores in descending order
        scores.sort(Comparator.comparingInt(Highscore::getScore).reversed());
        saveToJson();
    }

    /**
     * Saves the current highscores to the JSON file.
     *
     * @throws IOException If there's an error writing to the file
     */
    private void saveToJson() throws IOException {
        // Create the resources directory if it doesn't exist
        File resourcesDir = new File(filename).getParentFile();
        if (!resourcesDir.exists()) {
            resourcesDir.mkdirs();
        }

        // Use a temporary file to avoid data corruption
        File tempFile = new File(filename + ".tmp");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(tempFile)) {
            gson.toJson(scores, writer);
        }

        // Only replace the actual file if the temporary write was successful
        File targetFile = new File(filename);
        System.out.println("Saving highscores to: " + targetFile.getAbsolutePath());
        Files.move(tempFile.toPath(), targetFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Reads highscores from the JSON file.
     *
     * @throws IOException If there's an error reading the file
     */
    public void readFromJson() throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Highscore file doesn't exist yet");
            scores = new ArrayList<>();
            return;
        }

        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            Gson gson = new Gson();
            Type highscoreListType = new TypeToken<List<Highscore>>(){}.getType();

            List<Highscore> loadedScores = gson.fromJson(content, highscoreListType);
            if (loadedScores == null) {
                scores = new ArrayList<>();
            } else {
                scores = loadedScores;
                // Sort scores in descending order
                scores.sort(Comparator.comparingInt(Highscore::getScore).reversed());
            }
        } catch (JsonSyntaxException e) {
            System.err.println("Error parsing highscore JSON: " + e.getMessage());
            scores = new ArrayList<>();
        }
    }

    /**
     * Returns a string representation of the highscores.
     */
    @Override
    public String toString() {
        if (scores.isEmpty()) {
            return "No highscores yet!";
        }

        final int maxPlayersShown = 10;
        StringBuilder sb = new StringBuilder();
        int numberOfPlayersShown = Math.min(maxPlayersShown, scores.size());

        for (int i = 0; i < numberOfPlayersShown; i++) {
            Highscore score = scores.get(i);
            sb.append(i + 1).append(". ")
                    .append(score.getName()).append(" - ")
                    .append(score.getScore()).append("\n");
        }

        return sb.toString();
    }
}