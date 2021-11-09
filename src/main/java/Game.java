import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;


public class Game {
    public Screen screen;
    public Arena arena;

    private void draw() throws IOException {
        screen.clear();
        arena.draw(screen);
        screen.refresh();
    }

    private boolean processKey(KeyStroke key) {
        return arena.processKey(key, screen);
    }

    public void run() throws IOException {
        while (true) {
            this.draw();
            KeyStroke key = screen.readInput();
            if (!processKey(key)){
                break;
            }
        }
    }
}
