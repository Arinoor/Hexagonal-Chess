package Objects.Pieces;

public enum PieceType {
    PAWN (100, 4),
    BISHOP (300, 12),
    KNIGHT (310, 12),
    ROOK (500, 30),
    QUEEN (900, 42),
    KING (2000, 12);

    final private int value;
    final private int mobility;

    PieceType(int value, int mobility){
        this.value = value;
        this.mobility = mobility;
    }

    public static double getInteractionRate(PieceType attackerOrSupporter, PieceType targetOrSupported){
        return (double) targetOrSupported.value / attackerOrSupporter.value;
    }

    public int getValue() {
        return value;
    }

    public int getMobility() {
        return mobility;
    }
}