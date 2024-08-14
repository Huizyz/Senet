import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JPanel {
    private MainApp app; // Reference to MainApp

    public SplashScreen(MainApp app) {
        this.app = app; // Store the reference
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Welcome to the Game!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 36));
        add(titleLabel, BorderLayout.CENTER);

        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(e -> app.startGame()); // Use the reference to call the method
        add(startButton, BorderLayout.SOUTH);
    }
}
