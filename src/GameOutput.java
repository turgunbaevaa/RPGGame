import java.util.List;

public interface GameOutput {
    void displayMessage(String message);
    void printBoard(Board board, List<Hero> heroes, List<Enemy> enemies); // Needs actual lists for detailed stats
    void displayUnitStats(List<Hero> heroes, List<Enemy> enemies);
    void displayShop(int gold, List<Hero> heroes); // Might need more parameters
    void displayAvailableTargets(List<? extends Unit> targets, Unit attacker); // Generic for both hero/enemy targets
    void displayError(String message);
    // Add any other specific display needs
}