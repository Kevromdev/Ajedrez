package chessgame.model.pieces;

import chessgame.model.*;

import java.util.List;

public class Rook extends Piece {
    public Rook(Color color, Position position) {
        super(color, position);
    }

    @Override
    public char getSymbol() {
        // Unicode: ♖ (blanco), ♜ (negro)
        return color == Color.WHITE ? '\u2656' : '\u265C';
    }

    @Override
    public List<Position> getLegalMoves(Board board, boolean attackOnly) {
        return board.getMovesInDirections(this, new int[][]{
            {-1,0},{1,0},{0,-1},{0,1}
        }, true);
    }

    @Override
    public IPiece deepCopy() {
        Rook r = new Rook(color, new Position(position.getRow(), position.getCol()));
        r.moved = this.moved;
        return r;
    }
}