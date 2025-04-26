package se.liu.feljo718.towerDefence;

/**
 * An interface for classes that need to respond to changes in the game board state.
 * <p>
 * This listener is part of the observer pattern implementation for the Tower Defense game.
 * Classes implementing this interface can register with the Board class to receive
 * notifications about state changes and game events.
 *
 * @author feljo718
 * @see Board
 */
public interface BoardListener {


    /**
     * Called when the state of the game board changes.
     * <p>
     * This method is invoked whenever a change occurs in the game state, including:
     * <ul>
     *   <li>Enemy movement or removal</li>
     *   <li>Tower placement or upgrade</li>
     *   <li>Player resource changes (lives, coins)</li>
     *   <li>Level or wave progression</li>
     * </ul>
     * Implementations should update their visual representation or internal state
     * to reflect the current board state.
     */
    void boardChanged();


    /**
     * Called when the game reaches a game over state.
     * <p>
     * This method is invoked when the player loses all lives.
     * Implementations should handle the game over state appropriately,
     * such as displaying a game over screen or stopping game processes.
     */
    void gameOver();
}
