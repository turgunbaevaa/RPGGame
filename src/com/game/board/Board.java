package com.game.board;

public class Board {
    private static final int SIZE = 10;
    private final Locatable[][] grid;

    public Board() {
        this.grid = new Locatable[SIZE][SIZE];
    }

    public boolean isValidPosition(Position pos) {
        if (pos == null) return false;
        return pos.x() >= 0 && pos.x() < SIZE && pos.y() >= 0 && pos.y() < SIZE;
    }

    public boolean isEmpty(Position pos) {
        return isValidPosition(pos) && grid[pos.y()][pos.x()] == null;
    }

    public void place(Locatable unit) {
        if (unit == null || unit.getPosition() == null) {
            throw new IllegalArgumentException("Cannot place a null unit or a unit without a position.");
        }
        Position pos = unit.getPosition();

        if (!isValidPosition(pos)) {
            System.err.printf("Error: com.game.board.Position %s is invalid.%n", pos);
        } else if (!isEmpty(pos)) {
            // Displaying the unit that is occupying the spot for better debugging
            Locatable blockingUnit = grid[pos.y()][pos.x()];
            System.err.printf("Error: com.game.board.Position %s is occupied by %s (%s).%n",
                    pos, blockingUnit.toString(), blockingUnit.getClass().getSimpleName());
        } else {
            grid[pos.y()][pos.x()] = unit;
        }
    }

    public void removeUnit(Locatable unit) {
        if (unit == null || unit.getPosition() == null) {
            System.err.printf("Error: Cannot remove null unit or unit with null position.%n");
            return;
        }
        Position pos = unit.getPosition();

        if (isValidPosition(pos) && grid[pos.y()][pos.x()] == unit) {
            grid[pos.y()][pos.x()] = null;
        }
    }

    public void updatePosition(Locatable unit, Position newPos) {
        if (unit == null || newPos == null) {
            System.err.printf("Error: Incorrect data for updating position.%n");
            return;
        }

        if (!isValidPosition(newPos)) {
            System.err.printf("Error: New position %s is invalid.%n", newPos);
            return;
        }

        Position oldPos = unit.getPosition();
        Locatable unitAtNewPos = grid[newPos.y()][newPos.x()];
        if (unitAtNewPos != null && unitAtNewPos != unit) {
            System.err.printf("Error: com.game.board.Position %s occupied by another unit: %s.%n",
                    newPos, unitAtNewPos);
            return;
        }
        if (oldPos != null && isValidPosition(oldPos) && grid[oldPos.y()][oldPos.x()] == unit) {
            grid[oldPos.y()][oldPos.x()] = null;
        }
        grid[newPos.y()][newPos.x()] = unit;
        unit.setPosition(newPos);
    }

    public Locatable getLocatableAt(Position pos) {
        if (!isValidPosition(pos)) {
            return null;
        }
        return grid[pos.y()][pos.x()];
    }
}