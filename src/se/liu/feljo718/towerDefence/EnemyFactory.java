package se.liu.feljo718.towerDefence;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EnemyFactory {
    private final Board board;
    private final List<Enemy> enemies;

    public EnemyFactory(Board board) {
        this.board = board;
        this.enemies = new ArrayList<>();
    }

    public void spawnEnemy(EnemyType type) {
        Enemy enemy = new Enemy(type, board);
        enemies.add(enemy);
    }

    public void moveEnemies() {
        for (Enemy enemy : enemies) {
            enemy.move();
        }
    }

    public List<Point> getEnemyPositions() {
        List<Point> positions = new ArrayList<>();
        for (Enemy enemy : enemies) {
            positions.add(enemy.getPosition());
        }
        return positions;
    }

    public void removeFinishedEnemies() {
        for (Enemy enemy : new ArrayList<>(enemies)) {
            if (enemy.hasReachedEnd()) {
                enemies.remove(enemy);
            } else if (enemy.isDead()) {
                board.gainCoins(5); // How much coins to give for killing an enemy
                enemies.remove(enemy);
            }
        }

    }

    public List<Enemy> getEnemies() {
        return enemies;
    }
}