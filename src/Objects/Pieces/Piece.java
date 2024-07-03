package Objects.Pieces;

import Objects.Color;
import Objects.Pair;

public abstract class Piece implements Reachability, CopyPiece {

    public Color color;
    public PieceType type;
    public Pair pos;
    public boolean isAlive;

    public Piece(Color color, Pair pos, PieceType type) {
        this.color = color;
        this.pos = pos;
        this.type = type;
        this.isAlive = pos != null;
    }

    public static Piece makePiece(PieceType type, Color color, Pair pos){
        return switch (type){
            case KING -> new King(color, pos);
            case QUEEN -> new Queen(color, pos);
            case ROOK -> new Rook(color, pos);
            case BISHOP -> new Bishop(color, pos);
            case KNIGHT -> new Knight(color, pos);
            case PAWN -> new Pawn(color, pos, false);
        };
    }

    public void moveTo(Pair pos) { this.pos = pos; }

    public void makeDead() {
        this.pos = null;
        this.isAlive = false;
    }
}