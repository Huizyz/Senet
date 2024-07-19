package components;

import java.util.Random;

public class ComputerPlayer {
    private Random random;

    public ComputerPlayer() {
        random = new Random();
    }

    public void makeMove(SenetBoard board) {
        // Implement computer's move logic here
        // Example: Random move
        int move = random.nextInt(SenetBoard.NUM_SQUARES);
        System.out.println("Computer moves to square " + move);
        // Update board state based on move
    }
}
