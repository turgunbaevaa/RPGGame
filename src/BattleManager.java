import java.util.List;
import java.util.Random;

public class BattleManager {
    private List<Hero> heroes;
    private Boss boss;
    private boolean isFirstAttack;
    private boolean isFinish;

    public BattleManager(List<Hero> heroes, Boss boss) {
        this.heroes = heroes;
        this.boss = boss;
        this.isFirstAttack = true;
        this.isFinish = false;
    }

    public void bossHit() {
        boolean superHit = boss.attemptSuperAttack();

        for (Hero hero : heroes) {
            int damage = superHit ? boss.getDamage() * 2 : boss.getDamage();

            if (hero instanceof Tank) {
                Tank tank = (Tank) hero;
                int reflected = tank.reflectDamage(damage);
                damage -= reflected;
                System.out.println(tank.getName() + " reflected " + reflected + " damage from the boss!");
            }

            hero.receiveDamage(damage);
        }

        System.out.println(superHit ? "Boss did critical damage!" : "Boss did normal damage.");
    }

    public void heroesHit() {
        for (Hero hero : heroes) {
            hero.attack(boss);
            hero.useAbility(heroes, boss);
        }
    }

    public void statistic() {
        System.out.println("Boss HP: " + boss.getHealth());
        for (Hero hero : heroes) {
            System.out.println(hero.getName() + " HP: " + hero.getHealth());
        }
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