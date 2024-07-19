import components.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GameUI extends JFrame {
    private SenetGame game;
    private JLabel[][] houseLabels;
    private Map<String, ImageIcon> pieceImages;
    private JLabel[] diceLabels;
    private ImageIcon whiteStickIcon;
    private ImageIcon blackStickIcon;

    public GameUI(SenetGame game) {
        this.game = game;
        this.houseLabels = new JLabel[3][10];
        this.pieceImages = new HashMap<>();
        this.diceLabels = new JLabel[4];

        loadPieceImages(); // Load piece images into memory
        loadDiceImages(); // Load dice images into memory

        setTitle("Senet Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        // Setup board panel
        JPanel boardPanel = new JPanel(new GridLayout(3, 10));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Initialize house labels
        initializeHouseLabels(boardPanel);

        // Setup dice panel
        JPanel dicePanel = new JPanel(new FlowLayout());
        initializeDiceLabels(dicePanel);

        // Add panels to the frame
        getContentPane().add(boardPanel, BorderLayout.CENTER);
        getContentPane().add(dicePanel, BorderLayout.SOUTH);

        // Display the frame
        setVisible(true);
    }

    private void loadPieceImages() {
        // Load images for white and black pieces
        String[] colors = {"White", "Black"};
        for (String color : colors) {
            String imagePath = new Piece(null, color).getImagePath();
            ImageIcon imageIcon = new ImageIcon(imagePath);
            pieceImages.put(color, imageIcon);
        }
    }

    private void loadDiceImages() {
        // Load images for white and black stick faces
        whiteStickIcon = new ImageIcon("Assets/images/white-stick.png");
        blackStickIcon = new ImageIcon("Assets/images/black-stick.png");
    }

    private void initializeHouseLabels(JPanel boardPanel) {
        SenetBoard board = game.getBoard();

        // Initialize house labels and add to board panel
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 10; col++) {
                JLabel label = new JLabel();
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setOpaque(true);
                label.setBorder(new LineBorder(Color.BLACK));
                houseLabels[row][col] = label;
                boardPanel.add(label);
            }
        }

        updateBoardDisplay(); // Update board with current piece positions
    }

    private void initializeDiceLabels(JPanel dicePanel) {
        // Initialize dice labels and add to dice panel
        for (int i = 0; i < 4; i++) {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            diceLabels[i] = label;
            dicePanel.add(label);
        }
    }

    public void updateBoardDisplay() {
        SenetBoard board = game.getBoard();

        // Update house labels with current piece positions
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 10; col++) {
                Piece piece = board.getPieceAt(row, col);
                if (piece != null) {
                    String color = piece.getColor();
                    ImageIcon imageIcon = pieceImages.get(color);
                    houseLabels[row][col].setIcon(imageIcon);
                } else {
                    houseLabels[row][col].setIcon(null); // No piece
                }
            }
        }
    }

    public void rollDiceAndDisplay() {
        Dice dice = new Dice();
        int rollResult = dice.roll();

        // Display dice roll result using stick images
        int whiteFaces = (rollResult == 5) ? 0 : rollResult;
        for (int i = 0; i < 4; i++) {
            if (i < whiteFaces) {
                diceLabels[i].setIcon(whiteStickIcon);
            } else {
                diceLabels[i].setIcon(blackStickIcon);
            }
        }

        // Display the result of the roll
        JOptionPane.showMessageDialog(this, "You rolled: " + rollResult);
    }
}
