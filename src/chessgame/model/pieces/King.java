package chessgame.model.pieces;

import chessgame.model.*;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    public King(Color color, Position position) {
        super(color, position);
    }

    @Override
    public char getSymbol() {
        // Unicode: ♔ (blanco), ♚ (negro)
        return color == Color.WHITE ? '\u2654' : '\u265A';
    }

    @Override
    public List<Position> getLegalMoves(Board board, boolean attackOnly) {
        List<Position> moves = new ArrayList<>();
        int[][] directions = {{-1, -1},{-1, 0},{-1, 1},{0, -1},{0, 1},{1, -1},{1, 0},{1, 1}};

        for (int[] dir : directions) {
            int nr = position.getRow() + dir[0];
            int nc = position.getCol() + dir[1];
            if (board.isValidPosition(nr, nc)) {
                IPiece p = board.getPieceAt(nr, nc);
                if (p == null || p.getColor() != color) {
                    if (attackOnly) {
                        moves.add(new Position(nr, nc));
                    } else {
                        // Solo permite el movimiento si el destino NO está atacado después del movimiento
                        Board copy = board.deepCopy();
                        try {
                            copy.setPieceAt(position, null);
                            copy.setPieceAt(new Position(nr, nc), this.deepCopy());
                            if (!copy.isUnderAttack(new Position(nr, nc), color))
                                moves.add(new Position(nr, nc));
                        } catch (Exception ignore) {}
                    }
                }
            }
        }
        // Enroque solo si no estamos en modo "attackOnly"
        if (!attackOnly && !moved && !board.isInCheck(color)) {
            if (board.canCastle(color, true)) moves.add(board.castleTarget(color, true));
            if (board.canCastle(color, false)) moves.add(board.castleTarget(color, false));
        }
        return moves;
    }

    @Override
    public IPiece deepCopy() {
        King k = new King(color, new Position(position.getRow(), position.getCol()));
        k.moved = this.moved;
        return k;
    }
}