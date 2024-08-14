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

public class GameUI extends JPanel {
    private SenetGame game;
    private SenetBoard board;
    private JLabel[][] houseLabels;
    private Map<String, ImageIcon> pieceImages;
    private JLabel[] diceLabels;
    private ImageIcon whiteStickIcon;
    private ImageIcon blackStickIcon;
    private JButton rollDiceButton;
    private JButton endTurnButton;
    private JButton finishButton;
    private Piece selectedPiece;
    private int selectedRow, selectedCol;
    private String playerName;
    private Color highlightColor = Color.RED; // Highlight color
    private int currentRollResult = 0;
    private int tempCurrentRollResult;

    public GameUI(SenetGame game) {
        this.game = game;
        this.board = game.getBoard();
        this.houseLabels = new JLabel[3][10];
        this.pieceImages = new HashMap<>();
        this.diceLabels = new JLabel[4];
        this.selectedPiece = null;

        setLayout(new BorderLayout());
        askPlayerName();
        setupUI();
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
        loadPieceImages();
        loadDiceImages();

        // Setup board panel
        JPanel boardPanel = createBoardPanel();
        JPanel dicePanel = createDicePanel();

        // Add panels to the GameUI panel
        add(boardPanel, BorderLayout.CENTER);
        add(dicePanel, BorderLayout.SOUTH);
    }

//    private void setupMenu(JMenuBar menuBar) {
//        JMenu fileMenu = new JMenu("File");
//        JMenu helpMenu = new JMenu("Help");
//        JMenuItem newGameMenuItem = new JMenuItem("New Game");
//        JMenuItem rulesMenuItem = new JMenuItem("Rules");
//
//        newGameMenuItem.addActionListener(e -> startNewGame());
//        rulesMenuItem.addActionListener(e -> openRulesFile());
//
//        fileMenu.add(newGameMenuItem);
//        helpMenu.add(rulesMenuItem);
//        menuBar.add(fileMenu);
//        menuBar.add(helpMenu);
//    }

//    private void openRulesFile() {
//        try {
//            Desktop.getDesktop().open(new File("src/Assets/Rules.html"));
//        } catch (IOException ex) {
//            JOptionPane.showMessageDialog(this, "Unable to open rules file.");
//        }
//    }

    private JPanel createBoardPanel() {
        JPanel boardPanel = new JPanel(new GridLayout(3, 10));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        boardPanel.setBackground(Color.LIGHT_GRAY);
        initializeHouseLabels(boardPanel); // Initialize house labels
        return boardPanel;
    }

    private JPanel createDicePanel() {
        JPanel dicePanel = new JPanel(new FlowLayout());
        initializeDiceLabels(dicePanel);

        // Setup roll button
        rollDiceButton = new JButton("Throw Sticks");
        rollDiceButton.addActionListener(e -> rollDiceAndDisplay());
        dicePanel.add(rollDiceButton);

        // Setup end turn button
        endTurnButton = new JButton("End Turn");
        endTurnButton.addActionListener(e -> endPlayerTurn());
        dicePanel.add(endTurnButton);

        // Setup exit token button
        finishButton = new JButton("Finish");
        finishButton.setEnabled(false);
        finishButton.addActionListener(e -> handleFinishPiece());
        dicePanel.add(finishButton);

        return dicePanel;
    }

