package com.game.board;

public class Board {
    private static final int SIZE = 10;
    private final Locatable[][] grid;

    public Board() {
        this.grid = new Locatable[SIZE][SIZE];
    }

    public boolean isValidPosition(Position pos) {
        return pos != null && pos.getX() >= 0 && pos.getX() < SIZE && pos.getY() >= 0 && pos.getY() < SIZE;
    }

    public boolean isEmpty(Position pos) {
        return isValidPosition(pos) && grid[pos.getY()][pos.getX()] == null;
    }

    public void place(Locatable unit) {
        if (unit == null || unit.getPosition() == null) {
            throw new IllegalArgumentException("Cannot place a null unit or a unit without a position.");
        }
        Position pos = unit.getPosition();

        if (!isValidPosition(pos)) {
            printError("Error: Position %s is invalid.%n", pos);
        } else if (!isEmpty(pos)) {
            // Displaying the unit that is occupying the spot for better debugging
            Locatable blockingUnit = grid[pos.getY()][pos.getX()];
            printError("Error: Position %s is occupied by %s (%s).%n",
                    pos, blockingUnit.toString(), blockingUnit.getClass().getSimpleName());
        } else {
            grid[pos.getY()][pos.getX()] = unit;
        }
    }

    public void removeUnit(Locatable unit) {
        if (unit == null || unit.getPosition() == null) {
            printError("Error: Cannot remove null unit or unit with null position.%n");
            return;
        }
        Position pos = unit.getPosition();

        if (isValidPosition(pos) && grid[pos.getY()][pos.getX()] == unit) {
            grid[pos.getY()][pos.getX()] = null;
        }
    }

    public void updatePosition(Locatable unit, Position newPos) {
        if (unit == null || newPos == null) {
            printError("Error: Incorrect data for updating position.%n");
            return;
        }

        if (!isValidPosition(newPos)) {
            printError("Error: New position %s is invalid.%n", newPos);
            return;
        }

        Position oldPos = unit.getPosition();
        Locatable unitAtNewPos = grid[newPos.getY()][newPos.getX()];
        if (unitAtNewPos != null && unitAtNewPos != unit) {
            printError("Error: Position %s occupied by another unit: %s.%n",
                    newPos, unitAtNewPos);
            return;
        }
        if (oldPos != null && isValidPosition(oldPos) && grid[oldPos.getY()][oldPos.getX()] == unit) {
            grid[oldPos.getY()][oldPos.getX()] = null;
        }
        grid[newPos.getY()][newPos.getX()] = unit;
        unit.setPosition(newPos);
    }

    public Locatable getLocatableAt(Position pos) {
        if (!isValidPosition(pos)) {
            return null;
        }
        return grid[pos.getY()][pos.getX()];
    }

    private static void printError(String format, Object... args) {
        System.err.printf(format, args);
    }
}