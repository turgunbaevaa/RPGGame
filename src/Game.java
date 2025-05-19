import java.util.*;

public class Game {
    private List<Hero> heroes;
    private Boss boss;
    private Healer healer;

    private boolean isFirstAttack;
    private boolean isFinish;

    public Game() {
        // Initialize heroes
        heroes = new ArrayList<>();
        heroes.add(new Hero("Archer", 250, 40));
        heroes.add(new Tank(0.5, "Tank", 300, 50));

        // Initialize boss and healer
        boss = new Boss(0, "Boss", 1000, 60);
        healer = new Healer(20, "Healer", 1000, 0);

        isFirstAttack = true;
        isFinish = false;
    }

    public void bossHit() {
        boolean superHit = boss.attemptSuperAttack();

        for (Hero hero : heroes) {
            int damage = superHit ? boss.damage * 2 : boss.damage;

            if (hero instanceof Tank) {
                Tank tank = (Tank) hero;
                int reflected = tank.reflectDamage(damage);
                damage -= reflected;
                System.out.println(tank.name + " reflected " + reflected + " damage from the boss!");
            }

            hero.receiveDamage(damage);
        }

        int healerDamage = superHit ? boss.damage * 2 : boss.damage;
        healer.receiveDamage(healerDamage);

        System.out.println(superHit ? "Boss did critical damage!" : "Boss did normal damage.");
    }

    public void heroesHit() {
        for (Hero hero : heroes) {
            boss.receiveDamage(hero.damage);
        }
    }

    public void statistic() {
        System.out.println("Boss HP: " + boss.getHealth());
        for (Hero hero : heroes) {
            System.out.println("Hero " + hero.name + " HP: " + hero.getHealth());
        }
        System.out.println("Healer HP: " + healer.getHealth());
    }

    public void checkWinner() {
        boolean allHeroesDead = heroes.stream().allMatch(h -> h.getHealth() <= 0);

        if (boss.getHealth() <= 0 && allHeroesDead) {
            System.out.println("Draw!");
        } else if (boss.getHealth() <= 0) {
            System.out.println("Heroes win!");
        } else if (allHeroesDead) {
            System.out.println("Boss wins!");
        }
    }

    public void firstAttack() {
        int whoStarts = new Random().nextInt(2);
        if (whoStarts == 0) {
            bossHit();
            System.out.println("Boss started the battle!");
        } else {
            heroesHit();
            System.out.println("Heroes started the battle!");
        }
    }

    public void healHeroes() {
        healer.heal(heroes);
    }

    public void battle() {
        while (!isFinish) {
            if (isFirstAttack) {
                firstAttack();
                isFirstAttack = false;
            } else {
                System.out.println("____");
                bossHit();
                heroesHit();
                statistic();
                if (healer.getHealth() > 0) {
                    healer.heal(heroes);
                } else {
                    System.out.println("Healer " + healer.name + " is dead and can't heal!");
                }
            }

            boolean allHeroesDead = heroes.stream().allMatch(h -> h.getHealth() <= 0);
            if (boss.getHealth() <= 0 || allHeroesDead) {
                isFinish = true;
                checkWinner();
                System.out.println("Game Over");
            }
        }
    }
}
