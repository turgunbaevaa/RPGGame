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
    public void useAbility(List<Hero> heroes, Boss boss) {
        Hero target = chooseHealingTarget(heroes);
        if (target != null) {
            target.receiveHealing(healingPower);
            System.out.println(this.name + " healed " + target.getName() + " for " + healingPower + " HP.");
        } else {
            System.out.println(this.name + " никого не лечил — нет подходящих целей.");
        }
    }

    private Hero chooseHealingTarget(List<Hero> heroes) {
        List<Hero> candidates = heroes.stream()
                .filter(h -> h != this && h.isAlive() && h.getHealth() < 1000)
                .toList();

        if (candidates.isEmpty()) return null;

        System.out.println("Кого вы хотите вылечить?");
        for (int i = 0; i < candidates.size(); i++) {
            Hero h = candidates.get(i);
            System.out.println("[" + (i + 1) + "] " + h.getName() + " (HP: " + h.getHealth() + ")");
        }

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        return (choice >= 1 && choice <= candidates.size()) ? candidates.get(choice - 1) : null;
    }
}