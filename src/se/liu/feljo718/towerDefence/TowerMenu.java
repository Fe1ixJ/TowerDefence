package se.liu.feljo718.towerDefence;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TowerMenu {
    private final JDialog dialog;
    private final Tower tower;
    private final Board board;

    public TowerMenu(JFrame parent, Tower tower, Board board){
        this.tower = tower;
        this.board = board;

        dialog = new JDialog(parent, "Tower Menu", false);
        dialog.setLayout(new BorderLayout(10,10));

        JPanel infoPanel = createInfoPanel();

        dialog.add(infoPanel, BorderLayout.CENTER);
        dialog.add(createButtonPanel(), BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(parent);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeDialog();
            }
        });

    }
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add tower information
        panel.add(new JLabel("Type:"));
        panel.add(new JLabel(tower.getType().name()));

        panel.add(new JLabel("Range:"));
        panel.add(new JLabel(tower.getRange() + " tiles"));

        panel.add(new JLabel("Damage:"));
        panel.add(new JLabel(tower.getDamage() + ""));

        panel.add(new JLabel("Fire Rate:"));
        panel.add(new JLabel(tower.getFireRate() + " shots/sec"));

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // Upgrade button
        JButton upgradeButton = new JButton("Upgrade Tower");
        upgradeButton.addActionListener(e -> upgradeTower());
        panel.add(upgradeButton);

        // Remove button
        JButton removeButton = new JButton("Remove Tower");
        removeButton.addActionListener(e -> removeTower());
        panel.add(removeButton);

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> closeDialog());
        panel.add(closeButton);

        return panel;
    }

    private void upgradeTower() {
        // Check if tower is already at max level
        if (tower.getLevel() >= tower.getType().getMaxLevel()) {
            JOptionPane.showMessageDialog(dialog,
                    "Tower is already at maximum level!",
                    "Upgrade Failed",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Get upgrade cost
        int upgradeCost = getTowerCost(tower.getType());

        if (board.getCoins() >= upgradeCost) {
            // Deduct coins and upgrade tower
            board.gainCoins(-upgradeCost);
            tower.upgrade();

            JOptionPane.showMessageDialog(dialog,
                    "Tower upgraded to level " + tower.getLevel() + "!",
                    "Upgrade Complete",
                    JOptionPane.INFORMATION_MESSAGE);

            // Close the dialog after upgrade
            closeDialog();
        } else {
            // Show error if not enough coins
            JOptionPane.showMessageDialog(dialog,
                    "Not enough coins to upgrade! You need " + upgradeCost + " coins.",
                    "Upgrade Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private void removeTower() {
        // Confirm tower removal
        int result = JOptionPane.showConfirmDialog(dialog,
                "Are you sure you want to remove this tower?",
                "Remove Tower",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            // Remove tower and refund some coins (50% of original cost)
            int refund = getTowerCost(tower.getType()) / 2;
            board.getTowerFactory().getTowers().remove(tower);
            board.gainCoins(refund);

            JOptionPane.showMessageDialog(dialog,
                    "Tower removed. You received " + refund + " coins.",
                    "Tower Removed",
                    JOptionPane.INFORMATION_MESSAGE);

            closeDialog();
        }
    }

    private int getTowerCost(TowerType type) {
        return TowerShop.getStaticTowerCost(type);
    }

    private void closeDialog() {
        dialog.dispose();
    }

    public void show() {
        dialog.setVisible(true);
    }




}
