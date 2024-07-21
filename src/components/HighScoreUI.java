package components;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HighScoreUI extends JFrame {
    public HighScoreUI() {
        setTitle("High Scores");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a panel to display high scores
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1)); // 5 scores + title

        // Add title
        JLabel title = new JLabel("Top 5 High Scores", SwingConstants.CENTER);
        panel.add(title);

        // Get high scores
        List<Player.PlayerScore> scores = Player.getHighScores();
        for (Player.PlayerScore score : scores) {
            JLabel scoreLabel = new JLabel(score.getPlayerName() + ": " + score.getScore(), SwingConstants.CENTER);
            panel.add(scoreLabel);
        }

        // Add panel to the frame
        getContentPane().add(panel);
    }

    public static void showHighScores() {
        SwingUtilities.invokeLater(() -> {
            new HighScoreUI().setVisible(true);
        });
    }
}
