import java.util.HashSet;
import java.util.Set;

public class Board {
    private final int SIZE = 10;
    private Unit[][] grid;
    private Set<Position> occupiedPositions; // To efficiently check for occupied spots

    public Board() {
        grid = new Unit[SIZE][SIZE];
        occupiedPositions = new HashSet<>();
    }

    public boolean isValidPosition(Position pos) {
        return pos.getX() >= 0 && pos.getX() < SIZE && pos.getY() >= 0 && pos.getY() < SIZE;
    }

    public boolean isEmpty(Position pos) {
        return isValidPosition(pos) && grid[pos.getY()][pos.getX()] == null;
    }

    public void placeUnit(Unit unit) {
        if (unit == null || unit.getPosition() == null) {
            System.err.println("Ошибка: Попытка разместить null юнит или юнит с null позицией.");
            return;
        }
        Position pos = unit.getPosition();
        if (isValidPosition(pos) && isEmpty(pos)) {
            grid[pos.getY()][pos.getX()] = unit;
            occupiedPositions.add(pos);
        } else {
            System.err.println("Ошибка: Невозможно разместить юнит " + unit.getName() + " на " + pos.toString() + ". Позиция недействительна или занята.");
        }
    }

    public void removeUnit(Unit unit) {
        if (unit == null || unit.getPosition() == null) {
            System.err.println("Ошибка: Попытка удалить null юнит или юнит с null позицией.");
            return;
        }
        Position pos = unit.getPosition();
        if (isValidPosition(pos) && grid[pos.getY()][pos.getX()] == unit) {
            grid[pos.getY()][pos.getX()] = null;
            occupiedPositions.remove(pos);
        }
    }

    public void updatePosition(Unit unit, Position newPos) {
        if (unit == null || unit.getPosition() == null || newPos == null) {
            System.err.println("Ошибка: Неверные данные для обновления позиции.");
            return;
        }

        if (!isValidPosition(newPos)) {
            System.err.println("Ошибка: Новая позиция " + newPos.toString() + " недействительна.");
            return;
        }

        // Check if the new position is occupied by another unit (excluding the unit itself)
        if (grid[newPos.getY()][newPos.getX()] != null && grid[newPos.getY()][newPos.getX()] != unit) {
            System.err.println("Ошибка: Позиция " + newPos.toString() + " занята другим юнитом.");
            return;
        }

        // Only remove from old position if it's the same unit
        Position oldPos = unit.getPosition();
        if (grid[oldPos.getY()][oldPos.getX()] == unit) {
            grid[oldPos.getY()][oldPos.getX()] = null;
            occupiedPositions.remove(oldPos);
        }

        grid[newPos.getY()][newPos.getX()] = unit;
        occupiedPositions.add(newPos);
        unit.setPosition(newPos);
    }

    public Unit getUnitAt(Position pos) {
        if (!isValidPosition(pos)) {
            return null;
        }
        return grid[pos.getY()][pos.getX()];
    }

    public boolean isInRange(Unit attacker, Unit target) {
        return attacker.getPosition().distanceTo(target.getPosition()) <= attacker.getRange();
    }

    public void printBoard() {
        System.out.println("   0 1 2 3 4 5 6 7 8 9");
        System.out.println("  ---------------------");
        for (int i = 0; i < SIZE; i++) {
            System.out.print(i + " |");
            for (int j = 0; j < SIZE; j++) {
                Unit unit = grid[i][j];
                if (unit != null) {
                    System.out.print(unit.getDisplaySymbol() + " "); // Use the new method!
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println("|");
        }
        System.out.println("  ---------------------");
    }
}