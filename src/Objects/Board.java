package Objects;

import Objects.Pieces.Pawn;
import Objects.Pieces.Piece;
import Objects.Pieces.PieceType;
import Objects.Pieces.Queen;

import java.util.ArrayList;

public class Board {

    public static final int size;
    Cell[][] table;
    public static final ArrayList<Pair> positions;

    static {
        size = 11;
        positions = new ArrayList<>();
        for(int l = 1; l <= size; l ++){
            for(int r = 1; r <= size; r ++){
                Pair pos = new Pair(l, r);
                if(isInBoard(pos)){
                    positions.add(pos);
                }
            }
        }
    }

    public static boolean isInBoard(Pair pos){
        int l = pos.l;
        int r = pos.r;
        boolean isInBoard = Math.max(l, r) <= Board.size && Math.min(l, r) >= 1;
        isInBoard &= r >= l - 5 && r <= l + 5;
        return isInBoard;
    }


    public Board(Cell[][] table){
        this.table = table;
    }


    public boolean isValidMove(Color turn, Movement movement){
        Piece sourcePiece = getPiece(movement.getSource());
        Piece targetPiece = getPiece(movement.getTarget());
        if(sourcePiece == null || sourcePiece.color != turn || targetPiece != null && targetPiece.color == turn)
            return false;
        if(!sourcePiece.canReach(this, movement.getTarget()))
            return false;
        Board testBoard = new Board(getTableCopy());
        testBoard.makeMove(movement);
        return !testBoard.isChecked(turn);
    }

    public Piece makeMove(Movement movement){
        Cell sourceCell = getCell(movement.getSource());
        Cell targetCell = getCell(movement.getTarget());
        Piece sourcePiece = getPiece(movement.getSource());
        Piece targetPiece = getPiece(movement.getTarget());
        Piece deadPiece = null;

        if(sourcePiece.type == PieceType.PAWN){
            Pawn deadPawn = ((Pawn) sourcePiece).getEnPassantPawn(this, movement.getTarget());
            if(deadPawn != null) {
                Cell cell = getCell(deadPawn.pos);
                cell.setPiece(null);
                deadPawn.makeDead();
                deadPiece = deadPawn;
            }
        }

        sourcePiece.moveTo(movement.getTarget());
        sourceCell.setPiece(null);
        targetCell.setPiece(sourcePiece);


        if(targetPiece != null){
            targetPiece.makeDead();
            deadPiece = targetPiece;
        }

        clearEnPassant(sourcePiece);

        return deadPiece;
    }

    public boolean isChecked(Color color){
        Pair kingPos = null;
        for(Pair pos : positions){
            Piece piece = getPiece(pos);
            if(piece != null && piece.type == PieceType.KING && piece.color == color){
                kingPos = pos;
            }
        }
        for(Pair source : positions){
            Piece piece = getPiece(source);
            if(piece != null && piece.color == color.getOpposite()){
                if(piece.canReach(this, kingPos))
                    return true;
            }
        }
        return false;
    }

    public boolean canNotMove(Color color){
        return getValidMovesCount(color) == 0;
    }

    public int getValidMovesCount(Color color){
        int count = 0;
        for(Pair source : positions){
            Piece piece = getPiece(source);
            if(piece != null && piece.color == color){
                for(Pair target : positions){
                    if(isValidMove(color, new Movement(source, target))){
                        count ++;
                    }
                }
            }
        }
        return count;
    }

    public void checkForPromotion(Pair pos){
        Piece piece = getPiece(pos);
        if(piece.type == PieceType.PAWN && pos.isPromotionPos(piece.color)){
            Piece queen = new Queen(piece.color, pos);
            getCell(pos).setPiece(queen);
        }
    }

    public boolean isDraw(){
        Color aloneKingColor = null;
        Color otherColor = null;
        if(countPiece(null, Color.WHITE) == 1){
            aloneKingColor = Color.WHITE;
            otherColor = Color.BLACK;
        }
        else if(countPiece(null, Color.BLACK) == 1){
            aloneKingColor = Color.BLACK;
            otherColor = Color.WHITE;
        }
        if(aloneKingColor == null){
            return false;
        }
        int pieceCount = countPiece(null, otherColor);
        int knightCount = countPiece(PieceType.KNIGHT, otherColor);
        int bishopCount = countPiece(PieceType.BISHOP, otherColor);
        return pieceCount == 1 || (pieceCount == 2 && (knightCount == 1 || bishopCount == 1) || (pieceCount == 3 && bishopCount == 2));
    }

    public int countPiece(PieceType type, Color color){
        int count = 0;
        for(Pair pos : positions){
            Piece piece = getPiece(pos);
            if(piece != null && piece.color == color){
                if(type == null || piece.type == type){
                    count ++;
                }
            }
        }
        return count;
    }

    void clearEnPassant(Piece movedPiece){
        for(Pair pos : positions){
            Piece piece = getPiece(pos);
            if(piece != null && piece.type == PieceType.PAWN && piece != movedPiece){
                ((Pawn) piece).setHasEnPassantSituation(false);
            }
        }
    }


    public Cell[][] getTableCopy(){
        Cell[][] copyTable = new Cell[size + 1][size + 1];
        for(int l = 1; l <= 11; l ++)
            for(int r = 1; r <= 11; r ++)
                copyTable[l][r] = table[l][r] == null ? null : table[l][r].getCopy();
        return copyTable;
    }

    public Cell getCell(Pair pos) {
        return table[pos.l][pos.r];
    }


    public Piece getPiece(Pair pos) {
        return table[pos.l][pos.r].piece;
    }
}