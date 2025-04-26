package se.liu.feljo718.towerDefence;

import javax.swing.*;
import java.awt.*;

/**
 * Provides a shop interface for purchasing powerups for towers.
 * <p>
 * This class presents a modal dialog that displays available powerup types,
 * their costs, and effects. The shop ensures that players have sufficient
 * coins before allowing a purchase.
 *
 * @author feljo718
 * @see PowerupType
 * @see Tower
 */
public class PowerupShop {
    private final JDialog dialog;
    private final Board board;
    private final Tower targetTower;
    private PowerupType selectedPowerupType = null;

    /**
     * Creates a new powerup shop dialog linked to the specified game board and tower.
     *
     * @param parent      The parent frame that owns this dialog
     * @param board       The game board to associate with this shop
     * @param targetTower The tower to apply powerups to
     */
    public PowerupShop(JFrame parent, Board board, Tower targetTower) {
        this.board = board;
        this.targetTower = targetTower;

        dialog = new JDialog(parent, "Powerup Shop", true);
        dialog.setLayout(new GridLayout(PowerupType.values().length, 1, 5, 5));

        for (PowerupType type : PowerupType.values()) {
            JButton powerupButton = createPowerupButton(type);
            dialog.add(powerupButton);
        }

        dialog.pack();
        dialog.setLocationRelativeTo(parent);
    }

    /**
     * Creates a button for purchasing a specific powerup type.
     *
     * @param type The powerup type to create a button for
     * @return A configured button for the specified powerup type
     */
    private JButton createPowerupButton(PowerupType type) {
        JButton button = new JButton(String.format("%s (%d coins) - %s",
                type.getName(), type.getCost(), type.getDescription()));

        button.addActionListener(e -> {
            if (board.getCoins() >= type.getCost()) {
                // Check if the powerup is already active
                if (targetTower.hasActivePowerupOfType(type)) {
                    JOptionPane.showMessageDialog(dialog,
                            "This powerup is already active on the tower!",
                            "Powerup Active", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int result = JOptionPane.showConfirmDialog(dialog,
                        "Apply " + type.getName() + " to this tower?",
                        "Confirm Purchase", JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    board.gainCoins(-type.getCost());
                    targetTower.applyPowerup(type, 30000); // 30 seconds duration
                    selectedPowerupType = type;
                    dialog.dispose();
                }
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Not enough coins!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return button;
    }

    public PowerupType getSelectedPowerupType() {
        return selectedPowerupType;
    }

    public void show() {
        dialog.setVisible(true);
    }
}