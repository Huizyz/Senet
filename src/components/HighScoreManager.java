package components;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HighScoreManager {

    private static final String FILE_PATH = "src/highscores.txt";

    public static void saveHighScores(List<Player.PlayerScore> highScores) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Player.PlayerScore score : highScores) {
                String line = score.getPlayerName() + ":" + score.getScore();
                writer.write(EncryptionUtils.encrypt(line));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Player.PlayerScore> loadHighScores() {
        List<Player.PlayerScore> highScores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String decryptedLine = EncryptionUtils.decrypt(line);
                String[] parts = decryptedLine.split(":");
                if (parts.length == 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    highScores.add(new Player.PlayerScore(name, score));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return highScores;
    }
}
