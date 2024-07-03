package hex_chess.graphics.listeners;

import Manager.ChessGameManager;
import Objects.Movement;
import Objects.Pair;
import Objects.Pieces.Piece;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class GameEventListener implements EventListener{

    ChessGameManager chessManager;

    public GameEventListener(ChessGameManager chessManager){
        this.chessManager = chessManager;
    }

    public void onClick(int row, char col) {
        if(chessManager.getGame().getStatus().isFinished()){
            return;
        }

        Pair clickedPos = chessManager.getCorrectPos(chessManager.getPos(row, col));
        Piece clickedPiece = chessManager.getGame().getBoard().getPiece(clickedPos);
        Pair selectedToMovePos = chessManager.getSelectedToMovePos();

        Movement movement = new Movement(selectedToMovePos, clickedPos);
        if (selectedToMovePos != null && !chessManager.getGame().move(movement).equals("Invalid Move")) {
            chessManager.move(movement);
        }
        else if(clickedPiece != null){
            if(selectedToMovePos != null && clickedPos.equals(selectedToMovePos)){
                chessManager.setSelectedToMovePos(null);
            }
            else{
                chessManager.setSelectedToMovePos(clickedPos);
            }
        }
        chessManager.show();
        chessManager.AIMove();
    }

    public void onLoad(File file) {
        if(!file.getName().endsWith(".ChessGame")){
            throw new RuntimeException("choose appropriate file type 'ChessGame'");
        }
        chessManager = new ChessGameManager(file, chessManager.getApplication(), this);
    }

    public void onSave(File file) {
        if(!file.getName().endsWith(".ChessGame")){
            throw new RuntimeException("choose appropriate file type 'ChessGame'");
        }

        Scanner read;
        PrintWriter write;

        try {
            read = new Scanner(new File(ChessGameManager.getLastGameFilePath()));
            write = new PrintWriter(file);
        } catch (FileNotFoundException e) { throw new RuntimeException(e); }

        while(read.hasNextLine()){
            write.println(read.nextLine());
        }

        read.close();
        write.close();
    }

    public void onNewGame() {
        File file = new File(ChessGameManager.getNewGameFilePath());
        chessManager = new ChessGameManager(file, chessManager.getApplication(), this);
    }
}
