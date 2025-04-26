package se.liu.feljo718.towerDefence;

import javax.swing.*;
import java.awt.GridLayout;

/**
 * Provides a shop interface for purchasing new towers in the Tower Defense game.
 * <p>
 * This class presents a modal dialog that displays the available tower types,
 * their costs, and handles the tower selection process. The shop ensures that
 * players have sufficient coins before allowing a purchase.
 *
 * @author feljo718
 * @see Tower
 * @see TowerType
 * @see Board
 */
public class TowerShop {
    private final JDialog dialog;
    private final Board board;
    private TowerType selectedTowerType = null;

    /**
     * Creates a new tower shop dialog linked to the specified game board.
     * <p>
     * Initializes a modal dialog with buttons for each available tower type,
     * displaying their names and costs.
     *
     * @param parent The parent frame that owns this dialog
     * @param board  The game board to associate with this shop
     */
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

    /**
     * Creates a button for purchasing a specific tower type.
     * <p>
     * Configures the button with tower information and sets up an action
     * listener that validates the player has enough coins before selecting.
     *
     * @param type The tower type to create a button for
     * @return A configured button for the specified tower type
     */
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

    /**
     * Returns the cost for a tower of the specified type.
     * <p>
     * Provides a consistent pricing system for all tower types across the game.
     *
     * @param type The type of tower to get the cost for
     * @return The cost of the tower in coins
     */
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