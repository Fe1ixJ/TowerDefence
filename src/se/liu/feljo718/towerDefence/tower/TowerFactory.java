package se.liu.feljo718.towerdefence.tower;

import se.liu.feljo718.towerdefence.board.Board;
import se.liu.feljo718.towerdefence.enemy.Enemy;
import se.liu.feljo718.towerdefence.handler.LogHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the creation and operation of defensive towers in the Tower Defense game.
 * <p>
 * This class handles the placement of towers on the game board, manages the collection of active towers, and coordinates tower attacks
 * against enemies. Each tower operates independently based on its type, range, and attack patterns.
 *
 * @author feljo718
 * @see Tower
 * @see TowerType
 * @see Board
 */
public class TowerFactory
{
    /** 0.017 is approximately 60 fps **/
    private static final double FRAME_TIME_SECONDS = 0.017;
    private final Board board;
    private final List<Tower> towers;
    private double gameTime;

    public TowerFactory(Board board) {
	this.board = board;
	this.towers = new ArrayList<>();
	this.gameTime = 0;
    }

    public Tower createTower(TowerType type, int row, int col) {
	Point position = new Point(col, row);

	LogHandler.info(TowerFactory.class, "Creating " + type + " tower at position (" + row + "," + col + ")");

	Tower tower = switch (type) {
	    case BASIC -> new BasicTower(position);
	    case SNIPER -> new SniperTower(position);
	    case SPLASH -> new SplashTower(position);
	    case BOMBER -> new BomberTower(position);
	    case SLOW -> new SlowTower(position);
	};

	tower.setBoard(board);
	towers.add(tower);
	return tower;
    }
    /**
     * Updates all towers for one game tick.
     * <p>
     * Increments the game time and processes each tower's attack logic. Handles different attack patterns based on tower types.
     */
    public void tick() {
	updateGameTime();
	updateTowerPowerups();
	processTowerAttacks();
    }


    private void updateGameTime() {
	gameTime += FRAME_TIME_SECONDS;
    }

    /**
     * Updates powerups for all towers, removing expired ones.
     */
    private void updateTowerPowerups() {
	for (Tower tower : towers) {
	    tower.updatePowerups();
	}
    }

    private void processTowerAttacks() {
	List<Enemy> enemies = board.getEnemyFactory().getEnemies();

	for (Tower tower : towers) {
	    if (tower.canShoot(gameTime)) {
		tower.processAttack(enemies, gameTime);
	    }
	}
    }

    public List<Tower> getTowers() {
	return towers;
    }
}