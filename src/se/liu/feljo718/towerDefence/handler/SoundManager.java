package se.liu.feljo718.towerdefence.handler;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for playing game sound effects.
 * <p>
 * This class provides static methods to load and play sound files from the game's resources. It handles audio loading, playback, volume
 * control, and resource cleanup.
 * <p>
 * Two playback methods are available:
 * <ul>
 *   <li>Standard playback - loads and plays a sound once</li>
 *   <li>Cached playback - loads sounds once and reuses them for better performance</li>
 * </ul>
 *
 * @author feljo718
 */
public class SoundManager
{
    /**
     * Plays a sound file from the specified resource path with adjusted volume.
     * <p>
     * This method loads the sound file each time it's called and automatically releases resources when playback completes.
     *
     * @param resourcePath    Path to the sound resource (e.g., "/audio/shot.wav")
     * @param volumeReduction Amount to reduce volume in decibels (negative values, e.g., -10.0f)
     */
    public static void playSound(String resourcePath, float volumeReduction) {
	try (InputStream in = SoundManager.class.getResourceAsStream(resourcePath)) {
	    if (in == null) {
		System.err.println("Warning: Could not find sound resource: " + resourcePath);
		return;
	    }

	    try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(in)) {
		Clip clip = AudioSystem.getClip();
		clip.open(audioIn);

		FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		volumeControl.setValue(volumeReduction);

		clip.start();

		clip.addLineListener(event -> {
		    if (clip.isRunning()) {
			return;
		    }
		    clip.close();
		});
	    }
	    /**No need to crash the game if audio doesn't work so just return*/
	} catch (UnsupportedAudioFileException e) {
	    LogHandler.severe(SoundManager.class, "Unsupported audio format: " + resourcePath, e);
	    return;
	} catch (IOException e) {
	    LogHandler.severe(SoundManager.class, "Error reading sound file: " + resourcePath, e);
	    return;
	} catch (LineUnavailableException e) {
	    LogHandler.severe(SoundManager.class, "Audio line unavailable: " + e.getMessage(), e);
	    return;
	}
    }
}