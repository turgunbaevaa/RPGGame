import java.util.List;

public class Healer extends Hero {
    int healingPower;

    public Healer(int healingPower, String name, int health, int damage) {
        super(name, health, damage);
        this.healingPower = healingPower;
    }

    @Override
    public void attack(Character enemy) {
        // Healer does not attack
    }

    @Override
    public void useAbility(List<Hero> heroes, Boss boss) {
        heal(heroes);
    }

    private void heal(List<Hero> heroes) {
        for (Hero hero : heroes) {
            if (hero != this && hero.getHealth() > 0) {
                hero.receiveHealing(healingPower);
                System.out.println(this.name + " healed " + hero.getName() + " for " + healingPower + " HP.");
            }
        }
    }
}
