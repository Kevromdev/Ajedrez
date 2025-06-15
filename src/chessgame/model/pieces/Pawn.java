package chessgame.model.pieces;

import chessgame.model.*;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {
    public Pawn(Color color, Position position) {
        super(color, position);
    }

    @Override
    public char getSymbol() {
        // Unicode: ♙ (blanco), ♟ (negro)
        return color == Color.WHITE ? '\u2659' : '\u265F';
    }

    @Override
    public List<Position> getLegalMoves(Board board, boolean attackOnly) {
        List<Position> moves = new ArrayList<>();
        int dir = (color == Color.WHITE) ? -1 : 1;
        int row = position.getRow();
        int col = position.getCol();

        if (!attackOnly) {
            // Movimiento hacia adelante
            if (board.isValidPosition(row + dir, col) && board.getPieceAt(row + dir, col) == null) {
                moves.add(new Position(row + dir, col));
                if (!moved && board.getPieceAt(row + 2*dir, col) == null)
                    moves.add(new Position(row + 2*dir, col));
            }
        }
        // Captura diagonal y comer al paso (o ataque en modo attackOnly)
        for (int dcol = -1; dcol <= 1; dcol += 2) {
            int nc = col + dcol;
            int nr = row + dir;
            if (board.isValidPosition(nr, nc)) {
                if (attackOnly) {
                    moves.add(new Position(nr, nc));
                } else {
                    IPiece target = board.getPieceAt(nr, nc);
                    if (target != null && target.getColor() != color)
                        moves.add(new Position(nr, nc));
                    if (board.isEnPassant(position, new Position(nr, nc)))
                        moves.add(new Position(nr, nc));
                }
            }
        }
        return moves;
    }

    @Override
    public IPiece deepCopy() {
        Pawn p = new Pawn(color, new Position(position.getRow(), position.getCol()));
        p.moved = this.moved;
        return p;
    }
}