package components;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private List<Piece> pieces;
    private String name;

    public Player(String name) {
        this.name = name;
        pieces = new ArrayList<>();
        // Initialize pieces if needed
    }

    // Getters and Setters
    public List<Piece> getPieces() {
        return pieces;
    }

    public void setPieces(List<Piece> pieces) {
        this.pieces = pieces;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Other methods can be added as needed
}
