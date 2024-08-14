import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private SplashScreen splashScreen;
    private JPanel gameScreenPanel;
    private SenetGame senetGame;
    private GameUI gameUI;

    public MainApp() {
        setTitle("Game Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize screens
        splashScreen = new SplashScreen(this);
        HighScoreUI highScoreUI = new HighScoreUI();
        HelpScreen helpScreen = new HelpScreen();

        // Add screens to main panel
        mainPanel.add(splashScreen, "SplashScreen");
        mainPanel.add(highScoreUI, "HighScoreUI");
        mainPanel.add(helpScreen, "HelpScreen");
        // Game screen panel will be initialized later
        mainPanel.add(new JPanel(), "GameScreen"); // Placeholder panel

        // Add main panel to frame
        getContentPane().add(mainPanel);

        // Display splash screen initially
        cardLayout.show(mainPanel, "SplashScreen");

        // Setup menu bar
        setupMenuBar();
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Menu");
        JMenuItem splashItem = new JMenuItem("Splash Screen");
        splashItem.addActionListener(e -> cardLayout.show(mainPanel, "SplashScreen"));

        JMenuItem gameItem = new JMenuItem("Game");
        gameItem.addActionListener(e -> cardLayout.show(mainPanel, "GameScreen"));

        JMenuItem highScoresItem = new JMenuItem("High Scores");
        highScoresItem.addActionListener(e -> cardLayout.show(mainPanel, "HighScoreUI"));

        JMenuItem helpItem = new JMenuItem("Help");
        helpItem.addActionListener(e -> cardLayout.show(mainPanel, "HelpScreen"));

        menu.add(splashItem);
        menu.add(gameItem);
        menu.add(highScoresItem);
        menu.add(helpItem);

        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    public void startGame() {
        // Initialize SenetGame and GameUI
        senetGame = new SenetGame();
        gameUI = new GameUI(senetGame);

        // Replace the placeholder panel with the actual game UI
        mainPanel.add(gameUI, "GameScreen");

        // Show the game screen
        cardLayout.show(mainPanel, "GameScreen");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.setVisible(true);
        });
    }
}
