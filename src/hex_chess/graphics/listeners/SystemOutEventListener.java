package hex_chess.graphics.listeners;

import java.io.File;

public class SystemOutEventListener implements EventListener {

    @Override
    public void onClick(int row, char col) {
        System.out.printf("clicked -> row=%d, col=%c%n", row, col);
    }

    @Override
    public void onLoad(File file) {
        System.out.println(file);
    }

    @Override
    public void onSave(File file) {
        System.out.println(file);
    }

    @Override
    public void onNewGame() {
        System.out.println("new game");
    }
}
