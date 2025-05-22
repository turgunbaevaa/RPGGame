import java.util.List;

public class Tank extends Hero{

    Tank(String name, int health, int damage){
        super(name, health, damage);
    }

    @Override
    public void useAbility(List<Hero> heroes, Boss boss) {

    }

    @Override
    public void attack(Character enemy) {
        if (this.isAlive()) {
            enemy.receiveDamage(this.damage);
            System.out.println(this.name + " attacked " + enemy.getName() + " for " + this.damage + " damage.");
        }
    }

    public int reflectDamage(int incomingDamage){
        int reflected = incomingDamage / 5;
        return reflected;
    }
}
