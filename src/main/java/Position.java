 public class Position {
    private int x;
    private int y;

    Position(int x, int y){
        setX(x);
        setY(y);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public String toString(){
        return "(" + x + ", " + y + ")";
    }

     @Override
     public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null) return false;
         if (getClass() != o.getClass()) return false;
         Position p = (Position) o;
         return x == p.getX() && y == p.getY();
     }
}