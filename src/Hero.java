import java.util.List;

public abstract class Hero extends Character {

    public Hero(String name, int health, int damage) {
        super(name, health, damage);
    }

    public abstract void useAbility(List<Hero> heroes, Boss boss);

    @Override
    public void receiveDamage(int damage) {
        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        }
    }

    public void receiveHealing(int healing) {
        this.health += healing;
        System.out.println(this.name + " has been healed for " + healing + ". Current health: " + this.health);
    }

    // Optionally override attack() here or in each subclass like Warrior, Tank, etc.
}
