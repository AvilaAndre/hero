import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

public class Arena {
    private final int width;
    private final int height;
    private Hero hero;
    Arena(int width, int height){
        this.width = width;
        this.height = height;
    }

    public void draw(Screen screen){
        hero.draw(screen);
    }

    public boolean processKey(KeyStroke key, Screen screen) {
        if (key.getKeyType() == KeyType.Character) {
            System.out.println(key.getCharacter());
        }
        switch (key.getKeyType()) {
            case ArrowUp:
                moveHero(hero.moveUp());
                break;
            case ArrowDown:
                moveHero(hero.moveDown());
                break;
            case ArrowLeft:
                moveHero(hero.moveLeft());
                break;
            case ArrowRight:
                moveHero(hero.moveRight());
                break;
            case EOF:
                return false;
            case Character:
                switch (key.getCharacter()) {
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

    private void moveHero(Position position) {
        if (canHeroMove(position)){
            hero.setPosition(position);
        }
    }

    private boolean canHeroMove(Position position){
        if (position.getX() < 0 || position.getX() > width){
            return false;
        } else if (position.getY() < 0 || position.getY() > height){
            return false;
        }
        return true;
    }
}
