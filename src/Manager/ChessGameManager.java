package Manager;

import Objects.*;
import Objects.Color;
import Objects.Pieces.Piece;
import Objects.Pieces.PieceType;
import hex_chess.graphics.Application;
import hex_chess.graphics.listeners.EventListener;
import hex_chess.graphics.listeners.GameEventListener;
import hex_chess.graphics.models.StringColor;
import hex_chess.graphics.util.HintUtil;
import hex_chess.util.PieceName;

import java.io.File;
import java.util.ArrayList;

public class ChessGameManager {

    final static String newGameFilePath = ".\\.\\Game files\\initial.ChessGame";
    final static String lastGameFilePath = ".\\.\\Game files\\last.ChessGame";

    Game game;
    AI AI;
    Application application;
    EventListener eventListener;
    Pair selectedToMovePos;
    Pair lastMoveSource;
    Pair lastMoveTarget;
    boolean hasAI;
    final int AIDepth = 2;

    public ChessGameManager(File file, Application application){
        this.game = new Game(this, file);
        this.application = application;
        this.eventListener = new GameEventListener(this);
        this.selectedToMovePos = null;
        application.registerEventListener(eventListener);
        show();
        setUpAI();
    }

    public ChessGameManager(File file, Application application, EventListener eventListener){
        this.game = new Game(this, file);
        this.application = application;
        this.eventListener = eventListener;
        this.selectedToMovePos = null;
        application.registerEventListener(eventListener);
        show();
        setUpAI();
    }

    public void setUpAI(){
        hasAI = false;
        if(application.showModeSelectionPopup().equals("AI mode")){
            Color AIColor = application.showColorSelectionPopup().equals("White") ? Color.BLACK : Color.WHITE;
            AI = new AI(AIColor, AIDepth);
            hasAI = true;
            AIMove();
        }
    }

    public void AIMove(){
        if(!game.getStatus().isFinished() && hasAI && AI.getColor() == game.getTurn()){
            Movement movement = AI.getBestMove(game.getBoard());
            game.move(movement);
            move(movement);
            show();
        }
    }

    public void move(Movement movement){
        selectedToMovePos = null;
        lastMoveSource = movement.getSource();
        lastMoveTarget = movement.getTarget();
    }

    public void show(){
        setHintUtilProperties();
        showBoard();
        showRemovePieces();
        showMessage();
        showEndGamePopup();
    }

    public void showBoard(){
        for(Pair pos : Board.positions){
            Pair correctPos = getCorrectPos(pos);
            int row = getRow(correctPos.l, correctPos.r);
            char col = getCol(correctPos.l, correctPos.r);
            Piece piece = game.getBoard().getPiece(pos);
            String pieceName = piece == null ? null : PieceName.getPieceName(piece);
            java.awt.Color color = piece == null ? null : piece.color.getColor();
            
            java.awt.Color backgroundColor = null;
            if(piece != null && piece.type == PieceType.KING && game.getBoard().isChecked(piece.color))
                backgroundColor = java.awt.Color.RED;
            if(lastMoveSource != null && (pos.equals(lastMoveSource) || pos.equals(lastMoveTarget))){
                backgroundColor = java.awt.Color.lightGray;
            }
            if(selectedToMovePos != null){
                if(pos.equals(selectedToMovePos)){
                    backgroundColor = java.awt.Color.BLUE;
                }
                else if(game.getBoard().isValidMove(game.getTurn(), new Movement(selectedToMovePos, pos))){
                    backgroundColor = java.awt.Color.GREEN;
                }
                else if(piece == null && game.getBoard().isValidMove(game.getTurn().getOpposite(), new Movement(selectedToMovePos, pos))) {
                    backgroundColor = null;
                    pieceName = "•";
                    color = java.awt.Color.orange;
                }
            }
            application.setCellProperties(row, col, pieceName, backgroundColor, color);
        }
    }

    public void showRemovePieces(){
        ArrayList<Piece> deadPieces = game.getDeadPieces();
        StringColor[] removedPieces = new StringColor[deadPieces.size()];
        Color[] colorOrder = {Color.WHITE, Color.BLACK};
        PieceType[] typeOrder = {
                PieceType.PAWN,
                PieceType.KNIGHT,
                PieceType.BISHOP,
                PieceType.ROOK,
                PieceType.QUEEN,
                PieceType.KING,
        };
        int index = 0;
        for (Color color : colorOrder) {
            for (PieceType type : typeOrder) {
                for (Piece piece : game.getDeadPieces()) {
                    if (piece.color == color && piece.type == type) {
                        removedPieces[index++] = new StringColor(PieceName.getPieceName(piece), piece.color.getColor());
                    }
                }
            }
        }
        application.setRemovedPieces(removedPieces);
    }

