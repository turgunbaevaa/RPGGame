import java.util.List;

public abstract class Hero extends Character {
    public Hero(String name, int health, int damage) {
        super(name, health, damage);
    }

    public void receiveHealing(int healing) {
        this.health += healing;
    }

    public abstract void attack(Boss boss);
    public abstract void useAbility(List<Hero> allies, Boss boss);
}