import components.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class GameUI extends JFrame {
    private SenetGame game;
    private JLabel[][] houseLabels;
    private Map<String, ImageIcon> pieceImages;
    private JLabel[] diceLabels;
    private ImageIcon whiteStickIcon;
    private ImageIcon blackStickIcon;
    private JButton rollDiceButton;
    private Piece selectedPiece;
    private int selectedRow, selectedCol;
    private String playerName;
    private Color highlightColor = Color.RED; // Highlight color
    private int currentRollResult = 0;

    public GameUI(SenetGame game) {
        this.game = game;
        this.houseLabels = new JLabel[3][10];
        this.pieceImages = new HashMap<>();
        this.diceLabels = new JLabel[4];
        this.selectedPiece = null;


        startNewGame();
    }

    private void askPlayerName() {
        playerName = JOptionPane.showInputDialog(this, "Enter your name:", "Player Name", JOptionPane.PLAIN_MESSAGE);
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Player";
        }
        game.setPlayerName(playerName);
    }

    private void setupUI() {
        loadPieceImages(); // Load piece images into memory
        loadDiceImages(); // Load dice images into memory

        setTitle("Senet Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);

        // Setup menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        JMenuItem newGameMenuItem = new JMenuItem("New Game");
        JMenuItem highScoresMenuItem = new JMenuItem("High Scores");
        JMenuItem rulesMenuItem = new JMenuItem("Rules");
        newGameMenuItem.addActionListener(e -> startNewGame());
        highScoresMenuItem.addActionListener(e -> HighScoreUI.showHighScores());
        rulesMenuItem.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(new File("src/Assets/Rules.html"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        fileMenu.add(newGameMenuItem);
        fileMenu.add(highScoresMenuItem);
        helpMenu.add(rulesMenuItem);
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        // Setup board panel
        JPanel boardPanel = new JPanel(new GridLayout(3, 10));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        boardPanel.setBackground(Color.LIGHT_GRAY);

        // Initialize house labels
        initializeHouseLabels(boardPanel);

        // Setup dice panel
        JPanel dicePanel = new JPanel(new FlowLayout());
        initializeDiceLabels(dicePanel);

        // Setup roll button
        rollDiceButton = new JButton("Roll Dice");
        rollDiceButton.addActionListener(e -> rollDiceAndDisplay());
        dicePanel.add(rollDiceButton);

        // Add panels to the frame
        getContentPane().add(boardPanel, BorderLayout.CENTER);
        getContentPane().add(dicePanel, BorderLayout.SOUTH);

        // Display the frame
        setVisible(true);
    }

    private void loadPieceImages() {
        // Load images for white and black pieces
        String[] colors = {Player.WHITE, Player.BLACK};
        for (String color : colors) {
            String imagePath = "Assets/images/" + color.toLowerCase() + ".png";
            ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource(imagePath)));
            pieceImages.put(color, imageIcon);
        }
    }

    private void loadDiceImages() {
        // Load images for white and black stick faces
        whiteStickIcon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("Assets/images/white-stick.png")));
        blackStickIcon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("Assets/images/black-stick.png")));
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
                label.setBackground(Color.WHITE);
                int finalRow = row;
                int finalCol = col;
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleHouseClick(finalRow, finalCol, e);
                    }
                });
                houseLabels[row][col] = label;
                boardPanel.add(label);
            }
        }

        updateBoardDisplay(); // Update board with current piece positions
    }

    private void handleHouseClick(int row, int col, MouseEvent e) {
        if (!game.hasRolledDice) {
            JOptionPane.showMessageDialog(this, "You must roll the dice before making a move.");
            return;
        }

        // Reset previously selected piece label color
        if (selectedPiece != null) {
            houseLabels[selectedRow][selectedCol].setBackground(Color.WHITE);
        }

        if (SwingUtilities.isRightMouseButton(e)) {
            selectedPiece = null;
            System.out.println("Deselected piece.");
        } else if (selectedPiece == null) {
            selectedPiece = game.getBoard().getPieceAt(row, col);
            if (selectedPiece != null) {
                // Check if the piece belongs to the current player
                String currentPlayerColor = game.isPlayerTurn() ? Player.WHITE : Player.BLACK;
                if (selectedPiece.getOwnerColor().equals(currentPlayerColor)) {
                    selectedRow = row;
                    selectedCol = col;
                    houseLabels[selectedRow][selectedCol].setBackground(highlightColor);
                    System.out.println("Selected piece at (" + row + ", " + col + ").");
                } else {
                    selectedPiece = null;
                    System.out.println("Cannot select opponent's piece.");
                }
            }
        } else {
            // Move piece using the roll result
            boolean success = game.movePiece(selectedRow, selectedCol, currentRollResult);
            if (success) {
                System.out.println("Moved piece from (" + selectedRow + ", " + selectedCol + ") with roll result " + currentRollResult);
                // Check if the player gets another turn
                if (currentRollResult == 1 || currentRollResult == 4 || currentRollResult == 5) {
                    JOptionPane.showMessageDialog(this, "You get another turn!");
                    rollDiceButton.setEnabled(true);
                } else {
                    game.setPlayerStarts(!game.isPlayerTurn()); // Switch turn to the computer
                    performComputerMove();
                }
            } else {
                System.out.println("Invalid move.");
            }
            selectedPiece = null;
            currentRollResult = 0; // Reset roll result after move
            updateBoardDisplay();
        }
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

    private void startNewGame() {
        askPlayerName();
        setupUI();
        //game.hasRolledDice = false;
        game.startGame();
        updateBoardDisplay();
        System.out.println("New game started.");

        askWhoStarts();
    }

    private void askWhoStarts() {
        int option = JOptionPane.showOptionDialog(this,
                "Who should start the game?",
                "Start Game",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Player", "Computer", "Random"},
                "Player");

        switch (option) {
            case JOptionPane.YES_OPTION:
                game.setPlayerStarts(true);
                break;
            case JOptionPane.NO_OPTION:
                game.setPlayerStarts(false);
                performComputerMove();
                break;
            case JOptionPane.CANCEL_OPTION:
                boolean playerStarts = new Random().nextBoolean();
                game.setPlayerStarts(playerStarts);
                if (!playerStarts) {
                    performComputerMove();
                }
                break;
            default:
                game.setPlayerStarts(true); // Default to player starts
        }
    }

    private void performComputerMove() {
        Timer timer = new Timer(1000, e -> {
            game.performComputerMove();
            updateBoardDisplay();
            game.checkGameOver();
            if (game.isPlayerTurn()) {
                rollDiceButton.setEnabled(true);
            } else {
                performComputerMove();
            }
        });
        rollDiceButton.setEnabled(false);
        timer.setRepeats(false);
        timer.start();
    }

    public void updateBoardDisplay() {
        SenetBoard board = game.getBoard();

        // Update house labels with current piece positions
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 10; col++) {
                Piece piece = board.getPieceAt(row, col);
                if (piece != null) {
                    String color = piece.getOwnerColor();
                    ImageIcon imageIcon = pieceImages.get(color);
                    houseLabels[row][col].setIcon(imageIcon);
                } else {
                    houseLabels[row][col].setIcon(null); // No piece
                }
            }
        }
    }

    public void rollDiceAndDisplay() {
        currentRollResult = game.rollDice();

        // Display dice roll result using stick images
        int whiteFaces = (currentRollResult == 5) ? 0 : currentRollResult;
        for (int i = 0; i < 4; i++) {
            if (i < whiteFaces) {
                diceLabels[i].setIcon(whiteStickIcon);
            } else {
                diceLabels[i].setIcon(blackStickIcon);
            }
        }

        // Display the result of the roll
        JOptionPane.showMessageDialog(this, "You rolled: " + currentRollResult);

        // Debug statement
        System.out.println("Dice rolled: " + currentRollResult);

        // Allow the player to select a piece to move based on the roll
        rollDiceButton.setEnabled(false);

        updateBoardDisplay();
        game.checkGameOver();
        if (!game.isPlayerTurn()) {
            performComputerMove();
        }
    }

    public static void main(String[] args) {
        SenetGame game = new SenetGame();
        new GameUI(game);
    }
}
