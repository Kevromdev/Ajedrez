package chessgame.model;

import chessgame.model.pieces.*;

import java.io.Serializable;
import java.util.*;

public class Board implements Serializable {
    private final IPiece[][] board;
    private Position enPassantTarget;
    private transient boolean checkingForCheck = false;

    public Board() {
        board = new IPiece[8][8];
        init();
    }

    public void init() {
        for (int i=0; i<8; i++)
            for (int j=0; j<8; j++)
                board[i][j] = null;
        // Blancas
        board[7][0] = new Rook(Color.WHITE, new Position(7,0));
        board[7][1] = new Knight(Color.WHITE, new Position(7,1));
        board[7][2] = new Bishop(Color.WHITE, new Position(7,2));
        board[7][3] = new Queen(Color.WHITE, new Position(7,3));
        board[7][4] = new King(Color.WHITE, new Position(7,4));
        board[7][5] = new Bishop(Color.WHITE, new Position(7,5));
        board[7][6] = new Knight(Color.WHITE, new Position(7,6));
        board[7][7] = new Rook(Color.WHITE, new Position(7,7));
        for (int i=0; i<8; i++)
            board[6][i] = new Pawn(Color.WHITE, new Position(6,i));
        // Negras
        board[0][0] = new Rook(Color.BLACK, new Position(0,0));
        board[0][1] = new Knight(Color.BLACK, new Position(0,1));
        board[0][2] = new Bishop(Color.BLACK, new Position(0,2));
        board[0][3] = new Queen(Color.BLACK, new Position(0,3));
        board[0][4] = new King(Color.BLACK, new Position(0,4));
        board[0][5] = new Bishop(Color.BLACK, new Position(0,5));
        board[0][6] = new Knight(Color.BLACK, new Position(0,6));
        board[0][7] = new Rook(Color.BLACK, new Position(0,7));
        for (int i=0; i<8; i++)
            board[1][i] = new Pawn(Color.BLACK, new Position(1,i));
        enPassantTarget = null;
    }

    public boolean isValidPosition(int row, int col) { return row >= 0 && row < 8 && col >= 0 && col < 8; }
    public boolean isValidPosition(Position pos) { return isValidPosition(pos.getRow(), pos.getCol()); }

    public IPiece getPieceAt(int row, int col) { return board[row][col]; }
    public IPiece getPieceAt(Position pos) { return board[pos.getRow()][pos.getCol()]; }
    public void setPieceAt(Position pos, IPiece p) { board[pos.getRow()][pos.getCol()] = p; }

    public List<Position> getMovesInDirections(IPiece piece, int[][] directions, boolean repeat) {
        List<Position> moves = new ArrayList<>();
        Position pos = piece.getPosition();
        for (int[] dir : directions) {
            int nr = pos.getRow() + dir[0], nc = pos.getCol() + dir[1];
            while (isValidPosition(nr, nc)) {
                IPiece p = getPieceAt(nr, nc);
                if (p == null) {
                    moves.add(new Position(nr, nc));
                } else {
                    if (p.getColor() != piece.getColor())
                        moves.add(new Position(nr, nc));
                    break;
                }
                if (!repeat) break;
                nr += dir[0]; nc += dir[1];
            }
        }
        return moves;
    }

    public boolean movePiece(Position from, Position to, Color currentColor) throws Exception {
        IPiece piece = getPieceAt(from);
        if (piece == null) throw new Exception("No hay pieza en la posición.");
        if (piece.getColor() != currentColor) throw new Exception("No es tu turno.");
        List<Position> legal = getLegalMovesForPieceWhileInCheck(piece, currentColor);
        if (!legal.contains(to)) throw new Exception("Movimiento ilegal.");

        // Enroque
        if (piece instanceof King && Math.abs(from.getCol()-to.getCol()) == 2) {
            doCastle(currentColor, to.getCol() > from.getCol());
        } else {
            // Comer al paso
            if (piece instanceof Pawn && isEnPassant(from, to)) {
                int dir = (piece.getColor() == Color.WHITE) ? 1 : -1;
                Position capturedPawn = new Position(to.getRow() + dir, to.getCol());
                setPieceAt(capturedPawn, null);
            }
            setPieceAt(to, piece);
            setPieceAt(from, null);
            piece.setPosition(new Position(to.getRow(), to.getCol()));
            piece.setMoved(true);
            // En paso
            if (piece instanceof Pawn && Math.abs(to.getRow() - from.getRow()) == 2)
                enPassantTarget = new Position((from.getRow() + to.getRow()) / 2, from.getCol());
            else enPassantTarget = null;
            // Promoción
            if (piece instanceof Pawn && (to.getRow() == 0 || to.getRow() == 7)) {
                setPieceAt(to, new Queen(currentColor, new Position(to.getRow(), to.getCol())));
            }
        }
        return true;
    }

    // --- NUEVO: Solo devuelve movimientos legales que sacan al rey del jaque ---
    public List<Position> getLegalMovesForPieceWhileInCheck(IPiece piece, Color color) {
        List<Position> moves = new ArrayList<>();
        for (Position to : piece.getLegalMoves(this, false)) {
            Board copy = this.deepCopy();
            try {
                copy.setPieceAt(piece.getPosition(), null);
                IPiece moved = piece.deepCopy();
                moved.setPosition(to);
                moved.setMoved(true);
                copy.setPieceAt(to, moved);
                if (!copy.isInCheck(color)) {
                    moves.add(to);
                }
            } catch (Exception ignored) {}
        }
        return moves;
    }

