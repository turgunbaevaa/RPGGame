import java.util.List;

public class Healer extends Hero {
    int healingPower;

    Healer(int healingPower, String name, int health, int damage) {
        super(name, health, damage);
        this.healingPower = healingPower;
    }

    @Override
    public void useAbility(List<Hero> heroes, Boss boss) {
        for (Hero hero : heroes) {
            if (hero != this && hero.getHealth() > 0) {
                hero.receiveHealing(healingPower);
            }
        }
    }

    @Override
    public void attack(Character enemy) {

    }

    public void heal(List<Hero> heroes) {
        for (Hero hero : heroes) {
            if (hero.getHealth() > 0 && hero != this) {
                hero.receiveHealing(healingPower);
            }
        }
    }
}
