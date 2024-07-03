package Objects;

public enum Color {

    WHITE (java.awt.Color.WHITE),
    BLACK (java.awt.Color.BLACK);

    private final java.awt.Color color;

    Color(java.awt.Color color) {
        this.color = color;
    }

    public Color getOpposite() {
        return this == WHITE ? BLACK : WHITE;
    }

    public java.awt.Color getColor() {
        return color;
    }

}