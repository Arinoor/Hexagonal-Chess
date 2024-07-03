package hex_chess.graphics.models;

import java.awt.*;

public class StringColor {
    private final String text;
    private final Color color;
    public static final Color WHITE = Color.WHITE;
    public static final Color BLACK = Color.BLACK;

    public StringColor(String text, Color color) {
        this.text = text;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }
}
