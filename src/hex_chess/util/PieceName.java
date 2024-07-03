package hex_chess.util;

import Objects.Pieces.Piece;

import java.lang.reflect.Field;

public class PieceName {

    public static final String WHITE_ROOK = "\u2656";
    public static final String WHITE_KNIGHT = "\u2658";
    public static final String WHITE_BISHOP = "\u2657";
    public static final String WHITE_QUEEN = "\u2655";
    public static final String WHITE_KING = "\u2654";
    public static final String WHITE_PAWN = "\u2659";


    public static final String BLACK_ROOK = "\u265C";
    public static final String BLACK_KNIGHT = "\u265E";
    public static final String BLACK_BISHOP = "\u265D";
    public static final String BLACK_QUEEN = "\u265B";
    public static final String BLACK_KING = "\u265A";
    public static final String BLACK_PAWN = "\u265F";

    public static String getPieceName(Piece piece){
        String name = piece.color.name() + "_" + piece.type.name();
        try {
            Field pieceName = PieceName.class.getDeclaredField(name);
            return (String) pieceName.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
