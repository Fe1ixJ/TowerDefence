package se.liu.feljo718.towerDefence;

import javax.swing.*;
import java.awt.GridLayout;

public class TowerShop {
    private final JDialog dialog;
    private final Board board;
    private TowerType selectedTowerType = null;

    public TowerShop(JFrame parent, Board board) {
        this.board = board;

        // Create a dialog instead of extending it
        dialog = new JDialog(parent, "Tower Shop", true);
        dialog.setLayout(new GridLayout(3, 1, 5, 5));

        for (TowerType type : TowerType.values()) {
            JButton towerButton = createTowerButton(type);
            dialog.add(towerButton);
        }

        dialog.pack();
        dialog.setLocationRelativeTo(parent);
    }

    private JButton createTowerButton(TowerType type) {
        JButton button = new JButton(String.format("%s Tower (%d coins)",
                type.name(), getStaticTowerCost(type)));

        button.addActionListener(e -> {
            if (board.getCoins() >= getStaticTowerCost(type)) {
                selectedTowerType = type;
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Not enough coins!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return button;
    }

    public static int getStaticTowerCost(TowerType type) {
        return switch (type) {
            case BASIC -> 100;
            case SNIPER -> 150;
            case SPLASH -> 200;
        };
    }

    public TowerType getSelectedTower() {
        return selectedTowerType;
    }

    public void show() {
        dialog.setVisible(true);
    }
}