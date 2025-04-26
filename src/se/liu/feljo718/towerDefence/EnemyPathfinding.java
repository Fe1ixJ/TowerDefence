package se.liu.feljo718.towerDefence;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the movement logic for enemies along the game path.
 * <p>
 * This class implements pathfinding for enemies, tracking their positions as they
 * navigate from the start tile to the end tile. It determines valid moves,
 * maintains position information, and detects when enemies reach the end of the path.
 *
 * @author feljo718
 * @see Enemy
 * @see Board
 * @see TileType
 */
public class EnemyPathfinding {
    private final Board board;
    private Point currentPos = null;
    private Point previousPos = null;
    private Point currentTarget = null;
    private boolean reachedEnd;

    /**
     * Creates a new pathfinding instance for navigating the specified game board.
     * <p>
     * Locates the start position on the board and initializes the enemy position.
     *
     * @param board The game board containing the path to navigate
     */
    public EnemyPathfinding(Board board) {
        this.board = board;
        this.reachedEnd = false;
        findStartPosition();
    }

    /**
     * Searches the board for the start tile and sets initial positions.
     * <p>
     * Scans the entire board to locate the tile marked as START and sets both
     * current and previous positions to that location.
     */
    private void findStartPosition() {
        for (int row = 0; row < board.getHeight(); row++) {
            for (int col = 0; col < board.getWidth(); col++) {
                if (board.getTileAt(row, col) == TileType.START) {
                    currentPos = new Point(col, row);
                    previousPos = new Point(col, row);
                    return;
                }
            }
        }
    }

    /**
     * Determines the next target position along the path.
     * <p>
     * If the enemy has already reached the end, returns null.
     * If no current target exists, calculates a new target position
     * based on available moves.
     *
     * @return The next Point the enemy should move toward, or null if path is complete
     */
    public Point getNextPosition() {
        if (reachedEnd) {
            return null;
        }

        if (currentTarget == null) {
            List<Point> possibleMoves = getPossibleMoves();
            if (!possibleMoves.isEmpty()) {
                currentTarget = possibleMoves.getFirst();
            }
        }
        return currentTarget;
    }

    /**
     * Identifies all valid adjacent tiles where the enemy can move.
     * <p>
     * Checks the four adjacent tiles (up, down, left, right) to determine
     * if they are valid path segments. Excludes the previous position to
     * prevent backtracking.
     *
     * @return A list of valid Point positions where the enemy can move next
     */
    //FIXA TILL ENUM
    private List<Point> getPossibleMoves() {
        List<Point> possibleMoves = new ArrayList<>();
        int[][] directions = {
                {0, 1},  // right
                {1, 0},  // down
                {0, -1}, // left
                {-1, 0}  // up
        };

        for (int[] dir : directions) {
            int newX = currentPos.x + dir[0];
            int newY = currentPos.y + dir[1];

            if (isValidMove(newX, newY)) {
                Point newPos = new Point(newX, newY);
                if (!newPos.equals(previousPos)) {
                    possibleMoves.add(newPos);
                }
            }
        }
        return possibleMoves;
    }

    /**
     * Updates position tracking after reaching the current target point.
     * <p>
     * Moves the current position to the target position and clears the target.
     * If the position is the end tile, marks the path as complete and notifies
     * the board that a life is lost.
     */
    public void reachedTargetPoint() {
        previousPos = currentPos;
        currentPos = currentTarget;
        currentTarget = null;

        if (board.getTileAt(currentPos.y, currentPos.x) == TileType.END) {
            reachedEnd = true;
            board.livesLost();
            System.out.println("Enemy reached the end!");
        }
    }

    /**
     * Determines if a potential move is valid within the game rules.
     * <p>
     * A move is valid if:
     * <ul>
     *   <li>The coordinates are within board boundaries</li>
     *   <li>The tile at those coordinates is a path or end tile</li>
     * </ul>
     *
     * @param x The x-coordinate to check
     * @param y The y-coordinate to check
     * @return {@code true} if the move is valid; {@code false} otherwise
     */
    private boolean isValidMove(int x, int y) {
        if (x < 0 || x >= board.getWidth() || y < 0 || y >= board.getHeight()) {
            return false;
        }
        TileType tileType = board.getTileAt(y, x);
        return tileType == TileType.PATH || tileType == TileType.END;
    }

    public Point getCurrentPos() {
        return currentPos;
    }

    public boolean hasReachedEnd() {
        return reachedEnd;
    }
}