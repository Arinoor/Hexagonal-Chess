package Objects.Pieces;

import Objects.Board;
import Objects.Color;
import Objects.Direction;
import Objects.Pair;

public class Rook extends Piece{

    public Rook(Color color, Pair pos){
        super(color, pos, PieceType.ROOK);
    }

    public boolean canReach(Board board, Pair target) {
        Direction direction = Direction.getDirection(pos, target);
        return direction != null && direction.isStraight() && direction.isPathClear(pos, target, board);
    }

    public Rook getCopy(){
        return new Rook(color, pos);
    }

}