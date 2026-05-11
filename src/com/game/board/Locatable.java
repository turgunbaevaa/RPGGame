package com.game.board;

/**
 * Represents an object that has a position on the game board.
 */
public interface Locatable {
    Position getPosition();
    void setPosition(Position pos);
}
