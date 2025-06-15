package chessgame.model.pieces;

import chessgame.model.*;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    public Knight(Color color, Position position) {
        super(color, position);
    }

    @Override
    public char getSymbol() {
        // Unicode: ♘ (blanco), ♞ (negro)
        return color == Color.WHITE ? '\u2658' : '\u265E';
    }

    @Override
    public List<Position> getLegalMoves(Board board, boolean attackOnly) {
        List<Position> moves = new ArrayList<>();
        int[][] steps = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
        for (int[] s : steps) {
            int nr = position.getRow() + s[0];
            int nc = position.getCol() + s[1];
            if (board.isValidPosition(nr, nc)) {
                IPiece p = board.getPieceAt(nr, nc);
                if (p == null || p.getColor() != color)
                    moves.add(new Position(nr, nc));
            }
        }
        return moves;
    }

    @Override
    public IPiece deepCopy() {
        Knight k = new Knight(color, new Position(position.getRow(), position.getCol()));
        k.moved = this.moved;
        return k;
    }
}