import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

import static com.googlecode.lanterna.input.KeyType.ArrowDown;
import static com.googlecode.lanterna.input.KeyType.ArrowUp;

public class Game {
    public Screen screen;
    private int x = 10;
    private int y = 10;

    private void draw() throws IOException {
        screen.clear();
        screen.setCharacter(x, y, TextCharacter.fromCharacter('X')[0]);
        screen.refresh();
    }

    private boolean processKey(KeyStroke key) {
        if (key.getKeyType() == KeyType.Character){
            System.out.println(key.getCharacter());
        }
        switch (key.getKeyType()){
            case ArrowUp:
                this.y -= 1;
                break;
            case ArrowDown:
                this.y += 1;
                break;
            case ArrowLeft:
                this.x -= 1;
                break;
            case ArrowRight:
                this.x += 1;
                break;
            case EOF:
                return false;
            case Character:
                switch (key.getCharacter()){
                    case 'q':
                        try {
                            screen.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
            default:
                System.out.println("Not an option");
        }
        return true;
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
