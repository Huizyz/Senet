import javax.swing.*;

public class SenetMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SenetGame game = new SenetGame();
            GameUI gameUI = new GameUI(game);
            gameUI.setVisible(true);
        });
    }
}
