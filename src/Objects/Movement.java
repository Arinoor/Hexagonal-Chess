package Objects;

public class Movement implements Comparable<Movement>{
    Pair source;
    Pair target;
    double score;

    public Movement(){
        this.source = null;
        this.target = null;
    }

    public Movement(Pair source, Pair target){
        this.source = source;
        this.target = target;
        this.score = 0;
    }
    
    public Movement(Pair source, Pair target, double score){
        this.source = source;
        this.target = target;
        this.score = score;
    }

    public boolean equals(Movement other){
        return this.source.equals(other.source) && this.target.equals(other.target);
    }

    @Override
    public int compareTo(Movement other) {
        double res = this.score - other.getScore();
        if(res == 0)
            return 0;
        return res > 0 ? 1 : -1;
    }

    public Pair getSource() {
        return source;
    }

    public Pair getTarget() {
        return target;
    }

    public double getScore() {
        return score;
    }

    public void setSource(Pair source) {
        this.source = source;
    }

    public void setTarget(Pair target) {
        this.target = target;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String toString(){
        return source.toString() + " " + target.toString();
    }

    
}
