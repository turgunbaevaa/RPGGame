import java.util.List;

public class Healer extends Hero {
    int healNum;

    Healer(int healNum, String name, int health, int damage) {
        super(name, health, damage);
        this.healNum = healNum;
    }

    public void heal(List<Hero> heroes) {
        for (Hero hero : heroes) {
            if (hero.getHealth() > 0 && hero != this) {
                hero.receiveHealing(healNum);
            }
        }
    }
}
