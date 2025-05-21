import java.util.List;

public class Warrior extends Hero{

    public Warrior(String name, int health, int damage) {
        super(name, health, damage);
    }

    @Override
    public void useAbility(List<Hero> heroes, Boss boss) {
        int bonus = 30;
        boss.receiveDamage(bonus);
        System.out.println(name + " uses Rage and hits Boss for " + bonus);
    }

    @Override
    public void attack(Character enemy) {
        if (this.isAlive()) {
            enemy.receiveDamage(this.damage);
            System.out.println(this.name + " attacked " + enemy.getName() + " for " + this.damage + " damage.");
        }
    }
}
