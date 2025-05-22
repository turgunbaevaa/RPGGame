import java.util.List;

public class Tank extends Hero {
    public Tank(String name, int health, int damage) {
        super(name, health, damage);
    }

    @Override
    public void attack(Boss boss) {
        boss.receiveDamage(damage);
        System.out.println(name + " slammed Boss for " + damage);
    }

    @Override
    public void useAbility(List<Hero> allies, Boss boss) {
        // Reflect damage in boss's attack method, handled elsewhere if needed
    }
}