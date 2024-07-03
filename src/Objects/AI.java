package Objects;

import Manager.ChessGameManager;
import Objects.Pieces.Piece;
import Objects.Pieces.PieceType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class AI {
    
    final private static int cellCount = 100;
    final private static int mod = (int) (1e9 + 7);
    final private static int baseL = 73;
    final private static int baseR = 47;
    final private static int baseP = 23;
    final private static int baseT = 83;
    final private static int inf = (int) 1e9;
    final private static int[] pwrL = new int[cellCount];
    final private static int[] pwrR = new int[cellCount];
    final private static int[] pwrP = new int[cellCount];
    final private static int[] pwrT = new int[cellCount];
    final private static String memoryFile = ".\\.\\AI files\\memory.txt";
    final private int depth;
    final private Color color;
    private Movement movement;
    HashMap<Integer, Movement> memory;
    
    public static int mul(int a, int b){
        return (int) ((long) a * b % mod);
    }
    
    public static int add(int a, int b){
        int c = a + b;
        if(c >= mod) c -= mod;
        if(c < 0) c += mod;
        return c;
    }
    
    static {
        pwrL[0] = 1;
        pwrR[0] = 1;
        pwrP[0] = 1;
        pwrT[0] = 1;
        for(int i = 1; i < cellCount; i ++){
            pwrL[i] = mul(pwrL[i - 1], baseL);
            pwrR[i] = mul(pwrR[i - 1], baseR);
            pwrP[i] = mul(pwrP[i - 1], baseP);
            pwrT[i] = mul(pwrT[i - 1], baseT);
        }
    }

    public AI(Color color, int depth){
        this.depth = depth;
        this.color = color;
        movement = new Movement();
    }

    public double getOptimizedBoard(Color turn, Board board, int depth, double alpha, double beta){
        if(depth == 0)
            return getValue(board, turn);
        ArrayList<Movement> movements = getMovementOrder(board, turn);
        if(movements.isEmpty())
            return getValue(board, turn);
        for(Movement movement : movements){
            Board newBoard = new Board(board.getTableCopy());
            newBoard.makeMove(movement);
            newBoard.checkForPromotion(movement.target);
            double value = -getOptimizedBoard(turn.getOpposite(), newBoard, depth - 1, -beta, -alpha);
            if(value > alpha){
                addMemory(board, turn, movement);
                alpha = value;
                if(depth == this.depth){
                    this.movement = movement;
                }
            }
            if(value >= beta)
                return beta;
        }
        return alpha;
    }

    public Movement getBestMove(Board board){
        readMemory();
        getOptimizedBoard(color, board, depth, -inf, inf);
        saveMemory();
        return movement;
    }

    public ArrayList<Movement> getMovementOrder(Board board, Color turn){
        Movement firstMovement = memory.get(getHash(board, turn));
        ArrayList<Movement> movements = new ArrayList<>();
        ArrayList<Movement> checkMovements = new ArrayList<>();
        ArrayList<Movement> captureMovements = new ArrayList<>();
        ArrayList<Movement> otherMovements = new ArrayList<>();
        for(Pair source : Board.positions){
            Piece sourcePiece = board.getPiece(source);
            if(sourcePiece != null && sourcePiece.color == turn){
                for(Pair target : Board.positions){
                    Movement movement = new Movement(source, target);
                    if(board.isValidMove(turn, movement)){
                        Board testBoard = new Board(board.getTableCopy());
                        Piece deadPiece = testBoard.makeMove(movement);
                        if(testBoard.isChecked(turn.getOpposite())){
                            movement.setScore(-testBoard.getValidMovesCount(turn.getOpposite()));
                            checkMovements.add(movement);
                        }
                        else if(deadPiece != null){
                            movement.setScore(PieceType.getInteractionRate(sourcePiece.type, deadPiece.type));
                            captureMovements.add(movement);
                        }
                        else{
                            movement.setScore(getValue(testBoard, color));
                            otherMovements.add(movement);
                        }
                    }
                }
            }
        }
        Collections.sort(checkMovements);
        Collections.sort(captureMovements);
        Collections.sort(otherMovements);
        if(firstMovement != null && board.isValidMove(turn, firstMovement)){
            movements.add(firstMovement);
        }
        movements.addAll(checkMovements);
        movements.addAll(captureMovements);
        movements.addAll(otherMovements);
        return movements;
    }

    public double getValue(Board board, Color turn){
        if(board.isDraw()){
            return 0;
        }
        boolean isChecked = board.isChecked(turn);
        boolean canNotMove = board.canNotMove(turn);
        boolean isOpponentChecked = board.isChecked(turn.getOpposite());
        boolean canNotOpponentMove = board.canNotMove(turn.getOpposite());
        if(isChecked && canNotMove) return -inf;
        if(isOpponentChecked && canNotOpponentMove) return inf;
        if(canNotMove) return (double) inf / 4;
        if(canNotOpponentMove) return (double) inf / 4 * 3;
        double value = 0;
        for(Pair pos : Board.positions){
            Piece piece = board.getPiece(pos);
            if(piece == null)
                continue;
            int typeValue = piece.type.getValue();
            double res = 0;
            int moveCnt = 0;
            for(Pair pos2 : Board.positions){
                boolean validMove = board.isValidMove(turn, new Movement(pos, pos2));
                if(validMove){
                    moveCnt++;
                }
                Piece piece2 = board.getPiece(pos2);
                if(piece2 == null) continue;
                if(piece2.color == color){
                    if(board.isValidMove(color, new Movement(pos2, pos))){
                        res += PieceType.getInteractionRate(piece2.type, piece.type);
                    }
                }
                else{
                    if(board.isValidMove(color, new Movement(pos, pos2))){
                        res += PieceType.getInteractionRate(piece.type, piece2.type);
                    }
                }
            }
            if(piece.type == PieceType.PAWN){
                int colDistance = Math.abs(pos.l - pos.r);
                res += 6.0 / (colDistance + 1);
                int rowDistance = 11 - colDistance + ChessGameManager.getRow(pos.r, pos.r);
                res += 6.0 / (rowDistance);
            }
            else{
                res += (double) moveCnt / piece.type.getMobility();
            }
            value += (res + 1000) * typeValue * (turn == piece.color ? 1 : -1);
        }
        return value;
    }
    
    public static int getHash(Board board, Color turn){
        int hash = pwrT[turn.ordinal() + 1];
        for(Pair pos : Board.positions){
            Piece piece = board.getPiece(pos);
            if(piece != null){
                int res = mul(pwrL[pos.l + 1], pwrR[pos.r + 1]);
                res = mul(res, pwrP[piece.type.ordinal() + 1]);
                hash = hash ^ res;
            }
        }
        return hash;
    }

    public void addMemory(Board board, Color turn, Movement movement){
        memory.put(getHash(board, turn), movement);
    }

    public void readMemory(){
        memory = new HashMap<>();
        Scanner reader;
        try {
            reader = new Scanner(new File(memoryFile));
        } catch (FileNotFoundException e) { throw new RuntimeException(e); }
        while(reader.hasNextLine()){
            String[] line = reader.nextLine().split(" ");
            int hash = Integer.parseInt(line[0]);
            Pair source = new Pair(Integer.parseInt(line[1]), Integer.parseInt(line[2]));
            Pair target = new Pair(Integer.parseInt(line[3]), Integer.parseInt(line[4]));
            Movement movement = new Movement(source, target);
            memory.put(hash, movement);
        }
        reader.close();
    }

    public void saveMemory(){
        PrintWriter writer;
        try {
            writer = new PrintWriter(memoryFile);
        } catch (FileNotFoundException e) { throw new RuntimeException(e); }
        for(Map.Entry<Integer, Movement> entry : memory.entrySet()){
            writer.print(entry.getKey() + " ");
            writer.println(entry.getValue().toString());
        }
        writer.close();
    }

    public int getDepth() {
        return depth;
    }

    public Color getColor() {
        return color;
    }
}
