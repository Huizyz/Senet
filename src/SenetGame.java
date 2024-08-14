import components.*;

import javax.swing.*;
import java.awt.*;

public class SenetGame extends Component {
    private SenetBoard board;
    private GameUI gameUI;
    private Player player;
    private Dice dice;
    private boolean isPlayerTurn;
    boolean hasRolledDice;
    private int whitePlayerFinishedPieces;
    private int blackPlayerFinishedPieces;
    private final int totalWhitePieces = 5; // Total number of pieces for each player
    private final int totalBlackPieces = 5;
    private boolean canNotifyExtraRoll = true;
    private String playerName;

    public SenetGame() {
        board = new SenetBoard();
        dice = new Dice();
        hasRolledDice = false; // Initialize the flag
        whitePlayerFinishedPieces = 0; // Initialize counters
        blackPlayerFinishedPieces = 0;
    }

    public void startGame() {
        board.clearBoard();
        board.initializeBoard();
        board.initializePieces();
        hasRolledDice = false; // Reset the flag
        whitePlayerFinishedPieces = 0; // Reset counters
        blackPlayerFinishedPieces = 0;
        canNotifyExtraRoll = true; // Enable notifications for the new game
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
        canNotifyExtraRoll = true;
        return dice.roll();
    }

    public boolean movePiece(int fromRow, int fromCol, int rollResult) {
        // Check if the player has rolled the dice before attempting to move
        if (!hasRolledDice) {
            System.out.println("You must roll the dice before making a move.");
            return false; // Prevent the move
        }

        Piece piece = board.getPieceAt(fromRow, fromCol);
        if (piece == null) {
            hasRolledDice = false;
            return false;
        }

        // Attempt to move forward
        if (attemptMove(fromRow, fromCol, rollResult, true)) {
            // Reset the dice roll after a successful move
            hasRolledDice = false;
            return true;
        }

        // If forward move is not possible, attempt to move backward
        if (attemptMove(fromRow, fromCol, rollResult, false)) {
            // Reset the dice roll after a successful move
            hasRolledDice = false;
            // End the player's turn immediately after a backward move
            endPlayerTurn();
            return true;
        }

        return false;
    }

    void endPlayerTurn() {

        System.out.println("Turn ended. Switching to the opponent.");

        // Disable notifications for extra rolls
        canNotifyExtraRoll = false;
        // Reset dice roll status
        hasRolledDice = false;
    }

    void notifyExtraRoll() {
        if (canNotifyExtraRoll) {
            // Display notification for extra roll
            JOptionPane.showMessageDialog(null, "You get another turn!");
        }
    }

    private boolean attemptMove(int fromRow, int fromCol, int moveAmount, boolean isForward) {
        int toRow = fromRow;
        // Adjust direction based on isForward flag
        int toCol = fromCol + (isForward ? moveAmount : -moveAmount);

        // Adjust for crossing row boundaries
        while (toCol < 0) {
            toRow--;
            toCol += SenetBoard.HOUSES_PER_ROW;
        }
        while (toCol >= SenetBoard.HOUSES_PER_ROW) {
            toRow++;
            toCol -= SenetBoard.HOUSES_PER_ROW;
        }

        // Verify move is within board bounds
        if (toRow < 0 || toRow >= SenetBoard.NUM_ROWS) {
            System.out.println("Move out of bounds: (" + toRow + ", " + toCol + ")");
            return false;
        }

        // Piece movement logic
        Piece movingPiece = board.getPieceAt(fromRow, fromCol);
        if (movingPiece == null) {
            System.out.println("No piece at the starting position.");
            return false;
        }

        // Waterfall check: if moving backward from houses 27, 28, or 29
        if (!isForward) {
            waterFall(movingPiece, fromRow, fromCol, toRow, toCol);
        }

        // Continue with the rest of the movement logic...
        Piece targetPiece = board.getPieceAt(toRow, toCol);
        if (targetPiece == null) {
            // Check if the move is valid considering any special rules
            if (!canPassThreeConsecutiveEnemies(movingPiece, fromRow, fromCol, toRow, toCol)) {
                System.out.println("Cannot pass three consecutive enemies.");
                return false;
            }
            // Move piece to the new position
            board.setPieceAt(toRow, toCol, movingPiece);
            board.setPieceAt(fromRow, fromCol, null);
            System.out.println("Moved piece from (" + fromRow + ", " + fromCol + ") to (" + toRow + ", " + toCol + ")");
            return true;
        } else if (!targetPiece.getOwnerColor().equals(movingPiece.getOwnerColor())) {
            // Handle opponent's piece
            if (!isProtectedPiece(targetPiece, toRow, toCol) && canSwapPiece(fromRow, fromCol, toRow, toCol)) {
                board.setPieceAt(fromRow, fromCol, targetPiece);
                board.setPieceAt(toRow, toCol, movingPiece);
                System.out.println("Swapped pieces between (" + fromRow + ", " + fromCol + ") and (" + toRow + ", " + toCol + ")");
                return true;
            } else {
                System.out.println("Swap failed between (" + fromRow + ", " + fromCol + ") and (" + toRow + ", " + toCol + ") due to protection.");
            }
        }
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
        // Protected Piece: A piece is protected if it is located in a special house (House 25) or if there is at least one other friendly piece in the adjacent houses to the left or right.
        // Not Protected: If the piece is in Houses 27, 28, or 29, or if there are no adjacent friendly pieces, it is not protected.

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
        if (piece == null) {
            return true;
        }

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
        // Roll the dice for the computer
        int rollResult = rollDice();
        System.out.println();
        System.out.println("Computer rolled: " + rollResult);

        // Try to finish a piece first
        if (performComputerFinishMove(rollResult)) {
            // If a piece was finished, check if the game is over and switch turn
            checkGameOver();
            isPlayerTurn = true;
            hasRolledDice = false;
            return;
        }

        // Initialize a flag to check if a move was made
        boolean moveMade = false;

            // Try to find and perform a valid move for the computer
            for (int row = 0; row < SenetBoard.NUM_ROWS; row++) {
                for (int col = 0; col < SenetBoard.HOUSES_PER_ROW; col++) {
                    Piece piece = board.getPieceAt(row, col);
                    if (piece != null && piece.getOwnerColor().equals("Black")) {
                        // Attempt to make a move with this piece
                        if (movePiece(row, col, rollResult)) {
                            moveMade = true;
                            break; // exit the column loop if a move was made
                        }
                    }
                }
                if (moveMade){
                    break; // exit the row loop if a move was made
                }
            }

        // If no move was made, print a message
        if (!moveMade) {
            System.out.println("Computer could not make a move, passing turn to player.");
        }

        // Switch turn back to the player
        isPlayerTurn = true;
        hasRolledDice = false;
    }

