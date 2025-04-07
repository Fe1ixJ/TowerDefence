package se.liu.feljo718.towerDefence;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardComponent extends JComponent implements BoardListener {
    public static final int TILE_SIZE = 40;
    public static final int ENEMY_SIZE = 30;
    public static final int TOWER_SIZE = 30;
    private final Board board;
    private final JLabel livesLabel;
    private final JLabel coinsLabel;
    private final JLabel roundLabel;
    private final JButton buyMenuButton;
    private final JButton nextRoundButton;
    private final JButton viewCircleButton;
    private boolean placingTower = false;
    private TowerType selectedTower = null;
    private boolean showRangeCircles = false;

    public BoardComponent(Board board) {
        this.board = board;
        this.board.addBoardListener(this);
        setLayout(null);

        livesLabel = new JLabel("Lives: " + board.getLives());
        coinsLabel = new JLabel("Coins: " + board.getCoins());
        buyMenuButton = new JButton("Buy Tower");
        nextRoundButton = new JButton("Next Round");
        roundLabel = new JLabel("Round: " + board.getRound());
        viewCircleButton = new JButton("View Circle");


        // MAGIC NUMBER GEOMETRY FIX
        livesLabel.setBounds(10, 10, 100, 20);
        coinsLabel.setBounds(getPreferredSize().width - 110, 10, 100, 20);
        buyMenuButton.setBounds(200 - 50, 10, 100, 20);
        nextRoundButton.setBounds(200 + 75, 10, 120, 20);
        roundLabel.setBounds(200 + 200, 10, 75, 20);
        viewCircleButton.setBounds(200 + 300, 10, 100, 20);

        add(livesLabel);
        add(coinsLabel);
        add(buyMenuButton);
        add(nextRoundButton);
        add(roundLabel);
        add(viewCircleButton);

        buyMenuButton.addActionListener(e -> showTowerShop());
        nextRoundButton.addActionListener(e -> board.startNextRound());
        viewCircleButton.addActionListener(e -> {
            showRangeCircles = !showRangeCircles;
            viewCircleButton.setText(showRangeCircles ? "Hide Ranges" : "View Ranges");
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (placingTower) {
                    placeTower(e.getX(), e.getY());
                } else {
                    // Check if a tower was clicked
                    Tower clickedTower = getTowerAt(e.getX(), e.getY());
                    if (clickedTower != null) {
                        TowerMenu menu = new TowerMenu(
                                (JFrame) SwingUtilities.getWindowAncestor(BoardComponent.this),
                                clickedTower,
                                board);
                        menu.show();
                    }
                }
            }
        });
    }

    private void showTowerShop() {
        TowerShop shop = new TowerShop(
                (JFrame) SwingUtilities.getWindowAncestor(this), board);
        shop.show();

        TowerType selected = shop.getSelectedTower();
        if (selected != null) {
            selectedTower = selected;
            placingTower = true;
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
    }

    private void placeTower(int x, int y) {
        int col = x / TILE_SIZE;
        int row = y / TILE_SIZE;
        Point pos = new Point(col, row);

        if (board.getTileAt(row, col) == TileType.GRASS) {
            board.getTowerFactory().placeTower(selectedTower, pos);
            board.gainCoins(-getTowerCost(selectedTower));
        }

        placingTower = false;
        selectedTower = null;
        setCursor(Cursor.getDefaultCursor());
    }

    private int getTowerCost(TowerType type) {
        return switch (type) {
            case BASIC -> 100;
            case SNIPER -> 150;
            case SPLASH -> 200;
        };
    }

    private Tower getTowerAt(int x, int y) {
        int col = x / TILE_SIZE;
        int row = y / TILE_SIZE;

        // Check if any tower is at this position
        for (Tower tower : board.getTowerFactory().getTowers()) {
            Point pos = tower.getPosition();
            if (pos.x == col && pos.y == row) {
                return tower;
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        for (int row = 0; row < board.getHeight(); row++) {
            for (int col = 0; col < board.getWidth(); col++) {
                TileType tile = board.getTileAt(row, col);
                switch (tile) {
                    case GRASS -> g2d.setColor(Color.decode("#228B22"));
                    case PATH -> g2d.setColor(Color.decode("#996600"));
                    case START, END -> g2d.setColor(Color.decode("#FF0000"));
                    case WATER -> g2d.setColor(Color.BLUE);
                    case INTERFACE -> g2d.setColor(Color.LIGHT_GRAY);
                }
                g2d.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        // Draw enemies
        for (Enemy enemy : board.getEnemyFactory().getEnemies()) {
            Point pixelPos = enemy.getPixelPosition();
            switch (enemy.getType()) {
                case BASIC -> g2d.setColor(Color.BLACK);
                case FAST -> g2d.setColor(Color.RED);
                case TANK -> g2d.setColor(Color.GREEN);
            }
            int x = pixelPos.x + (TILE_SIZE - ENEMY_SIZE) / 2;
            int y = pixelPos.y + (TILE_SIZE - ENEMY_SIZE) / 2;
            g2d.fillOval(x, y, ENEMY_SIZE, ENEMY_SIZE);
        }

        // Draw towers
        for (Tower tower : board.getTowerFactory().getTowers()) {
            Point pos = tower.getPosition();
            g2d.setColor(tower.getColor());
            int x = pos.x * TILE_SIZE + (TILE_SIZE - TOWER_SIZE) / 2;
            int y = pos.y * TILE_SIZE + (TILE_SIZE - TOWER_SIZE) / 2;
            g2d.fillRect(x, y, TOWER_SIZE, TOWER_SIZE);
        }


        // Draw grid lines
        g2d.setColor(Color.BLACK);

        for (int row = 1; row <= board.getHeight(); row++) {
            g2d.drawLine(0, row * TILE_SIZE, board.getWidth() * TILE_SIZE, row * TILE_SIZE);
        }
        for (int col = 0; col <= board.getWidth(); col++) {
            g2d.drawLine(col * TILE_SIZE, TILE_SIZE, col * TILE_SIZE, board.getHeight() * TILE_SIZE);
        }

        if (showRangeCircles) {
            g2d.setColor(new Color(173, 216, 230, 128)); // Light blue with transparency

            for (Tower tower : board.getTowerFactory().getTowers()) {
                Point pos = tower.getPosition();
                double range = tower.getRange();

                // Calculate circle center position
                int centerX = pos.x * TILE_SIZE + TILE_SIZE / 2;
                int centerY = pos.y * TILE_SIZE + TILE_SIZE / 2;

                // Draw circle with diameter = range * 2 * TILE_SIZE
                double diameter = range * 2 * TILE_SIZE;
                g2d.drawOval((int)(centerX - diameter / 2), (int)(centerY - diameter / 2), (int)diameter, (int)diameter);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(board.getWidth() * TILE_SIZE, board.getHeight() * TILE_SIZE);
    }

    @Override
    public void boardChanged() {
        livesLabel.setText("Lives: " + board.getLives());
        coinsLabel.setText("Coins: " + board.getCoins());
        roundLabel.setText("Round: " + board.getRound());
        repaint();
    }


    @Override
    public void gameOver() {
    }
}

