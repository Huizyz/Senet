import javax.swing.*;
import java.awt.*;

public class GameScreen extends JPanel {
    private GameUI gameUI;
    private SenetGame senetGame;

    public GameScreen(MainApp app) {
        setLayout(new BorderLayout());

        // Initialize SenetGame and GameUI
        senetGame = new SenetGame();
        gameUI = new GameUI(senetGame);

        // Add GameUI to this panel
        add(gameUI, BorderLayout.CENTER);
    }
}
