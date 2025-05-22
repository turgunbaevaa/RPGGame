import java.util.List;
import java.util.Random;

public class Boss extends Character {
    private Random random = new Random();

    public Boss(String name, int health, int damage) {
        super(name, health, damage);
    }

    public void attack(List<Hero> heroes) {
        for (Hero h : heroes) {
            if (h.isAlive()) {
                int dmg = damage;
                boolean critical = random.nextInt(100) < 20;
                if (critical) dmg *= 2;

                h.receiveDamage(dmg);
                System.out.println("Boss hits " + h.getName() + " for " + dmg + (critical ? " (CRITICAL!)" : ""));
            }
        }
    }
}