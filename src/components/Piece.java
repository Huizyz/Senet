package components;

public class Piece {
    private int position;
    private Player owner;
    private String color; // Add color attribute (e.g., "White" or "Black")

    public Piece(Player owner, String color) {
        this.owner = owner;
        this.color = color;
        position = -1; // Off the board initially
    }

    public String getColor() {
        return color;
    }

    public String getImagePath() {
        // Assuming images are in a folder named "assets" in the project root
        if (color.equalsIgnoreCase("White")) {
            return "Assets/images/white.png";
        } else if (color.equalsIgnoreCase("Black")) {
            return "Assets/images/black.png";
        } else {
            return null; // Handle other cases or errors
        }
    }
}
