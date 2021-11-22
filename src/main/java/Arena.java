import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Arena {
    private final int width;
    private final int height;
    private final Element hero;
    private List<Element> walls;
    private List<Element> coins;
    private List<Element> monsters;
    private int moves = 0;
    enum GAME_STATE {
        ON,
        OVER
    }
    private GAME_STATE GameState = GAME_STATE.ON;


    Arena(int width, int height, Screen screen){
        this.width = width;
        this.height = height;
        hero = new Hero(10, 10);
        this.walls = createWalls();
        this.coins = createCoins();
        this.monsters = spawnMonsters();
    }

    private List<Element> createWalls() {
        List<Element> walls = new ArrayList<>();
        for (int c = 0; c < width; c++) {
            walls.add(new Wall(c, 0));
            walls.add(new Wall(c, height - 1));
        }
        for (int r = 1; r < height - 1; r++) {
            walls.add(new Wall(0, r));
            walls.add(new Wall(width - 1, r));
        }
        return walls;
    }

    private List<Element> createCoins() {
        Random random = new Random();
        ArrayList<Element> coins = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            coins.add(new Coin(random.nextInt(width - 2) + 1, random.nextInt(height - 2) + 1));
        coins.removeIf(coin -> coin.getPosition().equals(hero.getPosition()));
        for (Element wall : walls) {
            coins.removeIf(coin -> coin.getPosition().equals(wall.getPosition()));
        }
        return coins;
    }

    private List<Element> spawnMonsters() {
        Random random = new Random();
        ArrayList<Element> monsters = new ArrayList<>();
        for (int i = 0; i < 15; i++)
            monsters.add(new Monster(random.nextInt(width - 2) + 1, random.nextInt(height - 2) + 1));
        monsters.removeIf(monster -> monster.getPosition().equals(hero.getPosition()));
        for (Element wall : walls) {
            monsters.removeIf(monster -> monster.getPosition().equals(wall.getPosition()));
        }
        return monsters;
    }

    public void draw(TextGraphics graphics){
        graphics.setBackgroundColor(TextColor.Factory.fromString("#5577dd"));
        graphics.fillRectangle(new TerminalPosition(0, 0), new TerminalSize(width, height), ' ');
        hero.draw(graphics);
        for (Element wall : walls){
            wall.draw(graphics);
        }

        for (Element coin : coins){
            coin.draw(graphics);
        }

        for (Element monster : monsters){
            monster.draw(graphics);
        }

        graphics.setBackgroundColor(TextColor.Factory.fromString("#f7ffc9"));
        graphics.fillRectangle(new TerminalPosition(0, height), new TerminalSize(width, height+2), ' ');
        graphics.setForegroundColor(TextColor.Factory.fromString("#960000"));
        graphics.enableModifiers(SGR.BOLD);
        graphics.putString(new TerminalPosition(0, height), "COINS LEFT:" + coins.size() + "    MOVES: " + moves);
        if (GameState == GAME_STATE.OVER){
            graphics.setForegroundColor(TextColor.Factory.fromString("#660000"));
            graphics.enableModifiers(SGR.BOLD);
            graphics.putString(new TerminalPosition(width/2 - 6, height+1), "GAME OVER");
        }
    }

    public boolean processKey(KeyStroke key, Screen screen) {
        if (key.getKeyType() == KeyType.Character) {
            System.out.println(key.getCharacter());
        }
        if (GameState == GAME_STATE.ON) {
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
                    if (key.getCharacter() == 'q') {
                        try {
                            screen.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                default:
                    System.out.println("Not an option");
            }
        }
        return true;
    }

    private int monsterTurn = 0;

    private void moveHero(Position position) {
        if (canHeroMove(position)){
            hero.setPosition(position);
            moves++;
            verifyMonsterCollisions();
        }
        retrieveCoins();
        if (monsterTurn == 1) {
            moveMonsters();
            monsterTurn = 0;
        }
        else{
            monsterTurn++;
        }
        verifyMonsterCollisions();
    }

    private boolean canHeroMove(Position position){
        for (Element wall : walls) {
            if (wall.getPosition().equals(position)) {
                return false;
            }
        }
        return true;
    }

    private void retrieveCoins(){
        for (Element coin : coins) {
            if (coin.getPosition().equals(hero.getPosition())) {
                coins.remove(coin);
                if (coins.size() == 0) this.GameState = GAME_STATE.OVER;
                return;
            }
        }
    }

    private void verifyMonsterCollisions() {
        for (Element monster :  monsters) {
            if (monster.getPosition().equals(hero.getPosition())) {
                this.GameState = GAME_STATE.OVER;
                break;
            }
        }
    }

    enum DIRECTION {
        _NONE,
        _LEFT,
        _RIGHT,
        _UP,
        _DOWN
    }

    private void moveMonsters(){
        for (Element monster : monsters){
            int diff = -1;
            int dist = 0;
            DIRECTION dir = DIRECTION._NONE;
            dist = monster.getPosition().getX() - hero.getPosition().getX();
            if (dist < 0) {
                boolean valid = true;
                for (Element wall : walls) {
                    if (new Position(monster.getPosition().getX()+1, monster.getPosition().getY()).equals(wall.getPosition())) {
                        valid = false;
                        break;
                    }
                }
                for (Element enemy : monsters) {
                    if (new Position(monster.getPosition().getX()+1, monster.getPosition().getY()).equals(enemy.getPosition())) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    diff = -dist;
                    dir = DIRECTION._RIGHT;
                }
            }
            if (dist > 0) {
                boolean valid = true;
                for (Element wall : walls) {
                    if (new Position(monster.getPosition().getX()-1, monster.getPosition().getY()).equals(wall.getPosition())) {
                        valid = false;
                        break;
                    }
                }
                for (Element enemy : monsters) {
                    if (new Position(monster.getPosition().getX()-1, monster.getPosition().getY()).equals(enemy.getPosition())) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    diff = dist;
                    dir = DIRECTION._LEFT;
                }
            }
            dist = monster.getPosition().getY() - hero.getPosition().getY();
            if (dist < 0) {
                boolean valid = true;
                for (Element wall : walls) {
                    if (new Position(monster.getPosition().getX(), monster.getPosition().getY()+1).equals(wall.getPosition())) {
                        valid = false;
                        break;
                    }
                }
                for (Element enemy : monsters) {
                    if (new Position(monster.getPosition().getX(), monster.getPosition().getY()+1).equals(enemy.getPosition())) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    if (diff < -dist) {
                        diff = -dist;
                        dir = DIRECTION._DOWN;
                    }
                }
            }
            if (dist > 0) {
                boolean valid = true;
                for (Element wall : walls) {
                    if (new Position(monster.getPosition().getX(), monster.getPosition().getY()-1).equals(wall.getPosition())) {
                        valid = false;
                        break;
                    }
                }
                for (Element enemy : monsters) {
                    if (new Position(monster.getPosition().getX(), monster.getPosition().getY()-1).equals(enemy.getPosition())) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    if (diff < dist) {
                        diff = dist;
                        dir = DIRECTION._UP;
                    }
                }
            }
            if (monster.getPosition().equals(hero.getPosition())){
                dir = DIRECTION._NONE;
            }

            switch (dir){
                case _NONE : break;
                case _LEFT :
                    monster.setPosition(monster.moveLeft());
                    break;
                case _RIGHT :
                    monster.setPosition(monster.moveRight());
                    break;
                case _UP :
                    monster.setPosition(monster.moveUp());
                    break;
                case _DOWN :
                    monster.setPosition(monster.moveDown());
                    break;
            }
        }
    }
}
