package se.liu.feljo718.towerDefence;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class TowerFactory {
    private final Board board;
    private final List<Tower> towers;
    private double gameTime;

    public TowerFactory(Board board) {
        this.board = board;
        this.towers = new ArrayList<>();
        this.gameTime = 0;
    }

    public void tick() {
        gameTime += 0.017;  // Simulate a frame time of 17ms (60 FPS)
        List<Enemy> enemies = board.getEnemyFactory().getEnemies();
        for (Tower tower : towers) {
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

    public void placeTower(TowerType type, Point position) {
        if (canPlaceTower(position)) {
            Tower tower = new Tower(type, position);
            tower.setBoard(board);
            towers.add(tower);
        }
    }

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