package components;

public class Piece {
    private String ownerColor;

    public Piece(String ownerColor) {
        this.ownerColor = ownerColor;
    }

    public String getOwnerColor() {
        return ownerColor;
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
