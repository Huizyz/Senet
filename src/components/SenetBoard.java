package components;

public class SenetBoard {
    public static final int NUM_ROWS = 3;
    public static final int HOUSES_PER_ROW = 10;
    public static final int NUM_SQUARES = NUM_ROWS * HOUSES_PER_ROW;

    private String[][] houses;
    private Piece[][] pieces;

    public SenetBoard() {
        houses = new String[NUM_ROWS][HOUSES_PER_ROW];
        pieces = new Piece[NUM_ROWS][HOUSES_PER_ROW];

        initializeBoard();
        initializePieces();
    }

    public void initializeBoard() {
        // Initialize all houses with default labels or special names
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < HOUSES_PER_ROW; col++) {
                houses[row][col] = "House " + (row * HOUSES_PER_ROW + col);
            }
        }

        // Assign specific names to special houses
        houses[0][5] = "The House of Second Life";      // House 14
        houses[1][5] = "The Good House";                // House 25
        houses[2][6] = "The House of Water";            // House 26
        houses[2][7] = "The House of the Three Judges"; // House 27
        houses[2][8] = "The House of the Two Judges";   // House 28
        houses[2][9] = "The House of Horus";            // House 29
    }

    public void initializePieces() {
        // Initialize pieces on the board according to the specified layout
        pieces[0][0] = new Piece(Player.WHITE);
        pieces[0][2] = new Piece(Player.WHITE);
        pieces[0][4] = new Piece(Player.WHITE);
        pieces[0][6] = new Piece(Player.WHITE);
        pieces[0][8] = new Piece(Player.WHITE);

        pieces[0][1] = new Piece(Player.BLACK);
        pieces[0][3] = new Piece(Player.BLACK);
        pieces[0][5] = new Piece(Player.BLACK);
        pieces[0][7] = new Piece(Player.BLACK);
        pieces[0][9] = new Piece(Player.BLACK);
    }

    // Method to get the label of a specific house
    public String getHouseLabel(int row, int col) {
        if (row >= 0 && row < NUM_ROWS && col >= 0 && col < HOUSES_PER_ROW) {
            return houses[row][col];
        } else {
            throw new IllegalArgumentException("House index out of bounds.");
        }
    }

    // Method to get the piece at a specific house
    public Piece getPieceAt(int row, int col) {
        if (row >= 0 && row < NUM_ROWS && col >= 0 && col < HOUSES_PER_ROW) {
            return pieces[row][col];
        } else {
            throw new IllegalArgumentException("House index out of bounds.");
        }
    }

    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        Piece movingPiece = pieces[fromRow][fromCol];
        Piece targetPiece = pieces[toRow][toCol];

        // If moving to an enemy square, swap the pieces
        if (targetPiece != null && !targetPiece.getOwnerColor().equals(movingPiece.getOwnerColor())) {
            pieces[fromRow][fromCol] = targetPiece;
            pieces[toRow][toCol] = movingPiece;
        } else {
            // Normal move
            pieces[toRow][toCol] = movingPiece;
            pieces[fromRow][fromCol] = null;
        }

        // Handle special rules for hazardous squares
        handleHazardousSquares(movingPiece, toRow, toCol);
    }

    private void handleHazardousSquares(Piece piece, int toRow, int toCol) {
        int houseIndex = toRow * HOUSES_PER_ROW + toCol;
        switch (houseIndex) {
            case 26: // House of Water
                // Move back to house 15
                pieces[toRow][toCol] = null;
                pieces[1][4] = piece;
                break;
            case 27: // The House of the Three Judges
            case 28: // The House of the Two Judges
            case 29: // The House of Horus
                // These pieces are never protected and must move back if necessary
                // This logic needs to be handled in isValidMove and during game turn checks
                break;
        }
    }

    public boolean isValidMove(Piece piece, int fromRow, int fromCol, int toRow, int toCol, int rollResult) {
        // Check if target square is within bounds
        if (toRow < 0 || toRow >= NUM_ROWS || toCol < 0 || toCol >= HOUSES_PER_ROW) {
            return false;
        }

        // Check if the move is forward
        if (toRow * HOUSES_PER_ROW + toCol < fromRow * HOUSES_PER_ROW + fromCol) {
            return false;
        }

        // Check if the move is the correct number of spaces
        int distance = (toRow * HOUSES_PER_ROW + toCol) - (fromRow * HOUSES_PER_ROW + fromCol);
        if (distance != rollResult) {
            return false;
        }

        Piece targetPiece = pieces[toRow][toCol];

        // Check if moving to a square occupied by a piece of the same player
        if (targetPiece != null && targetPiece.getOwnerColor().equals(piece.getOwnerColor())) {
            return false;
        }

        // Check if the target piece is protected (two pieces of the same color on consecutive squares)
        if (targetPiece != null && isProtected(toRow, toCol)) {
            return false;
        }

        // Check if moving beyond three consecutive enemy pieces
        if (isBlockedByEnemies(fromRow, fromCol, toRow, toCol)) {
            return false;
        }

        // Additional checks for hazardous squares
        if (isHazardousSquare(toRow, toCol) && !canExitHazard(piece, toRow, toCol, rollResult)) {
            return false;
        }

        return true;
    }

    private boolean isProtected(int row, int col) {
        String color = pieces[row][col].getOwnerColor();
        return (row > 0 && pieces[row - 1][col] != null && pieces[row - 1][col].getOwnerColor().equals(color)) ||
                (row < NUM_ROWS - 1 && pieces[row + 1][col] != null && pieces[row + 1][col].getOwnerColor().equals(color)) ||
                (col > 0 && pieces[row][col - 1] != null && pieces[row][col - 1].getOwnerColor().equals(color)) ||
                (col < HOUSES_PER_ROW - 1 && pieces[row][col + 1] != null && pieces[row][col + 1].getOwnerColor().equals(color));
    }

    private boolean isBlockedByEnemies(int fromRow, int fromCol, int toRow, int toCol) {
        int step = (toRow * HOUSES_PER_ROW + toCol > fromRow * HOUSES_PER_ROW + fromCol) ? 1 : -1;
        for (int i = fromRow * HOUSES_PER_ROW + fromCol + step; i != toRow * HOUSES_PER_ROW + toCol; i += step) {
            int row = i / HOUSES_PER_ROW;
            int col = i % HOUSES_PER_ROW;
            if (pieces[row][col] != null && !pieces[row][col].getOwnerColor().equals(pieces[fromRow][fromCol].getOwnerColor())) {
                // Check if blocked by three consecutive enemy pieces
                if (isBlockedByThreeEnemies(row, col)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isBlockedByThreeEnemies(int row, int col) {
        String enemyColor = pieces[row][col].getOwnerColor();
        return (row > 1 && pieces[row - 1][col] != null && pieces[row - 1][col].getOwnerColor().equals(enemyColor) &&
                pieces[row - 2][col] != null && pieces[row - 2][col].getOwnerColor().equals(enemyColor)) ||
                (row < NUM_ROWS - 2 && pieces[row + 1][col] != null && pieces[row + 1][col].getOwnerColor().equals(enemyColor) &&
                        pieces[row + 2][col] != null && pieces[row + 2][col].getOwnerColor().equals(enemyColor)) ||
                (col > 1 && pieces[row][col - 1] != null && pieces[row][col - 1].getOwnerColor().equals(enemyColor) &&
                        pieces[row][col - 2] != null && pieces[row][col - 2].getOwnerColor().equals(enemyColor)) ||
                (col < HOUSES_PER_ROW - 2 && pieces[row][col + 1] != null && pieces[row][col + 1].getOwnerColor().equals(enemyColor) &&
                        pieces[row][col + 2] != null && pieces[row][col + 2].getOwnerColor().equals(enemyColor));
    }

    private boolean isHazardousSquare(int row, int col) {
        int houseIndex = row * HOUSES_PER_ROW + col;
        return houseIndex >= 25 && houseIndex <= 29;
    }

    private boolean canExitHazard(Piece piece, int row, int col, int rollResult) {
        int houseIndex = row * HOUSES_PER_ROW + col;
        switch (houseIndex) {
            case 25:
                // Check if exact throw to land or a score of 5 to exit
                return rollResult == 5;
            case 26:
                // Move back to house 15
                return true;
            case 27:
                return rollResult == 3;
            case 28:
                return rollResult == 2;
            case 29:
                return rollResult == 1;
            default:
                return false;
        }
    }
}
