import java.util.List;
import java.util.Scanner;

public abstract class Hero extends Character {
    public Hero(String name, int health, int damage) {
        super(name, health, damage);
    }

    public void receiveHealing(int healing) {
        this.health += healing;
    }

    public abstract void attack(Boss boss);
    public abstract void useAbility(List<Hero> heroes, Boss boss, Scanner scanner);
}