package Objects.Pieces;

import Objects.Board;
import Objects.Pair;

public interface Reachability {

    boolean canReach(Board board, Pair destination);

}