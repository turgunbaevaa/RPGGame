import java.util.*;

public class Game {
    private List<Hero> heroes;
    private Boss boss;
    private BattleManager battleManager;

    public Game() {
        heroes = new ArrayList<>();
        heroes.add(new Tank("Tank", 1000, 40));
        heroes.add(new Healer(20, "Healer", 1000, 20));
        heroes.add(new Warrior("Warrior", 1000, 20));
        heroes.add(new Archer("Archer", 1000, 20));

        boss = new Boss(0, "Boss", 1000, 20);

        battleManager = new BattleManager(heroes, boss);
    }

    public void start() {
        battleManager.battle();
    }
}