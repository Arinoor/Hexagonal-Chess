package hex_chess.graphics.models;

import hex_chess.graphics.util.Config;
import hex_chess.graphics.util.HintUtil;

import java.awt.*;


public class HexagonCell extends Hexagon implements Paintable {
    private String text;
    private Color backGroundColor;
    private Color textColor;

    public HexagonCell(int row, char col, int startX, int startY) {
        super(row, col, startX, startY);
    }

    public void setBackGroundColor(Color backGroundColor) { this.backGroundColor = backGroundColor; }

    public void setText(String text) {
        this.text = text;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public String getText() {
        return text == null ? "" : text;
    }

    public Color getTextColor() {
        return textColor == null ? Color.WHITE : textColor;
    }

    public Color getBackGroundColor() {
        return backGroundColor == null ? HintUtil.getColor(this.getRow(), this.getCol()) : backGroundColor;
    }

    @Override
    public void paint(Graphics2D g2) {
        g2.setColor(getBackGroundColor());
        Polygon p = this.getPolygon();
        g2.fillPolygon(p);
        g2.setFont(Config.PIECE_FONT);
        drawTextOnCenter(g2, p, getText(), getTextColor());
    }
}