    private void loadPieceImages() {
        // Load images for white and black pieces
        String[] colors = {Player.WHITE, Player.BLACK};
        for (String color : colors) {
            String imagePath = "Assets/images/" + color.toLowerCase() + ".png";
            pieceImages.put(color, new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource(imagePath))));
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
            // Deselect piece logic
            selectedPiece = null;
            finishButton.setEnabled(false); // Disable finish button on deselect
            System.out.println("Deselected piece.");
        } else if (selectedPiece == null) {
            // Select piece logic
            selectedPiece = game.getBoard().getPieceAt(row, col);
            if (selectedPiece != null) {
                // Check if the piece belongs to the current player
                String currentPlayerColor = game.isPlayerTurn() ? Player.WHITE : Player.BLACK;
                if (selectedPiece.getOwnerColor().equals(currentPlayerColor)) {
                    selectedRow = row;
                    selectedCol = col;
                    houseLabels[selectedRow][selectedCol].setBackground(highlightColor);

                    // Check if the piece can finish and enable the button
                    if (game.canPieceFinish(selectedPiece, row, col, currentRollResult)) {
                        finishButton.setEnabled(true);
                    } else {
                        finishButton.setEnabled(false);
                    }

                    System.out.println("Selected piece at (" + row + ", " + col + ").");
                } else {
                    selectedPiece = null;
                    finishButton.setEnabled(false); // Disable finish button on invalid selection
                    System.out.println("Cannot select opponent's piece.");
                }
            }
        } else {
            // attempt to Move piece using the roll result
            boolean success = game.movePiece(selectedRow, selectedCol, currentRollResult);
            if (success) {
                tempCurrentRollResult = currentRollResult;

                // Reset roll result after a successful move
                currentRollResult = 0;

                // Check if the player gets another turn
                if (tempCurrentRollResult == 1 || tempCurrentRollResult == 4 || tempCurrentRollResult == 5) {
                    game.notifyExtraRoll();
//                    JOptionPane.showMessageDialog(this, "You get another turn!");
                    rollDiceButton.setEnabled(true);
                } else {
                    // Switch turn to the computer if the player does not get another turn
                    if (game.isPlayerTurn()) {
                        game.setPlayerStarts(false); // Switch turn to the computer
                        performComputerMoveGUI();
                    }
                }
            } else {
                System.out.println("Invalid move.");
            }
            selectedPiece = null;
            tempCurrentRollResult = 0;
            finishButton.setEnabled(false); // Disable finish button after move
            updateBoardDisplay();
        }
    }

    private void initializeDiceLabels(JPanel dicePanel) {
        // Initialize dice labels and add to dice panel
        for (int i = 0; i < 4; i++) {
            JLabel label = new JLabel();
            label.setHorizontalAlignment(SwingConstants.CENTER);
            diceLabels[i] = label;
            dicePanel.add(diceLabels[i]);
        }
    }

    void startNewGame() {
        game.startGame();
        updateBoardDisplay();
        rollDiceButton.setEnabled(true); // Enable the roll dice button for the first turn
        currentRollResult = 0; // Ensure roll result is reset
        System.out.println("New game started.");

        askWhoStarts();
    }

    void askWhoStarts() {
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
                performComputerMoveGUI();
                break;
            case JOptionPane.CANCEL_OPTION:
                boolean playerStarts = new Random().nextBoolean();
                game.setPlayerStarts(playerStarts);
                if (!playerStarts) {
                    performComputerMoveGUI();
                }
                break;
            default:
                game.setPlayerStarts(true); // Default to player starts
        }
    }

    void performComputerMoveGUI() {
        Timer timer = new Timer(1000, e -> {
            game.performComputerMove();
            updateBoardDisplay();
            game.checkGameOver();
            if (game.isPlayerTurn()) {
                rollDiceButton.setEnabled(true);
            } else {
                performComputerMoveGUI();
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
        JOptionPane.showMessageDialog(this, "You threw: " + currentRollResult);

        // Debug statement
        System.out.println();
        System.out.println("Dice rolled: " + currentRollResult);

        // Allow the player to select a piece to move based on the roll
        rollDiceButton.setEnabled(false);

        updateBoardDisplay();
        game.checkGameOver();
    }

    private void handleFinishPiece() {
        if (selectedPiece == null) {
            JOptionPane.showMessageDialog(this, "No piece selected.");
            return;
        }

        int row = selectedRow;
        int col = selectedCol;

        // Check if the piece can finish
        if (game.canPieceFinish(selectedPiece, row, col, currentRollResult)) {
            // Move the piece out of the board
            board.setPieceAt(row, col, null);
            System.out.println("Piece at (" + row + ", " + col + ") has finished and moved out.");

            // Update game state
            if (selectedPiece.getOwnerColor().equals(Player.WHITE)) {
                game.incrementWhiteFinishedPieces();
                System.out.println("White pieces finished:" + game.getWhitePlayerFinishedPieces());
            } else {
                game.incrementBlackFinishedPieces();
                System.out.println("Black pieces finished:" + game.getBlackPlayerFinishedPieces());
            }
            game.checkGameOver(); // Check if the game has ended

            // Reset selected piece and roll result
            houseLabels[selectedRow][selectedCol].setBackground(Color.WHITE);
            selectedPiece = null;
            currentRollResult = 0;
            updateBoardDisplay();
            game.endPlayerTurn();
            System.out.println("End turn to continue");

            // Disable the finish button as the piece has been moved
            finishButton.setEnabled(false);
        } else {
            JOptionPane.showMessageDialog(this, "The piece cannot finish with the current roll.");
        }
    }

    private void endPlayerTurn() {
        // Ensure that the player can only end their turn if it's actually their turn
        if (game.isPlayerTurn()) {
            game.setPlayerStarts(false); // Pass turn to the computer
            performComputerMoveGUI(); // Trigger computer move
        } else {
            JOptionPane.showMessageDialog(this, "It's not your turn.");
        }
    }
}
