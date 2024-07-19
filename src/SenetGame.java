import components.*;

import java.util.Scanner;

public class SenetGame {
    private String playerName;
    private boolean playerStarts;
    private SenetBoard board;
    private ComputerPlayer computerPlayer;
    private ConsoleUI consoleUI;
    private GameUI gameUI;

    public SenetGame(String playerName, boolean playerStarts) {
        this.playerName = playerName;
        this.playerStarts = playerStarts;
        this.board = new SenetBoard();
        this.computerPlayer = new ComputerPlayer();
        this.consoleUI = new ConsoleUI(this); // Pass SenetGame instance to ConsoleUI
        this.gameUI = new GameUI(this);             // Pass SenetGame instance to GameUI
    }

    public void startGame() {
        System.out.println("Welcome to Senet, " + playerName + "!");
        System.out.println("Let's begin the game.");

        if (playerStarts) {
            playPlayerTurn();
        } else {
            playComputerTurn();
        }
    }

    private void playPlayerTurn() {
        gameUI.updateBoardDisplay(); // Update board display before player's turn
        System.out.println("Your turn:");

        // Roll the dice and display result
        gameUI.rollDiceAndDisplay();

        // Implement player's turn logic here
        // Example: Move pieces, handle input, etc.
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your move (house number): ");
        int move = scanner.nextInt();

        // Update board state based on player's move
        // Example: Move player's piece to the specified house
        board.movePiece(Player.WHITE, move);

        // Check game status or next turn
        // Example: Check if player wins, or proceed to computer's turn
        // For now, proceed to computer's turn
        playComputerTurn();
    }

    private void playComputerTurn() {
        gameUI.updateBoardDisplay(); // Update board display before computer's turn
        System.out.println("Computer's turn:");

        // Implement computer's turn logic here
        computerPlayer.makeMove(board);

        // Check game status or next turn
        // Example: Check if computer wins, or proceed to player's turn
        // For now, proceed to player's turn
        playPlayerTurn();
    }

    // Method to check if the game is over (example)
    public boolean isGameOver() {
        // Implement game over condition based on Senet rules
        // For example, if a player reaches the last house, they win
        return false; // Placeholder
    }

    // Method to get the current board state
    public SenetBoard getBoard() {
        return board;
    }

    // Method to start the game
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ask for player's name
        System.out.print("Enter your name: ");
        String playerName = scanner.nextLine();

        // Ask player if they want to start or let computer start
        System.out.println("Do you want to start first? (Y/N)");
        String startChoice = scanner.nextLine().toUpperCase();
        boolean playerStarts = startChoice.equals("Y");

        // Initialize and start the game
        SenetGame senetGame = new SenetGame(playerName, playerStarts);
        senetGame.startGame();
    }
}
