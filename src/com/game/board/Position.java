package com.game.board;

/**
 * @param x is final, and not changeable
 * @param y is final, and not changeable
 */
public record Position(int x, int y) {

    public int distanceTo(Position other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

}