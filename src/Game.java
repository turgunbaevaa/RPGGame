import java.util.*;

public class Game {
    private List<Hero> heroes;
    private Boss boss;
    private BattleManager battleManager;

    public Game() {
        heroes = new ArrayList<>();
        heroes.add(new Tank(0.5, "Tank", 300, 50));
        heroes.add(new Healer(20, "Healer", 1000, 20)); // Add healer to heroes list
        heroes.add(new Archer("Archer", 1000, 10));      // Add archer to heroes list

        boss = new Boss(0, "Boss", 1000, 60);

        battleManager = new BattleManager(heroes, boss);
    }

    public void start() {
        battleManager.battle();
    }
}