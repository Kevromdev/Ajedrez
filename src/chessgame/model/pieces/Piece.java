package chessgame.model.pieces;

import chessgame.model.*;

public abstract class Piece implements IPiece {
    protected Color color;
    protected Position position;
    protected boolean moved = false;

    public Piece(Color color, Position position) {
        this.color = color;
        this.position = position;
    }

    public Color getColor() { return color; }
    public Position getPosition() { return position; }
    public void setPosition(Position pos) { this.position = pos; }
    public boolean hasMoved() { return moved; }
    public void setMoved(boolean moved) { this.moved = moved; }
}