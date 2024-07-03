package Objects.Pieces;

import Objects.Board;
import Objects.Color;
import Objects.Direction;
import Objects.Pair;

public class Pawn extends Piece {

    boolean hasEnPassantSituation;

    public Pawn(Color color, Pair pos, boolean hasEnPassantSituation){
        super(color, pos, PieceType.PAWN);
        this.hasEnPassantSituation = hasEnPassantSituation;
    }

    public boolean canReach(Board board, Pair target) {
        Direction direction = Direction.getDirection(pos, target);
        if(direction == null || !direction.isValidDirectionForPawn(color)) {
            return false;
        }

        Piece targetPiece = board.getPiece(target);
        int maxOfVerticalMove = 1 + (pos.isInitialPawnPos(color) ? 1 : 0);
        int steps = direction.countSteps(pos, target);

        if(direction.isVertical()){
            return targetPiece == null && steps <= maxOfVerticalMove && direction.isPathClear(pos, target, board);
        }
        else{
            boolean isValidSimpleMove = targetPiece != null && targetPiece.color == color.getOpposite();
            boolean isEnPassant = getEnPassantPawn(board, target) != null;
            return (isValidSimpleMove || isEnPassant) && steps == 1;
        }
    }

    public Pawn getEnPassantPawn(Board board, Pair target){
        Direction direction = Direction.getDirection(pos, target);
        if(direction == null || !direction.isValidDirectionForPawn(color) || direction.isVertical())
            return null;
        Direction EnPassantDirection = direction.getEnPassantDirection();
        Pair opponentPawnPos = EnPassantDirection.move(pos, 1);
        Piece opponentPawn = Board.isInBoard(opponentPawnPos) ? board.getPiece(opponentPawnPos) : null;
        if(opponentPawn != null && opponentPawn.color == color.getOpposite() && opponentPawn.type == PieceType.PAWN){
            if(((Pawn) opponentPawn).hasEnPassantSituation()){
                return (Pawn) opponentPawn;
            }
        }
        return null;
    }

    public void moveTo(Pair pos) {
        if(this.pos.isInitialPawnPos(color) && Direction.getDirection(this.pos, pos).countSteps(this.pos, pos) == 2){
            hasEnPassantSituation = true;
        }
        super.moveTo(pos);
    }

    public Pawn getCopy() {
        return new Pawn(color, pos, hasEnPassantSituation);
    }

    public boolean hasEnPassantSituation() {
        return hasEnPassantSituation;
    }

    public void setHasEnPassantSituation(boolean hasEnPassantSituation) {
        this.hasEnPassantSituation = hasEnPassantSituation;
    }
}