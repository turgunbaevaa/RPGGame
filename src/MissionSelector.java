import java.util.Scanner;

public class MissionSelector {
    public static Mission selectMission() {
        System.out.println("Выберите миссию:");
        System.out.println("[1] Выжить 5 ходов");
        System.out.println("[2] Убить Босса");

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        return switch (choice) {
            case 1 -> new SurviveMission(5);
            case 2 -> new KillBossMission();
            default -> new KillBossMission();
        };
    }
}