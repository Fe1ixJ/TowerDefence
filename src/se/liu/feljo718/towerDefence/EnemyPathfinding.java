package se.liu.feljo718.towerDefence;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EnemyPathfinding {
    private final Board board;
    private Point currentPos = null;
    private Point previousPos = null;
    private Point currentTarget = null;
    private boolean reachedEnd;

    public EnemyPathfinding(Board board) {
        this.board = board;
        this.reachedEnd = false;
        findStartPosition();
    }

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