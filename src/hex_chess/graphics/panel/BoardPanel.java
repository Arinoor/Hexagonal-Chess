package hex_chess.graphics.panel;

import hex_chess.graphics.listeners.DummyEventListener;
import hex_chess.graphics.listeners.EventListener;
import hex_chess.graphics.models.HexagonCell;
import hex_chess.graphics.models.HexagonHint;
import hex_chess.graphics.util.Config;
import hex_chess.graphics.util.HintUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BoardPanel extends JPanel {
    private int boardLeftShift;
    private final int boardTopShift = 48;
    private final List<HexagonCell> cells;
    private final List<HexagonHint> hints;
    private String message;
    private EventListener eventListener;

    public BoardPanel() {
        setLayout(null);
        setBackground(Color.decode("#52932f"));
        this.addMouseListener(new BoardMouseListener());
        this.eventListener = new DummyEventListener();
        cells = new CopyOnWriteArrayList<>();
        hints = new CopyOnWriteArrayList<>();
        message = "";
    }

    public void initialize() {
        boardLeftShift = (getWidth() - (15 * Config.CELL_SIZE)) / 2;
        initialBaseBoard();
        initializeHints();
    }

    private void initialBaseBoard() {
        Character[] chars = HintUtil.getChars();
        for (int row = 1; row <= 11; row++) {
            if (row <= 6) {
                for (Character colChar : chars) {
                    cells.add(new HexagonCell(row, colChar, boardLeftShift, boardTopShift));
                }
            } else {
                for (int i = row - 6; i < chars.length - (row - 6); i++) {
                    cells.add(new HexagonCell(row, chars[i], boardLeftShift, boardTopShift));
                }
            }
        }
    }

    private void initializeHints() {
        int moreShift = 5;
        for (int i = 1; i < 7; i++) {
            hints.add(
                    new HexagonHint(
                            i,
                            'z',
                            boardLeftShift + moreShift,
                            boardTopShift,
                            "" + i
                    )
            );
            hints.add(
                    new HexagonHint(
                            i,
                            'x',
                            boardLeftShift - moreShift,
                            boardTopShift,
                            "" + i
                    )
            );
        }
        Character[] chars = HintUtil.getChars();
        for (Character colChar : chars) {
            int col = HintUtil.getCol(colChar);
            if (col <= 6) {
                hints.add(
                        new HexagonHint(
                                0,
                                colChar,
                                boardLeftShift,
                                boardTopShift - moreShift,
                                "" + colChar
                        )
                );
                if (col <= 5) {
                    hints.add(
                            new HexagonHint(
                                    6 + col,
                                    colChar,
                                    boardLeftShift + moreShift,
                                    boardTopShift + moreShift,
                                    "" + (6 + col)
                            )
                    );
                    hints.add(
                            new HexagonHint(
                                    6 + col,
                                    HintUtil.getCharCol(12 - col),
                                    boardLeftShift - moreShift,
                                    boardTopShift + moreShift,
                                    "" + (6 + col)
                            )
                    );
                }
            } else {
                hints.add(
                        new HexagonHint(
                                0,
                                colChar,
                                boardLeftShift - moreShift,
                                boardTopShift - moreShift,
                                "" + colChar
                        )
                );
            }
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // draw black board as board and background
        Stroke currentStroke = g2.getStroke();
        int boardBorderWidth = 5;
        g2.setStroke(new BasicStroke(boardBorderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        g.setColor(Color.decode("#080808"));
        for (HexagonCell cell : cells) {
            Polygon p = cell.getPolygon();
            g.drawPolygon(p);
        }
        g2.setStroke(currentStroke);
        // foreground board
        cells.forEach(hexagonCell -> hexagonCell.paint(g2));
        hints.forEach(hexagonHint -> hexagonHint.paint(g2));
        // draw message
        FontMetrics fm = g2.getFontMetrics();
        int x = getX() + (getWidth() - fm.stringWidth(message)) / 2;
        int messageBottomShift = 30;
        int y = getHeight() - messageBottomShift - ((fm.getHeight()) / 2) + fm.getAscent();
        g2.setColor(Color.WHITE);
        g2.drawString(message, x, y);
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void setCellProperties(int row, char col, String text, Color backGroundColor, Color textColor) {
        HexagonCell targetCell = findCell(row, col);
        if (targetCell != null) {
            targetCell.setText(text);
            targetCell.setBackGroundColor(backGroundColor);
            targetCell.setTextColor(textColor);
        } else {
            System.err.printf("cant find cel with row=%d, col=%c", row, col);
        }
    }

    private HexagonCell findCell(int row, char col) {
        for (HexagonCell cell : cells) {
            if (cell.getRow() == row && cell.getCol() == col) {
                return cell;
            }
        }
        return null;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private class BoardMouseListener implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            HexagonCell targetCell = null;
            for (HexagonCell cell : cells) {
                if (cell.getPolygon().contains(mouseEvent.getPoint())) {
                    targetCell = cell;
                    break;
                }
            }
            if (targetCell != null) {
                eventListener.onClick(targetCell.getRow(), targetCell.getCol());
            }
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {

        }
    }
}
