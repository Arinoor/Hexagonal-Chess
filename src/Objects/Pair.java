package Objects;

public class Pair{

    public final int l, r;

    public Pair(int l, int r){
        this.l = l;
        this.r = r;
    }

    public boolean equals(Pair other){
        return this.l == other.l && this.r == other.r;
    }

    public Pair getCopy(){
        return new Pair(l, r);
    }

    public boolean isPromotionPos(Color color){
        return color == Color.WHITE ? Math.max(l, r) == 11 : Math.min(l, r) == 1;
    }

    public boolean isInitialPawnPos(Color color){
        return color == Color.WHITE ? Math.max(l, r) == 5 : Math.min(l, r) == 7;
    }

    public Pair getSymmetric(){
        return new Pair(12 - r, 12 - l);
    }

    public String toString(){
        return l + " " + r;
    }

}
