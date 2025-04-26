package se.liu.feljo718.towerDefence;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Manages the creation and operation of defensive towers in the Tower Defense game.
 * <p>
 * This class handles the placement of towers on the game board, manages the collection
 * of active towers, and coordinates tower attacks against enemies. Each tower operates
 * independently based on its type, range, and attack patterns.
 *
 * @author feljo718
 * @see Tower
 * @see TowerType
 * @see Board
 */
public class TowerFactory {
    private final Board board;
    private final List<Tower> towers;
    private double gameTime;

    public TowerFactory(Board board) {
        this.board = board;
        this.towers = new ArrayList<>();
        this.gameTime = 0;
    }

    /**
     * Updates all towers for one game tick.
     * <p>
     * Increments the game time and checks each tower to see if it can attack.
     * Handles different attack patterns based on tower types - splash towers
     * affect multiple enemies, while other towers target individual enemies.
     */
    public void tick() {
        gameTime += 0.017;  // Simulate a frame time of 17ms (60 FPS)
        List<Enemy> enemies = board.getEnemyFactory().getEnemies();

        // Update each tower and handle powerups
        for (Tower tower : towers) {
            // Refresh powerups list to remove expired powerups
            tower.getActivePowerups();

            if (tower.canShoot(gameTime)) {
                if (tower.getType() == TowerType.SPLASH) {
                    if (!enemies.isEmpty()) {
                        // For splash towers, we just need any enemy in range to trigger the splash
                        Enemy target = tower.findTarget(enemies);
                        if (target != null) {
                            tower.shoot(target, gameTime);
                        }
                    }
                } else {
                    // For regular towers, find and shoot a single target
                    Enemy target = tower.findTarget(enemies);
                    if (target != null) {
                        tower.shoot(target, gameTime);
                    }
                }
            }
        }
    }

    /**
     * Attempts to place a new tower of the specified type at the given position.
     * <p>
     * Validates that the position is valid for tower placement before creating
     * the tower. A tower can only be placed on grass tiles.
     *
     * @param type     The type of tower to place
     * @param position The position on the board where the tower should be placed
     */
    public void placeTower(TowerType type, Point position) {
        if (canPlaceTower(position)) {
            Tower tower = new Tower(type, position);
            tower.setBoard(board);
            towers.add(tower);
        }
    }

    /**
     * Determines if a tower can be placed at the specified position.
     * <p>
     * Checks that the position is within the board boundaries and that
     * the tile at that position is a grass tile (valid for tower placement).
     *
     * @param position The position to check for tower placement
     * @return true if a tower can be placed at the position, false otherwise
     */
    private boolean canPlaceTower(Point position) {
        // Check if position is within bounds and on grass
        return position.x >= 0 && position.x < board.getWidth() &&
                position.y >= 0 && position.y < board.getHeight() &&
                board.getTileAt(position.y, position.x) == TileType.GRASS;
    }

    public List<Tower> getTowers() {
        return towers;
    }
}