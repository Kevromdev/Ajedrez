package chessgame.model.pieces;

import chessgame.model.*;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    public Queen(Color color, Position position) {
        super(color, position);
    }

    @Override
    public char getSymbol() {
        // Unicode: ♕ (blanco), ♛ (negro)
        return color == Color.WHITE ? '\u2655' : '\u265B';
    }

    @Override
    public List<Position> getLegalMoves(Board board, boolean attackOnly) {
        List<Position> moves = new ArrayList<>();
        moves.addAll(board.getMovesInDirections(this, new int[][]{
            {-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}
        }, true));
        return moves;
    }

    @Override
    public IPiece deepCopy() {
        Queen q = new Queen(color, new Position(position.getRow(), position.getCol()));
        q.moved = this.moved;
        return q;
    }
}