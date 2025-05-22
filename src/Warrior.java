import java.util.List;

public class Warrior extends Hero {
    public Warrior(String name, int health, int damage) {
        super(name, health, damage);
    }

    @Override
    public void attack(Boss boss) {
        boss.receiveDamage(damage);
        System.out.println(name + " attacked Boss for " + damage);
    }

    @Override
    public void useAbility(List<Hero> allies, Boss boss) {
        int bonus = 30;
        boss.receiveDamage(bonus);
        System.out.println(name + " used Rage and dealt extra " + bonus + " damage!");
    }
}