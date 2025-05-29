import java.util.*;

public class Game {
    public static void main(String[] args) {
        List<Hero> heroes = List.of(
                new Warrior("Warrior", 1000, 300),
                new Archer("Archer", 1000, 300),
                new Tank("Tank", 1000, 200),
                new Healer("Healer", 1000, 0, 400)
        );

        Boss boss = new Boss("Boss", 1000, 300);

        System.out.println("Select mission: [1] Kill Boss | [2] Survive 5 turns");
        Scanner scanner = new Scanner(System.in);
        int missionChoice = scanner.nextInt();

        Mission mission = (missionChoice == 2) ? new SurviveMission(5) : new KillBossMission();

        new CombatController(heroes, boss, mission).start();
    }
}