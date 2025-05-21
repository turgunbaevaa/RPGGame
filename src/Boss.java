import java.util.Random;

public class Boss extends Character {
    public int superDamage;
    private Random random = new Random();

    public Boss(int superDamage, String name, int health, int damage) {
        super(name, health, damage);
        this.superDamage = superDamage;
    }

    @Override
    public void attack(Character enemy) {

    }

    @Override
    public void receiveDamage(int damage) {

    }

    public boolean attemptSuperAttack() {
        int randomChance = random.nextInt(3); // 0, 1, or 2
        return randomChance <= superDamage;
    }

    public int getSuperDamage() {
        return superDamage;
    }
}
