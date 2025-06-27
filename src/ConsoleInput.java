import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsoleInput implements GameInput {
    private Scanner scanner;

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
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Неверный ввод. Пожалуйста, введите число.");
                scanner.nextLine(); // Consume the invalid input
            }
        }
    }

    @Override
    public String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}