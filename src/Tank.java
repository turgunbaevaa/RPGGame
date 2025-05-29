import java.util.List;
import java.util.Scanner;

public class Tank extends Hero {
    private boolean isTaunting = false;

    public Tank(String name, int health, int damage) {
        super(name, health, damage);
    }

    public boolean isTaunting() {
        return isTaunting;
    }

    public void setTaunting(boolean taunting) {
        this.isTaunting = taunting;
    }

    @Override
    public void attack(Boss boss) {
        boss.receiveDamage(damage);
        System.out.println(name + " slammed Boss for " + damage);
    }

    @Override
    public void useAbility(List<Hero> allies, Boss boss, Scanner scanner) {
        setTaunting(true);
        System.out.println(name + " used Taunt! Boss will attack them next turn.");
    }
}