    private boolean performComputerFinishMove(int rollResult) {
        boolean pieceFinished = false;

        // Iterate over the last row (row 2) and the last 5 columns
        int lastRow = 2;
        int[] finishableCols = {5, 6, 7, 8, 9}; // Columns where pieces can potentially finish

        for (int col : finishableCols) {
            Piece piece = board.getPieceAt(lastRow, col);
            if (piece != null && piece.getOwnerColor().equals("Black")) {
                if (canPieceFinish(piece, lastRow, col, rollResult)) {
                    // Move the piece out of the board
                    board.setPieceAt(lastRow, col, null);

                    // Update finished pieces count
                    incrementBlackFinishedPieces();
                    System.out.println("Black pieces finished: " + getBlackPlayerFinishedPieces());

                    // Update game UI
                    gameUI.updateBoardDisplay();

                    pieceFinished = true;
                    break; // Exit the loop once a piece is finished
                }
            }
        }

        return pieceFinished;
    }

    boolean canPieceFinish(Piece piece, int row, int col, int rollResult) {
        // Implement the finishing conditions
        if (piece == null) return false;

        if (row == 2 && col == 9 && rollResult == 1) return true; // House 29
        if (row == 2 && col == 8 && rollResult == 2) return true; // House 28
        if (row == 2 && col == 7 && rollResult == 3) return true; // House 27
        if (row == 2 && col == 5 && rollResult == 5) return true; // House 25

        return false;
    }

    // Add methods to increment finished pieces count
    public void incrementWhiteFinishedPieces() {
        whitePlayerFinishedPieces++;
    }

    public void incrementBlackFinishedPieces() {
        blackPlayerFinishedPieces++;
    }

    public int getWhitePlayerFinishedPieces() {
        return whitePlayerFinishedPieces;
    }

    public int getBlackPlayerFinishedPieces() {
        return blackPlayerFinishedPieces;
    }

    public void checkGameOver() {
        // Check if all pieces of one player have finished
        if (whitePlayerFinishedPieces == totalWhitePieces) {
            JOptionPane.showMessageDialog(null, "Player wins!");
            endGame();
        } else if (blackPlayerFinishedPieces == totalBlackPieces) {
            JOptionPane.showMessageDialog(null, "Computer wins!");
            endGame();
        }
    }

    public void waterFall(Piece piece, int fromRow, int fromCol, int toRow, int toCol) {
        int fromBox = fromRow * SenetBoard.HOUSES_PER_ROW + fromCol;
        int toBox = toRow * SenetBoard.HOUSES_PER_ROW + toCol;

        // Check if moving backward from squares 27, 28, or 29
        if ((fromBox >= 27 && fromBox <= 29) && toBox < fromBox) {
            // The piece falls into the water and returns to House 15
            System.out.println("Piece at (" + fromRow + ", " + fromCol + ") falls into the water and returns to House 15.");

            // Move the piece to House 15 (row 1, column 5)
            board.setPieceAt(fromRow, fromCol, null); // Remove piece from current location
            board.setPieceAt(1, 5, piece); // Place piece at House 15 (index 15)

            // Update the game UI to reflect this move
            gameUI.updateBoardDisplay();
        }
    }

    private void endGame() {
        // Implement end game logic, such as showing a message and offering a rematch
        int response = JOptionPane.showConfirmDialog(null, "Would you like to play again?", "Game Over", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            gameUI.startNewGame(); // Reset the game using startNewGame method
        } else {
            System.exit(0); // Close the game
        }
    }

    public void setPlayerName(String playerName) {
        if (player != null) {
            player.setName(playerName);
        }
    }

    public String getPlayerName() {
        return playerName;
    }
}
