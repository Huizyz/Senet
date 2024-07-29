import components.*;

import javax.swing.*;
import java.awt.*;

public class SenetGame extends Component {
    private SenetBoard board;
    private Player player;
    private Piece piece;
    private Dice dice;
    private boolean isPlayerTurn;
    boolean hasRolledDice;

    public SenetGame() {
        board = new SenetBoard();
        dice = new Dice();
        hasRolledDice = false; // Initialize the flag
    }

    public void startGame() {
        board.initializeBoard();
        board.initializePieces();
        hasRolledDice = false; // Reset the flag
        System.out.println("Game started.");
    }

    public void setPlayerStarts(boolean playerStarts) {
        isPlayerTurn = playerStarts;
    }

    public SenetBoard getBoard() {
        return board;
    }

    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }

    public int rollDice() {
        hasRolledDice = true; // Set the flag to true after rolling
        return dice.roll();
    }

    public boolean movePiece(int fromRow, int fromCol, int rollResult) {
        Piece piece = board.getPieceAt(fromRow, fromCol);
        if (piece == null) {
            return false;
        }

        // Attempt to move forward
        if (attemptMove(fromRow, fromCol, rollResult, true)) {
            return true;
        }

        // If forward move is not possible, attempt to move backward
        return attemptMove(fromRow, fromCol, -rollResult, false);
    }

    private boolean attemptMove(int fromRow, int fromCol, int moveAmount, boolean isForward) {
        int toRow = fromRow;
        int toCol = fromCol + moveAmount;

        if (toCol < 0) {
            toRow -= 1 + Math.abs(toCol) / SenetBoard.HOUSES_PER_ROW;
            toCol = SenetBoard.HOUSES_PER_ROW - 1 - (Math.abs(toCol) % SenetBoard.HOUSES_PER_ROW);
        } else if (toCol >= SenetBoard.HOUSES_PER_ROW) {
            toRow += toCol / SenetBoard.HOUSES_PER_ROW;
            toCol %= SenetBoard.HOUSES_PER_ROW;
        }

        if (toRow < 0 || toRow >= SenetBoard.NUM_ROWS) {
            System.out.println("Move out of bounds: (" + toRow + ", " + toCol + ")");
            return false;
        }

        Piece movingPiece = board.getPieceAt(fromRow, fromCol);
        Piece targetPiece = board.getPieceAt(toRow, toCol);

        if (targetPiece == null) {
            if (!canPassThreeConsecutiveEnemies(movingPiece, fromRow, fromCol, toRow, toCol)) {
                System.out.println("Cannot pass three consecutive enemies.");
                return false;
            }
            board.setPieceAt(toRow, toCol, movingPiece); // Place the piece at the new position
            board.setPieceAt(fromRow, fromCol, null); // Clear the old position
            System.out.println("Moved piece from (" + fromRow + ", " + fromCol + ") to (" + toRow + ", " + toCol + ")");
            return true;
        } else if (!targetPiece.getOwnerColor().equals(movingPiece.getOwnerColor())) {
            if (!isProtectedPiece(targetPiece, toRow, toCol) && canSwapPiece(fromRow, fromCol, toRow, toCol)) {
                // Swap the pieces
                board.setPieceAt(fromRow, fromCol, targetPiece); // Move the target piece to the original position
                board.setPieceAt(toRow, toCol, movingPiece); // Place the moving piece at the target position
                System.out.println("Swapped pieces between (" + fromRow + ", " + fromCol + ") and (" + toRow + ", " + toCol + ")");
                return true;
            }
        }

        System.out.println("Move failed from (" + fromRow + ", " + fromCol + ") to (" + toRow + ", " + toCol + ")");
        return false;
    }



    public boolean canSwapPiece(int fromRow, int fromCol, int toRow, int toCol) {
        // Get the piece at the destination
        Piece targetPiece = board.getPieceAt(toRow, toCol);

        // Get the piece at the source location
        Piece sourcePiece = board.getPieceAt(fromRow, fromCol);

        // Check if the target piece is not null and belongs to the opponent
        return targetPiece != null && !targetPiece.getOwnerColor().equals(sourcePiece.getOwnerColor());
    }


    private boolean isProtectedPiece(Piece piece, int row, int col) {
        // Handle special cases first
        if (row * SenetBoard.HOUSES_PER_ROW + col == 25) {
            return true; // House 25 is always protected
        }
        if (row * SenetBoard.HOUSES_PER_ROW + col >= 27) {
            return false; // Houses 27, 28, and 29 are never protected
        }

        String color = piece.getOwnerColor();
        int currentBox = row * SenetBoard.HOUSES_PER_ROW + col;
        int leftBox = currentBox - 1;
        int rightBox = currentBox + 1;

        if (leftBox >= 0 && board.getPieceAt(leftBox / SenetBoard.HOUSES_PER_ROW, leftBox % SenetBoard.HOUSES_PER_ROW) != null &&
                board.getPieceAt(leftBox / SenetBoard.HOUSES_PER_ROW, leftBox % SenetBoard.HOUSES_PER_ROW).getOwnerColor().equals(color)) {
            return true;
        }

        if (rightBox < SenetBoard.NUM_ROWS * SenetBoard.HOUSES_PER_ROW && board.getPieceAt(rightBox / SenetBoard.HOUSES_PER_ROW, rightBox % SenetBoard.HOUSES_PER_ROW) != null &&
                board.getPieceAt(rightBox / SenetBoard.HOUSES_PER_ROW, rightBox % SenetBoard.HOUSES_PER_ROW).getOwnerColor().equals(color)) {
            return true;
        }

        return false;
    }

    private boolean canPassThreeConsecutiveEnemies(Piece piece, int fromRow, int fromCol, int toRow, int toCol) {
        int fromBox = fromRow * SenetBoard.HOUSES_PER_ROW + fromCol;
        int toBox = toRow * SenetBoard.HOUSES_PER_ROW + toCol;
        String opponentColor = piece.getOwnerColor().equals(Player.WHITE) ? Player.BLACK : Player.WHITE;

        if (toBox > fromBox) {
            return !weCannotPassThreeOpponentsForward(fromBox, toBox, opponentColor);
        } else {
            return !weCannotPassThreeOpponentsBackward(fromBox, toBox, opponentColor);
        }
    }

    private boolean weCannotPassThreeOpponentsForward(int startBox, int endBox, String opponentColor) {
        if (endBox - startBox <= 3) return false; // Not enough space to be blocked by three

        for (int i = startBox + 2; i <= endBox - 2; i++) {
            int row = i / SenetBoard.HOUSES_PER_ROW;
            int col = i % SenetBoard.HOUSES_PER_ROW;

            // Ensure col is within bounds for previous and next cells
            String colorAtCurrent = board.getBoxContent(row, col);
            String colorAtPrevious = (col > 0) ? board.getBoxContent(row, col - 1) : null;
            String colorAtNext = (col < SenetBoard.HOUSES_PER_ROW - 1) ? board.getBoxContent(row, col + 1) : null;

            // Check if the current, previous, and next cells contain the opponent's color
            if (colorAtCurrent != null && colorAtPrevious != null && colorAtNext != null &&
                    colorAtCurrent.equals(opponentColor) &&
                    colorAtPrevious.equals(opponentColor) &&
                    colorAtNext.equals(opponentColor)) {
                return true;
            }
        }
        return false;
    }

    private boolean weCannotPassThreeOpponentsBackward(int startBox, int endBox, String opponentColor) {
        if (startBox - endBox <= 3) return false;

        for (int i = startBox - 2; i >= endBox + 2; i--) {
            int row = i / SenetBoard.HOUSES_PER_ROW;
            int col = i % SenetBoard.HOUSES_PER_ROW;

            // Ensure col is within bounds for previous and next cells
            String colorAtCurrent = board.getBoxContent(row, col);
            String colorAtPrevious = (col > 0) ? board.getBoxContent(row, col - 1) : null;
            String colorAtNext = (col < SenetBoard.HOUSES_PER_ROW - 1) ? board.getBoxContent(row, col + 1) : null;

            // Check if the current, previous, and next cells contain the opponent's color
            if (colorAtCurrent != null && colorAtPrevious != null && colorAtNext != null &&
                    colorAtCurrent.equals(opponentColor) &&
                    colorAtPrevious.equals(opponentColor) &&
                    colorAtNext.equals(opponentColor)) {
                return true;
            }
        }
        return false;
    }

    public void performComputerMove() {
        // Implement the logic for the computer to perform its move
        int rollResult = rollDice();
        System.out.println("Computer rolled: " + rollResult);

        // Add logic to select and move a piece for the computer based on the roll result
        boolean moveMade = false;

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
