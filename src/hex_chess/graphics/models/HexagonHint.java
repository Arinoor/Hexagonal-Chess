package hex_chess.graphics.models;


import hex_chess.graphics.util.Config;
import hex_chess.graphics.util.HintUtil;

import java.awt.*;

public class HexagonHint extends Hexagon implements Paintable {
    final private String hintLabel;

    public HexagonHint(int row, char col, int startX, int startY, String hintLabel) {
        super(row, col, startX, startY);
        this.hintLabel = hintLabel;
    }

    @Override
    public void paint(Graphics2D g2) {
        Polygon p = this.getPolygon();

        g2.setFont(Config.HINT_FONT);
        drawTextOnCenter(g2, p, getHintLabel(), Color.WHITE);
    }

    private String getHintLabel(){
        try {
            int label = Integer.parseInt(this.hintLabel);
            label = label * HintUtil.getTurnBaseDiagonalLabel_factor() + HintUtil.getTurnBaseDiagonalLabel_add();
            return Integer.toString(label);
        }
        catch (NumberFormatException e){
            return hintLabel;
        }

    }
}
