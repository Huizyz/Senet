package components;

public class Piece {
    private int position;
    private String ownerColor;

    public Piece(String ownerColor) {
        this.ownerColor = ownerColor;
        this.position = -1; // Off the board initially (may cause problems down the line)
    }

    public String getOwnerColor() {
        return ownerColor;
    }

    public String getImagePath() {
        // Assuming images are in a folder named "assets" in the project root
        if (ownerColor.equalsIgnoreCase(Player.WHITE)) {
            return "Assets/images/white.png";
        } else if (ownerColor.equalsIgnoreCase(Player.BLACK)) {
            return "Assets/images/black.png";
        } else {
            return null; // Handle other cases or errors
        }
    }

    public String getSymbol() {
        if (ownerColor.equalsIgnoreCase(Player.WHITE)) {
            return "W";
        } else if (ownerColor.equalsIgnoreCase(Player.BLACK)) {
            return "B";
        } else {
            return "-";
        }
    }
}
