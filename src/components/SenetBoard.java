package components;

public class SenetBoard {
    public static final int NUM_ROWS = 3;
    public static final int HOUSES_PER_ROW = 10;
    public static final int NUM_SQUARES = NUM_ROWS * HOUSES_PER_ROW;

    private String[][] houses;
    private Piece[][] pieces; // Assuming Piece class exists and manages pieces on board

    public SenetBoard() {
        houses = new String[NUM_ROWS][HOUSES_PER_ROW];
        pieces = new Piece[NUM_ROWS][HOUSES_PER_ROW];

        initializeBoard();
        initializePieces();
    }

    private void initializeBoard() {
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

    private void initializePieces() {
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

    // Other methods for board management (e.g., move pieces, check state, etc.)
}