    public void showMessage(){
        GameStatus status = getGame().getStatus();
        String message = switch (status) {
            case RUNNING -> {
                String turn = regularize(game.getTurn().name());
                yield AI == null ? turn + "'s turn" : "";
            }
            case PAT -> {
                message = "That's a PAT! 3/4 score for " + regularize(game.getTurn().getOpposite().name());
                yield message + " and 1/4 score for " + regularize(game.getTurn().name());
            }
            case DRAW -> "Its Draw! There is no way to win with these pieces remained :(";
            default -> regularize(status.name()) + " Won";
        };
        application.setMessage(message);
    }

    public void showEndGamePopup(){
        GameStatus status = getGame().getStatus();
        if(!status.isFinished())
            return;
        String message = switch (status) {
            case PAT -> "Oops\nThat's a PAT";
            case DRAW -> "Draw!";
            default -> {
                if(AI == null)
                    yield "Congratulations to " + regularize(game.getStatus().name()) + "\nYou won!";
                if(AI.getColor().name().equals(game.getStatus().name())){
                    yield "AI EAT You. Cry About it :(";
                }
                yield "Yeah This is Humanity's Kingdom!";
            }
        };
        application.showMessagePopup(message);
        File file = new File(lastGameFilePath);
        file.delete();
    }



    public static String regularize(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static int getRow(int l, int r){
        return Math.min(l, r);
    }

    public static char getCol(int l, int r){
        char col = (char) ('f' + l - r);
        if(col >= 'j')
            col ++;
        return col;
    }

    public Pair getPos(int row, char col){
        // 'f' + l - r = col; -> l = r + col - 'f', r = l + 'f' - col
        if(col > 'j')
            col --;
        int l, r;
        if(col <= 'f'){
            l = row;
            r = l + 'f' - col;
        }
        else{
            r = row;
            l = r + col - 'f';
        }
        return new Pair(l, r);
    }

    public Color getShowColor(){
        return !hasAI && game.getTurn() == Color.WHITE || hasAI && AI.getColor() == Color.BLACK?
                Color.WHITE : Color.BLACK;
    }

    public Pair getCorrectPos(Pair pos){
        if(pos == null)
            return null;
        return getShowColor() == Color.WHITE? pos : pos.getSymmetric();
    }

    public void setHintUtilProperties(){
        setHintUtil_turnBaseCellColor_remainderFactor();
        setHintUtil_turnBaseDiagonalLabel_factor();
        setHintUtil_turnBaseDiagonalLabel_add();
    }

    public void setHintUtil_turnBaseCellColor_remainderFactor(){
        HintUtil.setTurnBaseCellColor_remainderFactor(getShowColor() == Color.WHITE? 1 : -1);
    }

    public void setHintUtil_turnBaseDiagonalLabel_factor(){
        HintUtil.setTurnBaseDiagonalLabel_factor(getShowColor() == Color.WHITE? 1 : -1);
    }

    public void setHintUtil_turnBaseDiagonalLabel_add(){
        HintUtil.setTurnBaseDiagonalLabel_add(getShowColor() == Color.WHITE? 0 : 12);
    }



    public Pair getSelectedToMovePos() {
        return selectedToMovePos;
    }

    public Game getGame() {
        return game;
    }

    public Application getApplication() {
        return application;
    }

    public boolean hasAI(){
        return hasAI;
    }

    public static String getNewGameFilePath() {
        return newGameFilePath;
    }

    public static String getLastGameFilePath() {
        return lastGameFilePath;
    }

    public void setSelectedToMovePos(Pair selectedToMovePos) {
        this.selectedToMovePos = selectedToMovePos;
    }

    public void setLastMoveSource(Pair lastMoveSource) {
        this.lastMoveSource = lastMoveSource;
    }

    public void setLastMoveTarget(Pair lastMoveTarget) {
        this.lastMoveTarget = lastMoveTarget;
    }

    public static void main(String[] args) {
        File file = new File(ChessGameManager.getLastGameFilePath());
        if(!file.exists()){
            file = new File(ChessGameManager.getNewGameFilePath());
        }
        new ChessGameManager(file, new Application());
    }
}