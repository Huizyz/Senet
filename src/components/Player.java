package components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Player {
    public static final String WHITE = "White";
    public static final String BLACK = "Black";
    private List<Piece> pieces;
    private String name;
    private int score;
    // New field to track the score or relevant metric for high scores

    // Static list to keep track of all players' high scores
    private static final List<PlayerScore> highScores = new ArrayList<>();

    public Player(String name) {
        this.name = name;
        this.pieces = new ArrayList<>();
        this.score = 0; // Initialize score
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    // Method to update the high scores list
    public static void updateHighScores(Player player) {
        // Add new score
        highScores.add(new PlayerScore(player.getName(), player.getScore()));

        // Sort high scores in descending order
        Collections.sort(highScores, Comparator.comparingInt(PlayerScore::getScore).reversed());

        // Keep only the top 5 scores
        if (highScores.size() > 5) {
            highScores.subList(5, highScores.size()).clear();
        }
    }

    // Method to get high scores
    public static List<PlayerScore> getHighScores() {
        return highScores;
    }

    // Inner class to represent a player's score
    static class PlayerScore {
        private final String playerName;
        private final int score;

        public PlayerScore(String playerName, int score) {
            this.playerName = playerName;
            this.score = score;
        }

        public String getPlayerName() {
            return playerName;
        }

        public int getScore() {
            return score;
        }
    }
}
