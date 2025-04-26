package se.liu.feljo718.towerDefence;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Represents a modal dialog for tower management in the Tower Defense game.
 * <p>
 * This dialog allows players to view information about a selected tower,
 * upgrade its capabilities, or remove it from the game board. The dialog
 * displays tower statistics and provides controls for interacting with the tower.
 *
 * @author feljo718
 * @see Tower
 * @see Board
 * @see TowerShop
 */
public class TowerMenu {
    private final JDialog dialog;
    private final Tower tower;
    private final Board board;

    /**
     * Creates a tower management dialog for the specified tower.
     * <p>
     * Initializes the dialog with tower information and control buttons.
     * The dialog is modal and positioned relative to its parent window.
     *
     * @param parent The parent frame that owns this dialog
     * @param tower  The tower to be managed through this menu
     * @param board  The game board containing the tower
     */
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
        // Change from 4 rows, 2 columns to 2 rows, 4 columns
        panel.setLayout(new GridLayout(2, 4, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add tower information (now 2 label-value pairs per row)
        panel.add(new JLabel("Type:"));
        panel.add(new JLabel(tower.getType().name()));
        panel.add(new JLabel("Range:"));
        panel.add(new JLabel(tower.getRange() + " tiles"));

        panel.add(new JLabel("Damage:"));
        panel.add(new JLabel(tower.getDamage() + ""));
        panel.add(new JLabel("Fire Rate:"));
        panel.add(new JLabel(tower.getFireRate() + " shots/sec"));

        // For powerups, create a separate panel below since they may have variable content
        JPanel powerupPanel = new JPanel(new BorderLayout());
        powerupPanel.setBorder(BorderFactory.createTitledBorder("Active Powerups"));

        List<Powerup> activePowerups = tower.getActivePowerups();
        if (activePowerups.isEmpty()) {
            powerupPanel.add(new JLabel("None"), BorderLayout.CENTER);
        } else {
            JPanel powerupsList = new JPanel(new GridLayout(activePowerups.size(), 1));
            for (Powerup powerup : activePowerups) {
                long remainingSeconds = powerup.getRemainingTime() / 1000;
                powerupsList.add(new JLabel(powerup.getType().getName() + " (" + remainingSeconds + "s)"));
            }
            powerupPanel.add(powerupsList, BorderLayout.CENTER);
        }

        // Use a main panel with BorderLayout to position everything
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.NORTH);
        mainPanel.add(powerupPanel, BorderLayout.CENTER);

        return mainPanel;
    }
    private void openPowerupShop() {
        PowerupShop shop = new PowerupShop((JFrame) dialog.getParent(), board, tower);
        shop.show();

        // If a powerup was selected, refresh the menu to show updated stats
        if (shop.getSelectedPowerupType() != null) {
            dialog.dispose();
            TowerMenu refreshedMenu = new TowerMenu((JFrame) dialog.getParent(), tower, board);
            refreshedMenu.show();
        }
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // Upgrade button
        JButton upgradeButton = new JButton("Upgrade Tower");
        upgradeButton.addActionListener(e -> upgradeTower());
        panel.add(upgradeButton);

        // Powerup button
        JButton powerupButton = new JButton("Apply Powerup");
        powerupButton.addActionListener(e -> openPowerupShop());
        panel.add(powerupButton);

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

    /**
     * Attempts to upgrade the current tower.
     * <p>
     * Checks if the tower can be upgraded (not at max level) and if the player
     * has enough coins for the upgrade. Deducts coins and increases the tower's
     * level if conditions are met.
     */
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

    /**
     * Removes the current tower from the game board.
     * <p>
     * Confirms the action with the player before removing the tower.
     * Returns a portion of the tower's cost to the player's coin balance.
     */
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
