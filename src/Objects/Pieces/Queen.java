package Objects.Pieces;

import Objects.Board;
import Objects.Color;
import Objects.Direction;
import Objects.Pair;

public class Queen extends Piece{

    public Queen(Color color, Pair pos){
        super(color, pos, PieceType.QUEEN);
    }

    public boolean canReach(Board board, Pair target) {
        Direction direction = Direction.getDirection(pos, target);
        return direction != null && direction.isPathClear(pos, target, board);
    }

    public Queen getCopy(){
        return new Queen(color, pos);
    }

}