package Objects;

import Objects.Pieces.Piece;

import java.util.ArrayList;

public enum Direction {
    UP (1, 1),
    DOWN (-1, -1),
    UP_RIGHT (1, 0),
    DOWN_RIGHT (0, -1),
    UP_LEFT (0, 1),
    DOWN_LEFT (-1, 0),
    DIAGONAL_UP_RIGHT (2, 1),
    DIAGONAL_RIGHT (1, -1),
    DIAGONAL_DOWN_RIGHT (-1, -2),
    DIAGONAL_DOWN_LEFT (-2, -1),
    DIAGONAL_LEFT (-1, 1),
    DIAGONAL_UP_LEFT (1, 2);

    final Pair movement;
    final boolean isDiagonal;
    final boolean isStraight;
    final boolean isVertical;
    final int type; // Diagonal directions are type of 0 and straight directions are type of 1 or 2 based on how they change the color
    final static ArrayList<Direction> diagonalDirections = new ArrayList<>();
    final static ArrayList<Direction> straightDirections = new ArrayList<>();

    static {
        for(Direction direction : Direction.values()){
            if(direction.isDiagonal){
                diagonalDirections.add(direction);
            }
            else{
                straightDirections.add(direction);
            }
        }
    }

    public static Direction getDirection(Pair source, Pair target){
        Pair distance = new Pair(target.l - source.l, target.r - source.r);
        for (Direction direction : Direction.values()){
            // distance.l / movement.l == distance.r / movement.r
            if(distance.l * direction.movement.r == distance.r * direction.movement.l) {
                if (Integer.signum(distance.l) == Integer.signum(direction.movement.l)) {
                    if (Integer.signum(distance.r) == Integer.signum(direction.movement.r)) {
                        return direction;
                    }
                }
            }
        }
        return null;
    }

    public static ArrayList<Pair> getKnightMoves(Pair source){
        ArrayList<Pair> targets = new ArrayList<>();
        for(Direction direction : straightDirections){
            Pair intermediatePos = direction.move(source, 2);
            for(Direction otherDirection : straightDirections){
                if(!direction.isOppositeTo(otherDirection) && direction.type != otherDirection.type){
                    Pair target = otherDirection.move(intermediatePos, 1);
                    if(Board.isInBoard(target)) {
                        targets.add(target);
                    }
                }
            }
        }
        return targets;
    }

    Direction(int l, int r){
        this.movement = new Pair(l, r);
        this.isDiagonal = this.name().startsWith("DIAGONAL"); // or type == 0
        this.isStraight = !isDiagonal;
        this.isVertical = this.name().equals("UP") || this.name().equals("DOWN");
        this.type = (l + r + 3) % 3;
    }

    public Pair move(Pair pos, int x){
        int newL = pos.l + x * this.movement.l;
        int newR = pos.r + x * this.movement.r;
        return new Pair(newL, newR);
    }

    public int countSteps(Pair source, Pair target){
        int count = 0;
        Pair current = source.getCopy();
        while(!current.equals(target)){
            count ++;
            current = move(current, 1);
        }
        return count;
    }

    public boolean isPathClear(Pair source, Pair target, Board board){
        int steps = this.countSteps(source, target);
        Pair current = source.getCopy();
        for(int i = 0; i < steps - 1; i ++){
            current = this.move(current, 1);
            Piece piece = board.getPiece(current);
            if(piece != null)
                return false;
        }
        return true;
    }

    public boolean isValidDirectionForPawn(Color color){
        return switch (color) {
            case WHITE -> this.name().startsWith("UP");
            case BLACK -> this.name().startsWith("DOWN");
        };
    }

    public Direction getEnPassantDirection(){
        String[] separatedName = this.name().split("_");
        String vertical = separatedName[0].equals("UP") ? "DOWN" : "UP";
        String horizontal = separatedName[1];
        return Direction.valueOf(vertical + "_" + horizontal);
    }

    public boolean isOppositeTo(Direction otherDirection){
        int thisL = this.movement.l;
        int thisR = this.movement.r;
        int otherL = otherDirection.getMovement().l;
        int otherR = otherDirection.getMovement().r;
        boolean isOppositeL = thisL * -1 == otherL;
        boolean isOppositeR = thisR * -1 == otherR;
        return isOppositeL && isOppositeR;
    }

    public Pair getMovement() {
        return movement;
    }

    public boolean isDiagonal() {
        return isDiagonal;
    }

    public boolean isStraight() {
        return isStraight;
    }

    public boolean isVertical() {
        return isVertical;
    }
}