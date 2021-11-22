public class Wall extends Element{
    private Position position;

    Wall(int x, int y) {
        super(x, y);
        this.symbol = "#";
        this.color = "#00FF44";
    }
}
