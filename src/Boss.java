import java.util.*;

public class Boss extends Character {
    public Boss(String name, int health, int damage) {
        super(name, health, damage);
    }

    public void attack(List<Hero> heroes) {
        Hero target = null;

        for (Hero h : heroes) {
            if (h instanceof Tank tank && tank.isAlive() && tank.isTaunting()) {
                target = tank;
                break;
            }
        }

        if (target == null) {
            List<Hero> aliveHeroes = heroes.stream().filter(Hero::isAlive).toList();
            if (!aliveHeroes.isEmpty()) {
                target = aliveHeroes.get(new Random().nextInt(aliveHeroes.size()));
            }
        }

        if (target != null) {
            target.receiveDamage(damage);
            System.out.println("Boss attacked " + target.getName() + " for " + damage + " damage!");
        }
    }
}