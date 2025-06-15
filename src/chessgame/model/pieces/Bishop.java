package chessgame.model.pieces;

import chessgame.model.*;

import java.util.List;

public class Bishop extends Piece {
    public Bishop(Color color, Position position) {
        super(color, position);
    }

    @Override
    public char getSymbol() {
        // Unicode: ♗ (blanco), ♝ (negro)
        return color == Color.WHITE ? '\u2657' : '\u265D';
    }

    @Override
    public List<Position> getLegalMoves(Board board, boolean attackOnly) {
        return board.getMovesInDirections(this, new int[][]{
            {-1,-1},{-1,1},{1,-1},{1,1}
        }, true);
    }

    @Override
    public IPiece deepCopy() {
        Bishop b = new Bishop(color, new Position(position.getRow(), position.getCol()));
        b.moved = this.moved;
        return b;
    }
}