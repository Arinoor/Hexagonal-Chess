package Objects.Pieces;

import Objects.Board;
import Objects.Color;
import Objects.Direction;
import Objects.Pair;

public class King extends Piece {

    public King(Color color, Pair pos){
        super(color, pos, PieceType.KING);
    }

    public boolean canReach(Board board, Pair target) {
        Direction direction = Direction.getDirection(pos, target);
        if(direction == null){
            return false;
        }
        int steps = direction.countSteps(pos, target);
        return steps == 1;
    }

    public King getCopy(){
        return new King(color, pos);
    }

}