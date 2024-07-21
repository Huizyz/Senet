import components.*;

public class ConsoleUI {
    private SenetGame game;

    public ConsoleUI(SenetGame game) {
        this.game = game;
    }

    public void start() {
        // Implement console-based interaction
        displayBoard();
    }

    public void displayBoard() {
        SenetBoard board = game.getBoard();

        // Display top row (row 1): Houses 0 to 9
        for (int i = 0; i < 10; i++) {
            Piece piece = board.getPieceAt(0, i);
            String pieceSymbol = (piece != null) ? piece.getSymbol() : "-";
            System.out.print(pieceSymbol + " ");
        }
        System.out.println(); // Move to the next line

        // Display middle row (row 2): Houses 19 to 10
        for (int i = 9; i >= 0; i--) {
            Piece piece = board.getPieceAt(1, i);
            String pieceSymbol = (piece != null) ? piece.getSymbol() : "-";
            System.out.print(pieceSymbol + " ");
        }
        System.out.println(); // Move to the next line

        // Display bottom row (row 3): Houses 20 to 29
        for (int i = 0; i < 10; i++) {
            Piece piece = board.getPieceAt(2, i);
            String pieceSymbol = (piece != null) ? piece.getSymbol() : "-";
            System.out.print(pieceSymbol + " ");
        }
        System.out.println(); // Move to the next line

        // Display house numbers
        for (int i = 0; i < 10; i++) {
            System.out.print(i + " ");
        }
        System.out.println(); // Move to the next line

        for (int i = 19; i >= 10; i--) {
            System.out.print(i + " ");
        }
        System.out.println(); // Move to the next line

        for (int i = 20; i < 30; i++) {
            System.out.print(i + " ");
        }
        System.out.println(); // Move to the next line
    }
}
