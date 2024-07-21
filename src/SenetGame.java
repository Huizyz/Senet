import components.*;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class SenetGame extends Component {
    private SenetBoard board;
    private Player player;
    private Player computer;
    private Dice dice;
    private boolean isPlayerTurn;

    public SenetGame() {
        board = new SenetBoard();
        dice = new Dice();
    }

    public void startGame() {
        String playerName = askPlayerName();
        player = new Player(playerName);
        computer = new Player(Player.BLACK);

        String startingPlayer = askWhoStarts();
        isPlayerTurn = determineStartingPlayer(startingPlayer);

        board.initializeBoard();
        board.initializePieces();

        System.out.println("Game started. Player: " + playerName + ", Computer: Black");
    }

    private String askPlayerName() {
        return JOptionPane.showInputDialog("Enter your name:");
    }

    private String askWhoStarts() {
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
                setPlayerStarts(true);
                break;
            case JOptionPane.NO_OPTION:
                setPlayerStarts(false);
                performComputerMove();
                break;
            case JOptionPane.CANCEL_OPTION:
                boolean playerStarts = new Random().nextBoolean();
                setPlayerStarts(playerStarts);
                if (!playerStarts) {
                    performComputerMove();
                }
                break;
            default:
                setPlayerStarts(true); // Default to player starts
        }
        return null;
    }

    void setPlayerStarts(boolean playerStarts) {
        isPlayerTurn = playerStarts;
    }

    private boolean determineStartingPlayer(String startingPlayer) {
        if ("Player".equalsIgnoreCase(startingPlayer)) {
            return true;
        } else if ("Computer".equalsIgnoreCase(startingPlayer)) {
            return false;
        } else {
            Random random = new Random();
            return random.nextBoolean();
        }
    }

    public SenetBoard getBoard() {
        return board;
    }

    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }

    public int rollDice() {
        return dice.roll();
    }

    public boolean movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        Piece piece = board.getPieceAt(fromRow, fromCol);

        if (piece != null && isValidMove(piece, fromRow, fromCol, toRow, toCol)) {
            board.movePiece(fromRow, fromCol, toRow, toCol);
            System.out.println(piece.getOwnerColor() + " piece moved from (" + fromRow + ", " + fromCol + ") to (" + toRow + ", " + toCol + ").");
            return true;
        }
        return false;
    }

    private boolean isValidMove(Piece piece, int fromRow, int fromCol, int toRow, int toCol) {
        // Implement the game rules to check if a move is valid
        // For now, we'll just check if the target square is empty or contains an enemy piece
        if (toRow < 0 || toRow >= SenetBoard.NUM_ROWS || toCol < 0 || toCol >= SenetBoard.HOUSES_PER_ROW) {
            return false;
        }

        Piece targetPiece = board.getPieceAt(toRow, toCol);
        if (targetPiece == null) {
            return true;
        }

        if (!targetPiece.getOwnerColor().equals(piece.getOwnerColor())) {
            // Check if the target piece is protected
            boolean isProtected = isProtectedPiece(targetPiece, toRow, toCol);
            return !isProtected;
        }
        return false;
    }

    private boolean isProtectedPiece(Piece piece, int row, int col) {
        // Check if the piece at (row, col) is protected by adjacent pieces of the same color
        String color = piece.getOwnerColor();
        if (col > 0 && board.getPieceAt(row, col - 1) != null && board.getPieceAt(row, col - 1).getOwnerColor().equals(color)) {
            return true;
        }
        if (col < SenetBoard.HOUSES_PER_ROW - 1 && board.getPieceAt(row, col + 1) != null && board.getPieceAt(row, col + 1).getOwnerColor().equals(color)) {
            return true;
        }
        return false;
    }

    public void performComputerMove() {
        // Implement the logic for the computer to perform its move
        int rollResult = rollDice();
        System.out.println("Computer rolled: " + rollResult);

        // Add logic to select and move a piece for the computer based on the roll result
        // This is a placeholder implementation
        boolean moveMade = false;
        for (int row = 0; row < SenetBoard.NUM_ROWS && !moveMade; row++) {
            for (int col = 0; col < SenetBoard.HOUSES_PER_ROW && !moveMade; col++) {
                Piece piece = board.getPieceAt(row, col);
                if (piece != null && piece.getOwnerColor().equals(Player.BLACK)) {
                    int newRow = row;
                    int newCol = col + rollResult;
                    if (newCol >= SenetBoard.HOUSES_PER_ROW) {
                        newRow += newCol / SenetBoard.HOUSES_PER_ROW;
                        newCol %= SenetBoard.HOUSES_PER_ROW;
                    }
                    if (isValidMove(piece, row, col, newRow, newCol)) {
                        movePiece(row, col, newRow, newCol);
                        moveMade = true;
                    }
                }
            }
        }

        if (!moveMade) {
            System.out.println("Computer could not make a move.");
        }

        // Switch turn back to the player
        isPlayerTurn = true;
    }

    public void checkGameOver() {
        // Check if all pieces of one player are off the board
        boolean playerWins = true;
        boolean computerWins = true;
        for (int row = 0; row < SenetBoard.NUM_ROWS; row++) {
            for (int col = 0; col < SenetBoard.HOUSES_PER_ROW; col++) {
                Piece piece = board.getPieceAt(row, col);
                if (piece != null && piece.getOwnerColor().equals(Player.WHITE)) {
                    playerWins = false;
                } else if (piece != null && piece.getOwnerColor().equals(Player.BLACK)) {
                    computerWins = false;
                }
            }
        }

        if (playerWins) {
            JOptionPane.showMessageDialog(null, "Player wins!");
        } else if (computerWins) {
            JOptionPane.showMessageDialog(null, "Computer wins!");
        }
    }

    public void setPlayerName(String playerName) {
        if (player != null) {
            player.setName(playerName);
        }
    }
}
