package com.game.io;
import com.game.board.Position;
import com.game.exceptions.GameException;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsoleInput implements GameInput {
    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public int getIntInput(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                int value = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                return value; // Successfully read an integer and returns it
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consumes the invalid input
            }
        }
    }

    @Override
    public String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    @Override
    public Position getPositionInput(String prompt) throws GameException {
        System.out.print(prompt);
        String coordsInput = scanner.nextLine().trim();
        String[] parts = coordsInput.split(" ");

        if (parts.length < 2) {
            throw new GameException("Incorrect coordinate format. Enter two numbers separated by a space (X Y).");
        }

        try {
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            return new Position(x, y);
        } catch (NumberFormatException e) {
            throw new GameException("Coordinates must be integers.");
        }
    }
}