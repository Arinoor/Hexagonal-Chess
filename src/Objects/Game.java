package Objects;

import Manager.ChessGameManager;
import Objects.Pieces.Pawn;
import Objects.Pieces.Piece;
import Objects.Pieces.PieceType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Game {

    ChessGameManager chessManager;
    Color turn;
    Board board;
    GameStatus status;
    ArrayList<Piece> deadPieces;

    public Game(ChessGameManager chessManager, File gameInfo){
        this.chessManager = chessManager;
        load(gameInfo);
        save();
    }

    public String move(Movement movement){
        if(!board.isValidMove(turn, movement)){
            return "Invalid Move";
        }

        Piece deadPiece = board.makeMove(movement);
        if(deadPiece != null){
            deadPieces.add(deadPiece);
        }

        if(board.getPiece(movement.getTarget()).type == PieceType.PAWN && movement.getTarget().isPromotionPos(turn)){
            askForPromotion(movement.getTarget());
        }

        turn = turn.getOpposite();
        updateGameStatus();
        save();
        return status.name();
    }

    void askForPromotion(Pair pos){
        String promotedPieceName = !chessManager.hasAI() ?
                chessManager.getApplication().showPromotionPopup() : "Queen";
        PieceType promotedPieceType = PieceType.valueOf(promotedPieceName.toUpperCase());
        Piece promotedPiece = Piece.makePiece(promotedPieceType, turn, pos);
        Piece promotedPawn = board.getPiece(pos);
        Cell promotedCell = board.getCell(pos);
        promotedPawn.makeDead();
        deadPieces.add(promotedPawn);
        promotedCell.setPiece(promotedPiece);
    }

    void updateGameStatus(){
        boolean isChecked = board.isChecked(turn);
        boolean canNotMove = board.canNotMove(turn);
        if(isChecked && canNotMove){
            status = GameStatus.valueOf(turn.getOpposite().name());
        }
        else if(canNotMove){
            status = GameStatus.PAT;
        }
        else if(board.isDraw()){
            status = GameStatus.DRAW;
        }
        else{
            status = GameStatus.RUNNING;
        }
    }

    public void load(File file){
        Scanner readGame;
        try {
            readGame = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        readGame.nextLine(); readGame.nextLine();
        loadBoard(readGame);
        loadDeadPieces(readGame);
        loadTurn(readGame);
        loadStatus(readGame);
        readGame.close();
    }

    public void loadBoard(Scanner readBoard) {
        Cell[][] table = new Cell[Board.size + 1][Board.size + 1];
        for(Pair pos : Board.positions){
            table[pos.l][pos.r] = new Cell(pos, null);
        }
        String pieceInfo;
        while(!(pieceInfo = readBoard.nextLine()).equals("Dead Pieces:")){
            String[] info = pieceInfo.split(" ");
            PieceType type = PieceType.valueOf(info[0].toUpperCase());
            Color color = Color.valueOf(info[1].toUpperCase());
            int l = Integer.parseInt(info[2]);
            int r = Integer.parseInt(info[3]);
            Pair pos = new Pair(l, r);
            Piece piece;
            if(type != PieceType.PAWN) {
                piece = Piece.makePiece(type, color, pos);
            }
            else {
                boolean hasEnPassantSituation = info[4].equals("true");
                piece = new Pawn(color, pos, hasEnPassantSituation);
            }
            table[l][r].setPiece(piece);
        }
        board = new Board(table);
    }

    public void loadDeadPieces(Scanner readDeadPieces){
        deadPieces = new ArrayList<>();
        String pieceInfo;
        while(!(pieceInfo = readDeadPieces.nextLine()).equals("Turn:")){
            String[] info = pieceInfo.split(" ");
            PieceType type = PieceType.valueOf(info[0].toUpperCase());
            Color color = Color.valueOf(info[1].toUpperCase());
            Piece piece = Piece.makePiece(type, color, null);
            deadPieces.add(piece);
        }
    }

    public void loadTurn(Scanner readTurn){
        String turn = readTurn.nextLine();
        this.turn = Color.valueOf(turn.toUpperCase());
        readTurn.nextLine();
    }

    public void loadStatus(Scanner readStatus){
        String status = readStatus.nextLine();
        this.status = GameStatus.valueOf(status.toUpperCase());
    }

    public void save(){
        File file = new File(ChessGameManager.getLastGameFilePath());
        PrintWriter writeGame;
        try {
            writeGame = new PrintWriter(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        writeGame.println("This is a Chess Game file");
        saveBoard(writeGame);
        saveDeadPieces(writeGame);
        saveTurn(writeGame);
        saveStatus(writeGame);
        writeGame.close();
    }

    public void saveBoard(PrintWriter writeBoard){
        writeBoard.println("Alive Pieces");
        for(Pair pos : Board.positions){
            Piece piece = board.getPiece(pos);
            if(piece == null) continue;
            String type = ChessGameManager.regularize(piece.type.name());
            String color = ChessGameManager.regularize(piece.color.name());
            writeBoard.print(type + " " + color + " " + pos.l + " " + pos.r);
            if(piece.type == PieceType.PAWN){
                writeBoard.print(" " + ((Pawn) piece).hasEnPassantSituation());
            }
            writeBoard.println();
        }
        writeBoard.flush();
    }

    public void saveDeadPieces(PrintWriter writeDeadPieces){
        writeDeadPieces.println("Dead Pieces:");
        for(Piece piece : deadPieces){
            String type = ChessGameManager.regularize(piece.type.name());
            String color = ChessGameManager.regularize(piece.color.name());
            writeDeadPieces.println(type + " " + color);
        }
        writeDeadPieces.flush();
    }

    public void saveTurn(PrintWriter writeTurn){
        writeTurn.println("Turn:");
        String turn = ChessGameManager.regularize(this.turn.name());
        writeTurn.println(turn);
        writeTurn.flush();
    }

    public void saveStatus(PrintWriter writeStatus){
        writeStatus.println("GameStatus:");
        String status = ChessGameManager.regularize(this.status.name());
        writeStatus.println(status);
        writeStatus.flush();
    }

    public Board getBoard() {
        return board;
    }

    public ArrayList<Piece> getDeadPieces() {
        return deadPieces;
    }

    public Color getTurn() {
        return turn;
    }

    public GameStatus getStatus() {
        return status;
    }
}