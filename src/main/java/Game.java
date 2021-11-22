import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;


public class Game {
    public Screen screen;
    public Arena arena;
    TextGraphics graphics;

    Game(Screen screen, Arena arena){
        this.screen = screen;
        this.arena = arena;
        graphics = screen.newTextGraphics();
    }

    private void draw() throws IOException {
        screen.clear();
        arena.draw(graphics);
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
