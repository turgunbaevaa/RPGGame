package com.game.board;
import com.game.units.Unit;

public class Board {
    private final int SIZE = 10;
    private final Unit[][] grid;

    public Board() {
        this.grid = new Unit[SIZE][SIZE];
    }

    public boolean isValidPosition(Position pos) {
        // Null check for robustness, though usually handled by callers
        if (pos == null) return false;
        return pos.x() >= 0 && pos.x() < SIZE && pos.y() >= 0 && pos.y() < SIZE;
    }

    public boolean isEmpty(Position pos) {
        return isValidPosition(pos) && grid[pos.y()][pos.x()] == null;
    }

    public void placeUnit(Unit unit) {
        if (unit == null || unit.getPosition() == null) {
            // Using printf for structured error logging
            System.err.printf("Error: Cannot place null unit or unit without position.%n");
            return;
        }
        Position pos = unit.getPosition();

        if (!isValidPosition(pos)) {
            System.err.printf("Error: com.game.board.Position %s is invalid.%n", pos);
        } else if (!isEmpty(pos)) {
            // Displaying the unit that is occupying the spot for better debugging
            Unit blockingUnit = grid[pos.y()][pos.x()];
            System.err.printf("Error: com.game.board.Position %s is occupied by %s (%s).%n",
                    pos, blockingUnit.getName(), blockingUnit.getClass().getSimpleName());
        } else {
            grid[pos.y()][pos.x()] = unit;
        }
    }

    public void removeUnit(Unit unit) {
        if (unit == null || unit.getPosition() == null) {
            System.err.printf("Error: Cannot remove null unit or unit with null position.%n");
            return;
        }
        Position pos = unit.getPosition();

        if (isValidPosition(pos) && grid[pos.y()][pos.x()] == unit) {
            grid[pos.y()][pos.x()] = null;
        }
    }

    public void updatePosition(Unit unit, Position newPos) {
        if (unit == null || newPos == null) {
            System.err.printf("Error: Incorrect data for updating position.%n");
            return;
        }

        if (!isValidPosition(newPos)) {
            System.err.printf("Error: New position %s is invalid.%n", newPos);
            return;
        }

        Position oldPos = unit.getPosition();

        // 1. Check if the new position is occupied by another unit (excluding the unit itself)
        Unit unitAtNewPos = grid[newPos.y()][newPos.x()];
        if (unitAtNewPos != null && unitAtNewPos != unit) {
            System.err.printf("Error: com.game.board.Position %s occupied by another unit: %s.%n",
                    newPos, unitAtNewPos.getName());
            return;
        }

        // 2. Remove unit from old position (safely using null/validity checks)
        if (oldPos != null && isValidPosition(oldPos) && grid[oldPos.y()][oldPos.x()] == unit) {
            grid[oldPos.y()][oldPos.x()] = null;
        }

        // 3. Place unit in new position
        grid[newPos.y()][newPos.x()] = unit;
        unit.setPosition(newPos);
    }

    public Unit getUnitAt(Position pos) {
        if (!isValidPosition(pos)) {
            return null;
        }
        return grid[pos.y()][pos.x()];
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
                    System.out.print(unit.getDisplaySymbol() + " ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println("|");
        }
        System.out.println("  ---------------------");
    }
}