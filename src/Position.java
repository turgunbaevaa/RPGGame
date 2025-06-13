public class Position {
    public final int x; // Сделаем final, чтобы объект был неизменяемым
    public final int y; // Сделаем final, чтобы объект был неизменяемым

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int distanceTo(Position other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }

    // Добавим геттеры, хотя для final public полей это не строго необходимо,
    // это соответствует шаблону для неизменяемых объектов.
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y);
    }
}