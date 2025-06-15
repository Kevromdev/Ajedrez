package chessgame.model;

import java.io.Serializable;
import java.util.Objects;

public class Position implements Serializable {
    private final int row; // 0 to 7
    private final int col; // 0 to 7

    public Position(int row, int col) {
        if(row < 0 || row > 7 || col < 0 || col > 7)
            throw new IllegalArgumentException("Posición fuera de rango");
        this.row = row;
        this.col = col;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    // Conversión entre notación "a1" y posición
    public static Position fromChessCoords(String coord) {
        if (coord.length() != 2)
            throw new IllegalArgumentException("Coordenada inválida: " + coord);
        int col = coord.charAt(0) - 'a';
        int row = 8 - (coord.charAt(1) - '0');
        return new Position(row, col);
    }

    public String toChessCoords() {
        return "" + (char)('a' + col) + (8 - row);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position position)) return false;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}