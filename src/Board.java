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
            System.err.println("Error: Attempt to delete a null unit or a unit with a null position.");
            return;
        }
        Position pos = unit.getPosition();
        if (isValidPosition(pos) && isEmpty(pos)) {
            grid[pos.getY()][pos.getX()] = unit;
            occupiedPositions.add(pos);
        } else {
            System.err.println("Error: Unable to place unit " + unit.getName() + " at " + pos.toString() + ". Position is invalid or occupied.");
        }
    }

    public void removeUnit(Unit unit) {
        if (unit == null || unit.getPosition() == null) {
            System.err.println("Error: Attempt to delete a null unit or a unit with a null position.");
            return;
        }
        Position pos = unit.getPosition();
        if (isValidPosition(pos) && grid[pos.getY()][pos.getX()] == unit) {
            grid[pos.getY()][pos.getX()] = null;
            occupiedPositions.remove(pos);
        }
    }

    // In Board.java
    public void updatePosition(Unit unit, Position newPos) {
        if (unit == null || newPos == null) {
            System.err.println("Error: Incorrect data for updating position.");
            return;
        }

        if (!isValidPosition(newPos)) {
            System.err.println("Error: New position " + newPos.toString() + " is invalid.");
            return;
        }

        // Check if the new position is occupied by another unit (excluding the unit itself)
        Unit unitAtNewPos = grid[newPos.getY()][newPos.getX()];
        if (unitAtNewPos != null && unitAtNewPos != unit) {
            System.err.println("Error: Position " + newPos.toString() + " occupied by another unit.");
            return;
        }

        // 1. Remove unit from old position
        Position oldPos = unit.getPosition();
        if (oldPos != null && isValidPosition(oldPos) && grid[oldPos.getY()][oldPos.getX()] == unit) {
            grid[oldPos.getY()][oldPos.getX()] = null;
            occupiedPositions.remove(oldPos);
        }
        // Note: The unit's oldPos can be null if it's placed for the very first time (though setup should handle this)

        // 2. Place unit in new position
        grid[newPos.getY()][newPos.getX()] = unit;
        occupiedPositions.add(newPos);
        unit.setPosition(newPos); // Crucial step: update the unit's internal position
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