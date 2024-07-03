package Objects;

public enum GameStatus {
    RUNNING,
    PAT,
    DRAW,
    WHITE,
    BLACK;

    public boolean isFinished(){
        return this != RUNNING;
    }
}
