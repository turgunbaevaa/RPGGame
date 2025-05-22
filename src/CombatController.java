import java.util.*;

public class CombatController {
    private List<Hero> heroes;
    private Boss boss;
    private Mission mission;
    private int turn = 1;
    private Scanner scanner = new Scanner(System.in);

    public CombatController(List<Hero> heroes, Boss boss, Mission mission) {
        this.heroes = heroes;
        this.boss = boss;
        this.mission = mission;
    }

    public void start() {
        System.out.println("Mission: " + mission.getDescription());

        while (true) {
            System.out.println("\n--- Turn " + turn + " ---");

            for (Hero h : heroes) {
                if (!h.isAlive()) continue;

                System.out.println("\n" + h.getName() + " (HP: " + h.getHealth() + ")");
                System.out.println("[1] Attack | [2] Ability | [3] Skip");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1 -> h.attack(boss);
                    case 2 -> h.useAbility(heroes, boss);
                    case 3 -> System.out.println(h.getName() + " skipped the turn.");
                }
            }

            if (boss.isAlive()) {
                boss.attack(heroes);
            }

            printStatus();

            if (mission.isCompleted(heroes, boss, turn)) {
                System.out.println("\n✅ Mission Completed! Victory!");
                break;
            }

            if (heroes.stream().noneMatch(Hero::isAlive)) {
                System.out.println("\n💀 All heroes died. Game Over.");
                break;
            }

            turn++;
        }
    }

    private void printStatus() {
        System.out.println("\n--- Status ---");
        System.out.println("Boss: " + boss.getHealth() + " HP");
        for (Hero h : heroes) {
            System.out.println(h.getName() + ": " + h.getHealth() + " HP");
        }
    }
}