    // --- NUEVO: Todos los movimientos legales para el turno actual ---
    public List<Move> getLegalMovesForTurn(Color color) {
        List<Move> result = new ArrayList<>();
        for (int r = 0; r < 8; r++) for (int c = 0; c < 8; c++) {
            IPiece piece = board[r][c];
            if (piece != null && piece.getColor() == color) {
                for (Position to : getLegalMovesForPieceWhileInCheck(piece, color)) {
                    result.add(new Move(piece.getPosition(), to));
                }
            }
        }
        return result;
    }

    // Clase auxiliar para representar un movimiento
    public static class Move {
        public final Position from;
        public final Position to;
        public Move(Position from, Position to) {
            this.from = from;
            this.to = to;
        }
    }

    public boolean isEnPassant(Position from, Position to) {
        IPiece piece = getPieceAt(from);
        if (!(piece instanceof Pawn)) return false;
        if (enPassantTarget == null) return false;
        return to.equals(enPassantTarget);
    }

    public boolean canCastle(Color color, boolean shortCastle) {
        int row = (color == Color.WHITE) ? 7 : 0;
        King king = (King)getPieceAt(new Position(row,4));
        if (king == null || king.hasMoved() || isInCheck(color)) return false;
        int rookCol = shortCastle ? 7 : 0;
        Rook rook = (Rook)getPieceAt(new Position(row,rookCol));
        if (rook == null || rook.hasMoved()) return false;
        int step = shortCastle ? 1 : -1;
        for (int c = 4 + step; c != rookCol; c += step)
            if (getPieceAt(row, c) != null) return false;
        for (int c = 4; c != 4 + 2*step; c += step)
            if (isUnderAttack(new Position(row, c), color)) return false;
        return true;
    }

    public Position castleTarget(Color color, boolean shortCastle) {
        int row = (color == Color.WHITE) ? 7 : 0;
        int col = shortCastle ? 6 : 2;
        return new Position(row, col);
    }

    public void doCastle(Color color, boolean shortCastle) {
        int row = (color == Color.WHITE) ? 7 : 0;
        int kingSrcCol = 4;
        int kingDstCol = shortCastle ? 6 : 2;
        int rookSrcCol = shortCastle ? 7 : 0;
        int rookDstCol = shortCastle ? 5 : 3;
        // Mueve rey
        IPiece king = getPieceAt(row, kingSrcCol);
        setPieceAt(new Position(row, kingDstCol), king);
        king.setPosition(new Position(row, kingDstCol));
        king.setMoved(true);
        setPieceAt(new Position(row, kingSrcCol), null);
        // Mueve torre
        IPiece rook = getPieceAt(row, rookSrcCol);
        setPieceAt(new Position(row, rookDstCol), rook);
        rook.setPosition(new Position(row, rookDstCol));
        rook.setMoved(true);
        setPieceAt(new Position(row, rookSrcCol), null);
        enPassantTarget = null;
    }

    public boolean isInCheck(Color color) {
        if (checkingForCheck) return false;
        checkingForCheck = true;
        Position kingPos = findKingPosition(color);
        boolean result = false;
        if (kingPos != null) {
            outer:
            for (int r=0; r<8; r++)
                for (int c=0; c<8; c++) {
                    IPiece p = board[r][c];
                    if (p != null && p.getColor() != color) {
                        List<Position> moves = p.getLegalMoves(this, true);
                        if (moves.contains(kingPos)) {
                            result = true;
                            break outer;
                        }
                    }
                }
        }
        checkingForCheck = false;
        return result;
    }

    public boolean isUnderAttack(Position pos, Color color) {
        for (int r=0; r<8; r++)
            for (int c=0; c<8; c++) {
                IPiece p = board[r][c];
                if (p != null && p.getColor() != color) {
                    List<Position> moves = p.getLegalMoves(this, true);
                    if (moves.contains(pos))
                        return true;
                }
            }
        return false;
    }

    public boolean isCheckmate(Color color) {
        if (!isInCheck(color)) return false;
        List<Move> moves = getLegalMovesForTurn(color);
        return moves.isEmpty();
    }

    public Board deepCopy() {
        Board b = new Board();
        for (int i=0; i<8; i++)
            for (int j=0; j<8; j++)
                if (board[i][j] != null)
                    b.board[i][j] = board[i][j].deepCopy();
                else
                    b.board[i][j] = null;
        b.enPassantTarget = this.enPassantTarget != null ? new Position(this.enPassantTarget.getRow(), this.enPassantTarget.getCol()) : null;
        return b;
    }

    public Position findKingPosition(Color color) {
        for (int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                IPiece p = board[i][j];
                if (p instanceof King && p.getColor() == color) {
                    return new Position(i, j);
                }
            }
        }
        return null;
    }

    public void printBoard() {
        System.out.println("   a  b  c  d  e  f  g  h");
        for (int i=0; i<8; i++) {
            System.out.print((8-i) + " ");
            for (int j=0; j<8; j++) {
                IPiece p = board[i][j];
                if (p == null) System.out.print(" . ");
                else System.out.print(" " + p.getSymbol() + " ");
            }
            System.out.println(" " + (8-i));
        }
        System.out.println("   a  b  c  d  e  f  g  h");
    }
}