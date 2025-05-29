import java.util.*;

public class Healer extends Hero {
    private int healingPower;

    public Healer(String name, int health, int damage, int healingPower) {
        super(name, health, damage);
        this.healingPower = healingPower;
    }

    @Override
    public void attack(Boss boss) {
        // Healer не атакует
    }

    @Override
    public void useAbility(List<Hero> heroes, Boss boss, Scanner scanner) {
        Hero target = chooseHealingTarget(heroes, scanner);
        if (target != null) {
            target.receiveHealing(healingPower);
            System.out.println(this.name + " healed " + target.getName() + " for " + healingPower + " HP.");
        } else {
            System.out.println(this.name + " has not treated anyone - no suitable targets.");
        }
    }

    private Hero chooseHealingTarget(List<Hero> heroes, Scanner scanner) {
        List<Hero> candidates = heroes.stream()
                .filter(h -> h != this && h.isAlive() && h.getHealth() < 800)
                .toList();

        if (candidates.isEmpty()) return null;

        while (true) {
            System.out.println("Whom do you want to heal?");
            for (int i = 0; i < candidates.size(); i++) {
                Hero h = candidates.get(i);
                System.out.println("[" + (i + 1) + "] " + h.getName() + " (HP: " + h.getHealth() + ")");
            }

            System.out.println("[0] No one to treat");
            System.out.print("Enter the number: ");
            int choice = scanner.nextInt();

            if (choice == 0) {
                System.out.println("You chose not to treat anyone.");
                return null;
            } else if (choice >= 1 && choice <= candidates.size()) {
                return candidates.get(choice - 1);
            } else {
                System.out.println("You have selected the wrong number, enter the correct one.");
            }
        }
    }
}