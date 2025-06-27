import java.util.Scanner;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Scanner gameScanner = new Scanner(System.in);
        Random gameRandom = new Random();
        Board gameBoard = new Board();

        GameInput consoleInput = new ConsoleInput(gameScanner);
        GameOutput consoleOutput = new ConsoleOutput();
        UnitFactory unitFactory = new DefaultUnitFactory(gameRandom); // Instantiate the factory

        // Pass all dependencies to GameController
        GameController game = new GameController(gameBoard, gameRandom, consoleInput, consoleOutput, unitFactory);
        game.startGame();

        gameScanner.close();
    }
}