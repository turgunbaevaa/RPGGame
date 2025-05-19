import java.util.Random;

public class Boss extends Hero {
    public int superDamage;
    private Random random = new Random(); // define the Random object

    public Boss(int superDamage, String name, int health, int damage) {
        super(name, health, damage);
        this.superDamage = superDamage;
    }

    // Better name: describes action
    public boolean attemptSuperAttack() {
        int randomChance = random.nextInt(3); // 0, 1, or 2
        return randomChance <= superDamage;
    }

    // Optional: actual getter for superDamage
    public int getSuperDamage() {
        return superDamage;
    }
}
