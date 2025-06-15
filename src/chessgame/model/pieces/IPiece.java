package chessgame.model.pieces;

import chessgame.model.*;

import java.io.Serializable;
import java.util.List;

public interface IPiece extends Serializable {
    Color getColor();
    Position getPosition();
    void setPosition(Position pos);
    char getSymbol();
    // El parámetro attackOnly es true si se calcula para detectar jaque, false para un movimiento normal
    List<Position> getLegalMoves(Board board, boolean attackOnly);
    boolean hasMoved(); // para enroque y peón doble movimiento
    void setMoved(boolean moved);
    IPiece deepCopy(); // Para clonar piezas
}