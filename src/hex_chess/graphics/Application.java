package hex_chess.graphics;

import hex_chess.graphics.listeners.EventListener;
import hex_chess.graphics.models.StringColor;
import hex_chess.graphics.panel.BoardPanel;
import hex_chess.graphics.panel.RemovedPiecesPanel;

import javax.swing.*;
import java.awt.*;

public class Application {
    private final Frame mainFrame;
    private final BoardPanel boardPanel;
    private final RemovedPiecesPanel removedPiecesPanel;

    public Application() {
        this.mainFrame = new Frame();
        this.boardPanel = new BoardPanel();
        this.mainFrame.getContentPane().setLayout(new BorderLayout());
        this.mainFrame.getContentPane().add(boardPanel, BorderLayout.CENTER);

        removedPiecesPanel = new RemovedPiecesPanel();
        this.mainFrame.getContentPane().add(removedPiecesPanel, BorderLayout.LINE_END);

        this.mainFrame.setVisible(true);
        boardPanel.initialize();
    }

    public void registerEventListener(EventListener eventListener) {
        if (eventListener != null) {
            boardPanel.setEventListener(eventListener);
            mainFrame.setEventListener(eventListener);
        }
    }

    public void setCellProperties(int row, char col, String text, Color backGroundColor, Color textColor) {
        boardPanel.setCellProperties(row, col, text, backGroundColor, textColor);
    }

    public void setMessage(String text) {
        boardPanel.setMessage(text);
    }

    public void showMessagePopup(String text) {
        JOptionPane.showMessageDialog(mainFrame, text);
    }

    public void setRemovedPieces(StringColor[] pieces) {
        this.removedPiecesPanel.setPieces(pieces);
    }

    public String showPromotionPopup() {
        String[] options = new String[]{"Knight", "Bishop", "Rook", "Queen"};
        return options[JOptionPane.showOptionDialog(mainFrame, "Choose your piece ^-^", "Promotion",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, null)];
    }

    public String showModeSelectionPopup() {
        String[] options = new String[]{"AI mode", "Simple mode"};
        return options[JOptionPane.showOptionDialog(mainFrame, "Choose which mode you want to play",
                "Mode Selection",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, null)];
    }

    public String showColorSelectionPopup() {
        String[] options = new String[]{"White", "Black"};
        return options[JOptionPane.showOptionDialog(mainFrame, "Choose which color you want to play",
                "Color Selection",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, null)];
    }

}
