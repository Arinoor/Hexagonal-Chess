package Objects;

import Objects.Pieces.Piece;

public class Cell {
    final Pair pos;
    Piece piece;

    public Cell(Pair pos, Piece piece){
        this.pos = pos;
        this.piece = piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Piece getPiece() {
        return piece;
    }

    public Cell getCopy(){
        return new Cell(pos, piece == null ? null : piece.getCopy());
    }
}
