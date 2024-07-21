package components;

import java.util.Random;

public class Dice {
    private Random random;

    public Dice() {
        random = new Random();
    }

    public int roll() {
        // Roll 4 sticks
        int whiteFaces = 0;
        for (int i = 0; i < 4; i++) {
            boolean isWhite = random.nextBoolean();
            if (isWhite) {
                whiteFaces++;
            }
        }

        // Determine the result based on the number of white faces
        return switch (whiteFaces) {
            case 0 -> 5;
            case 1 -> 1;
            case 2 -> 2;
            case 3 -> 3;
            case 4 -> 4;
            default -> 0;
        };
    }
}
