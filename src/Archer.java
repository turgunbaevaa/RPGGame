import java.util.List;
import java.util.Random;

public class Archer extends Hero {
    private Random random = new Random();

    public Archer(String name, int health, int damage) {
        super(name, health, damage);
    }

    @Override
    public void attack(Boss boss) {
        boss.receiveDamage(damage);
        System.out.println(name + " shot Boss for " + damage);
    }

    @Override
    public void useAbility(List<Hero> allies, Boss boss) {
        if (random.nextBoolean()) {
            System.out.println(name + " used Double Shot!");
            attack(boss);
            attack(boss);
        } else {
            System.out.println(name + " tried to use Double Shot but failed.");
        }
    }
}