package Objects.Pieces;

import Objects.Board;
import Objects.Color;
import Objects.Direction;
import Objects.Pair;

import java.util.ArrayList;

public class Knight extends Piece {

    public Knight(Color color, Pair pos){
        super(color, pos, PieceType.KNIGHT);
    }

    public boolean canReach(Board board, Pair target) {
        ArrayList<Pair> validTargets = Direction.getKnightMoves(pos);
        for(Pair validTarget : validTargets){
            if(validTarget.equals(target)){
                return true;
            }
        }
        return false;
    }

    public Knight getCopy(){
        return new Knight(color, pos);
    }
}