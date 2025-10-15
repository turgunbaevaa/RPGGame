package com.game.io;
import com.game.board.Position;
import com.game.exceptions.GameException;

public interface GameInput {
    int getIntInput(String prompt);
    String getStringInput(String prompt);
    Position getPositionInput(String prompt) throws GameException;
}