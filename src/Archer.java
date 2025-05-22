import java.util.List;
import java.util.Random;

public class Archer extends Hero {
    private Random random;
    private int cooldown = 0;

    public Archer(String name, int health, int damage) {
        super(name, health, damage);
        this.random = new Random();
    }

    @Override
    public void useAbility(List<Hero> heroes, Boss boss) {
        if (!this.isAlive()) return;

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        boolean doubleAttack = random.nextInt(100) < 30;
        if (doubleAttack) {
            System.out.println(this.name + " used Piercing Shot and attacked twice!");
            this.attack(boss);
            this.attack(boss);
            cooldown = 2;
        } else {
            System.out.println(this.name + " tried to use Piercing Shot but failed.");
        }
    }

    @Override
    public void attack(Character target) {
        if (this.isAlive()) {
            target.receiveDamage(this.damage);
            System.out.println(this.name + " shot an arrow at " + target.getName() + " for " + this.damage + " damage.");
        }
    }
}