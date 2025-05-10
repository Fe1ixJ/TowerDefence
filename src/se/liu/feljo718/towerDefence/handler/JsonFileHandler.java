package se.liu.feljo718.towerdefence.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Base class providing JSON file handling capabilities for game data.
 * <p>
 * This abstract class implements common functionality for loading from and saving to JSON files, used by various data loaders in the Tower
 * Defense game. It provides a consistent approach to file operations with built-in fallback mechanisms for finding files in different
 * locations (filesystem or classpath).
 * <p>
 * The class uses Google's Gson library for JSON serialization and deserialization, and includes safety features like atomic file writes to
 * prevent data corruption.
 *
 * @author feljo718
 * @see MapReader
 * @see LevelReader
 */
public abstract class JsonFileHandler
{
    protected final String filename;
    protected final Gson gson;

    /**
     * Creates a new JSON file handler for the specified file.
     * <p>
     * Initializes the handler with the given filename and creates a Gson instance configured with pretty printing for better readability.
     *
     * @param filename The name of the JSON file to handle
     */
    protected JsonFileHandler(String filename) {
	this.filename = "resources" + File.separator + filename;
	this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Attempts to create a reader for the JSON file.
     * <p>
     * This method implements a multi-stage search strategy to locate the specified JSON file:
     * <ol>
     *   <li>First looks in the resources directory on the filesystem</li>
     *   <li>Then attempts to load from the classpath resources</li>
     * </ol>
     * If the file cannot be found in either location, null is returned.
     *
     * @return A Reader for the JSON file, or null if not found
     * @throws IOException If an I/O error occurs while accessing the file
     */
    protected Reader getJsonReader() throws FileNotFoundException, IOException {
	File file = new File(filename);
	if (file.exists()) {
	    LogHandler.info(JsonFileHandler.class, "Loading from file: " + file.getAbsolutePath());
	    try {
		return new FileReader(file);
	    } catch (FileNotFoundException e) {
		LogHandler.severe(JsonFileHandler.class, "File exists but couldn't be opened: " + file.getAbsolutePath(), e);
		throw new IOException("File exists but couldn't be opened", e);
	    }
	}

	String resourceName = new File(filename).getName();
	// Warning is false, It uses try with resources look at LevelReader for example
	InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
	if (inputStream != null) {
	    LogHandler.info(JsonFileHandler.class, "Loading from classpath: " + filename);
	    // The caller is responsible for closing this reader which will also close the underlying InputStream
	    return new InputStreamReader(inputStream);
	}

	throw new FileNotFoundException("Could not find file or classpath resource: " + filename);
    }


    /**
     * Saves data to the JSON file.
     * <p>
     * This method implements an atomic write pattern to prevent data corruption:
     * <ol>
     *   <li>First creates the necessary directory structure if it doesn't exist</li>
     *   <li>Writes data to a temporary file</li>
     *   <li>Only after successful write, replaces the target file</li>
     * </ol>
     * This ensures that the target file is never left in a partially-written state.
     *
     * @param data The data object to serialize and save
     *
     * @throws IOException If an I/O error occurs during file creation or writing
     */
    protected void saveToJson(Object data) throws IOException {
	File resourcesDir = new File(filename).getParentFile();
	if (!resourcesDir.exists()) {
	    boolean dirCreated = resourcesDir.mkdirs();
	    if (!dirCreated) {
		String errorMsg = "Failed to create directory: " + resourcesDir.getAbsolutePath();
		LogHandler.severe(JsonFileHandler.class, errorMsg);
		throw new IOException(errorMsg);
	    }
	}

	File tempFile = new File(filename + ".tmp");

	try (FileWriter writer = new FileWriter(tempFile)) {
	    gson.toJson(data, writer);
	}

	File targetFile = new File(filename);
	LogHandler.info(JsonFileHandler.class, "Saving to: " + targetFile.getAbsolutePath());
	Files.move(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}