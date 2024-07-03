package hex_chess.graphics.models;

import hex_chess.graphics.util.Config;
import hex_chess.graphics.util.HintUtil;

import java.awt.*;

public abstract class Hexagon implements Paintable {
    protected final int row;
    protected char col;
    protected final int startX;
    protected final int startY;
    protected final Polygon polygon;

    public Hexagon(int row, char col, int startX, int startY) {
        this.row = row;
        this.col = col;
        this.startX = startX;
        this.startY = startY;
        this.polygon = createPolygon();
    }

    private Polygon createPolygon() {
        double height = (Math.sin(Math.PI / 3) * Config.CELL_SIZE * 2);
        int iCol = HintUtil.getCol(col);
        double x = startX + ((3 * Config.CELL_SIZE / 2.0) * (iCol - 1));
        double y = startY + height * 8;
        y += (height / 2) * (iCol <= 6 ? (iCol - 1) : (11 - iCol));
        y -= height * (row - 1);
        Polygon hex = new Polygon();
        for (int i = 0, deg = 90; i < 6; i++, deg += 60) {
            hex.addPoint(
                    (int) Math.round(Config.CELL_SIZE * Math.sin(deg / 180.0 * Math.PI)),
                    (int) Math.round(Config.CELL_SIZE * Math.cos(deg / 180.0 * Math.PI))
            );
        }
        hex.translate((int) Math.round(x), (int) Math.round(y));
        return hex;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public int getRow() {
        return row;
    }

    public char getCol() {
        return col;
    }

    @Override
    public String toString() {
        return "Hexagon{" + "row=" + row + ", col=" + col + '}';
    }

    protected void drawTextOnCenter(Graphics2D g2, Polygon p, String text, Color color) {
        FontMetrics fm = g2.getFontMetrics();
        double x = p.getBounds().getX() + (p.getBounds().getWidth() - fm.stringWidth(text)) / 2;
        double y = p.getBounds().getY() + ((p.getBounds().getHeight() - fm.getHeight()) / 2) + fm.getAscent();
        g2.setColor(color);
        g2.drawString(text, (int) Math.round(x), (int) Math.round(y));
    }

    @Override
    public abstract void paint(Graphics2D g2);
}