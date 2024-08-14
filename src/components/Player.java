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
    private int wins;

    // Static list to keep track of all players' wins
    private static final List<PlayerScore> highScores = new ArrayList<>();

    public Player(String name) {
        this.name = name;
        this.pieces = new ArrayList<>();
        this.wins = 0; // Initialize score
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWins() {
        return wins;
    }

    // Method to update the number of wins for a player
    public void incrementWins() {
        this.wins++;
        updateHighScores(this);
    }

    public static void updateHighScores(Player player) {
        // Load existing high scores
        List<PlayerScore> existingHighScores = HighScoreManager.loadHighScores();

        // Remove the player if they already exist in the list
        existingHighScores.removeIf(ps -> ps.getPlayerName().equals(player.getName()));

        // Add new win count
        existingHighScores.add(new PlayerScore(player.getName(), player.getWins()));

        // Sort high scores in descending order
        Collections.sort(existingHighScores, Comparator.comparingInt(PlayerScore::getScore).reversed());

        // Keep only the top 5 scores
        if (existingHighScores.size() > 5) {
            existingHighScores.subList(5, existingHighScores.size()).clear();
        }

        // Save updated high scores
        HighScoreManager.saveHighScores(existingHighScores);
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
