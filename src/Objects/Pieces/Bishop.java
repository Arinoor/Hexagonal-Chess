package Objects.Pieces;

import Objects.Board;
import Objects.Color;
import Objects.Direction;
import Objects.Pair;

public class Bishop extends Piece {

    public Bishop(Color color, Pair pos){
        super(color, pos, PieceType.BISHOP);
    }

    public boolean canReach(Board board, Pair target){
        Direction direction = Direction.getDirection(pos, target);
        return direction != null && direction.isDiagonal() && direction.isPathClear(pos, target, board);
    }

    public Bishop getCopy(){
        return new Bishop(color, pos);
    }

}