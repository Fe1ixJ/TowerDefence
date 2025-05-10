package se.liu.feljo718.towerdefence.handler;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.File;

/**
 * Centralized logging utility for the Tower Defense game.
 * <p>
 * This class provides a unified interface for logging across the application.
 * It configures both console and file logging, ensuring that messages are
 * properly formatted and persisted for later analysis.
 * <p>
 * The logger maintains a hierarchical structure based on class names and
 * supports different logging levels (SEVERE, WARNING, INFO, FINE, etc.)
 *
 * @author feljo718
 */
public class LogHandler
{
    private static final String LOG_FOLDER = "logs";
    private static final String LOG_FILE = "towerdefence.log";
    /** The maximum size of a log file before rotation */
    private static final int LOG_FILE_SIZE_LIMIT = 1000000;
    private static final int LOG_FILE_COUNT = 3;

    // Static initializer block runs once when the class is loaded
    static {
	initializeLogging();
    }

    /**
     * Initializes the logging system. This should be called once at application startup.
     */
    /**
     * Initializes the logging system.
     */
    private static void initializeLogging() {
	try {
	    // Create logs directory if it doesn't exist
	    File logDir = new File(LOG_FOLDER);
	    if (!logDir.exists()) {
		boolean dirCreated = logDir.mkdir();
		if (!dirCreated) {
		    System.err.println("Failed to create logs directory: " + LOG_FOLDER);
		    // Continuing despite failure - logs will attempt to use working directory
		}
	    }

	    // Configure the root logger
	    Logger rootLogger = Logger.getLogger("");

	    // Remove existing handlers
	    for (Handler handler : rootLogger.getHandlers()) {
		rootLogger.removeHandler(handler);
	    }

	    // Set global logging level
	    rootLogger.setLevel(Level.INFO);

	    // Console handler
	    ConsoleHandler consoleHandler = new ConsoleHandler();
	    consoleHandler.setLevel(Level.INFO);
	    rootLogger.addHandler(consoleHandler);

	    // File handler with rotation
	    FileHandler fileHandler = new FileHandler(LOG_FOLDER + File.separator + LOG_FILE,
						      LOG_FILE_SIZE_LIMIT, LOG_FILE_COUNT, true);
	    fileHandler.setFormatter(new SimpleFormatter());
	    fileHandler.setLevel(Level.ALL);
	    rootLogger.addHandler(fileHandler);
	} catch (IOException e) {
	    /** Logging has failed so cant log the error so just a print since I dont want to crash the program*/
	    System.err.println("Failed to initialize logging: " + e.getMessage());
	    e.printStackTrace();
	    return;
	}
    }

    /**
     * Gets a logger for the specified class.
     *
     * @param clazz The class requesting the logger
     * @return A configured Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
	return Logger.getLogger(clazz.getName());
    }

    /**
     * Log an INFO level message.
     *
     * @param clazz The class generating the log message
     * @param message The message to log
     */
    public static void info(Class<?> clazz, String message) {
	getLogger(clazz).info(message);
    }

    /**
     * Log a WARNING level message.
     *
     * @param clazz The class generating the log message
     * @param message The message to log
     */
    public static void logWarning(Class<?> clazz, String message) {
	getLogger(clazz).warning(message);
    }

    /**
     * Log a SEVERE level message.
     *
     * @param clazz The class generating the log message
     * @param message The message to log
     */
    public static void severe(Class<?> clazz, String message) {
	getLogger(clazz).severe(message);
    }

    /**
     * Log a SEVERE level message with exception details.
     *
     * @param clazz The class generating the log message
     * @param message The message to log
     * @param throwable The exception to include in the log
     */
    public static void severe(Class<?> clazz, String message, Throwable throwable) {
	getLogger(clazz).log(Level.SEVERE, message, throwable);
    }

    /**
     * Log a FINE level message.
     *
     * @param clazz The class generating the log message
     * @param message The message to log
     */
    public static void fine(Class<?> clazz, String message) {
	Logger logger = getLogger(clazz);
	if (logger.isLoggable(Level.FINE)) {
	    logger.fine(message);
	}
    }

    /**
     * Log a message at the specified level with exception details.
     *
     * @param clazz The class generating the log message
     * @param level The logging level
     * @param message The message to log
     * @param throwable The exception to include in the log
     */
    public static void log(Class<?> clazz, Level level, String message, Throwable throwable) {
	getLogger(clazz).log(level, message, throwable);
    }

    /**
     * Check if a given logging level is enabled for the specified class.
     *
     * @param clazz The class to check logging level for
     * @param level The logging level to check
     * @return true if the level is enabled
     */
    public static boolean isLoggable(Class<?> clazz, Level level) {
	return getLogger(clazz).isLoggable(level);
    }
}