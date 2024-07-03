package hex_chess.graphics.listeners;

import java.io.File;

public interface EventListener {
    void onClick(int row, char col);

    void onLoad(File file);

    void onSave(File file);

    void onNewGame();
}
