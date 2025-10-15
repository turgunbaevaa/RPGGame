package com.game.core;
import com.game.board.Board;
import com.game.factory.UnitFactory;
import com.game.factory.DefaultUnitFactory;
import com.game.io.GameInput;
import com.game.io.ConsoleInput;
import com.game.io.GameOutput;
import com.game.io.ConsoleOutput;

import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner gameScanner = new Scanner(System.in);
        Random gameRandom = new Random();
        Board gameBoard = new Board();

        GameInput consoleInput = new ConsoleInput(gameScanner);
        GameOutput consoleOutput = new ConsoleOutput();
        UnitFactory unitFactory = new DefaultUnitFactory();

        GameController game = new GameController(gameBoard, gameRandom, consoleInput, consoleOutput, unitFactory);
        game.startGame();

        gameScanner.close();
    }
}