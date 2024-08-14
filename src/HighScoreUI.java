import components.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HighScoreUI extends JPanel {

    public HighScoreUI() {
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Top 5 High Scores", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        JPanel scoresPanel = new JPanel();
        scoresPanel.setLayout(new GridLayout(6, 1)); // 5 scores + title
        List<Player.PlayerScore> scores = HighScoreManager.loadHighScores();
        for (Player.PlayerScore score : scores) {
            JLabel scoreLabel = new JLabel(score.getPlayerName() + ": " + score.getScore() + " wins", SwingConstants.CENTER);
            scoresPanel.add(scoreLabel);
        }
        add(scoresPanel, BorderLayout.CENTER);
    }

}
