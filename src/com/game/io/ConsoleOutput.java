package com.game.io;
import com.game.board.Board;
import com.game.units.Enemy;
import com.game.units.Hero;
import com.game.units.Unit;

import java.util.List;

public class ConsoleOutput implements GameOutput {

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void displayError(String message) {
        System.err.println("ERROR: " + message); // Use System.err for errors
    }

    @Override
    public void printBoard(Board board, List<Hero> heroes, List<Enemy> enemies) {
        board.printBoard();
    }

    @Override
    public void displayUnitStats(List<Hero> heroes, List<Enemy> enemies) {
        System.out.println("\n--- The State of Heroes ---");
        heroes.stream().filter(Unit::isAlive).forEach(hero -> {
            // Prepare the taunt status string with a leading space if it exists
            String tauntStatus = hero.isTaunting() ? " (Provocation)" : "";

            System.out.printf("  %s%s (Lvl.%d) Position: %s, HP: %d/%d, Damage: %d, Range: %d, Speed: %d%n",
                    hero.getName(),
                    tauntStatus,
                    hero.getLevel(),
                    hero.getPosition().toString(), hero.getHealth(), hero.getMaxHealth(),
                    hero.getDamage(), hero.getRange(), hero.getSpeed());
        });

        System.out.println("\n--- The State of Enemies ---");
        enemies.stream().filter(Unit::isAlive).forEach(enemy ->
                System.out.printf("%s (%s) (Lvl.%d) Position: %s, HP: %d/%d, Damage: %d, Range: %d, Speed: %d%n",
                        enemy.getName(), enemy.getClass().getSimpleName(), enemy.getLevel(), enemy.getPosition().toString(),
                        enemy.getHealth(), enemy.getMaxHealth(), enemy.getDamage(),
                        enemy.getRange(), enemy.getSpeed())
        );
        System.out.println("------------------------");
    }

    @Override
    public void displayShop(int gold, List<Hero> heroes) {
        System.out.println("\n===== SHOP =====");
        System.out.println("Your Gold: " + gold);
        System.out.println("Available upgrades (cost):");
        System.out.println("1. Increase HP of the hero by 20 (20 golds)");
        System.out.println("2. Increase Damage of the hero by 5 (15 golds)");
        System.out.println("3. Increase Speed of the hero by 1 (25 golds)");
        System.out.println("4. Increase Range of the hero by 1 (20 golds)");
        System.out.println("5. Exit the shop");

        System.out.println("Select a hero to upgrade:");
        List<Hero> aliveHeroes = heroes.stream().filter(Hero::isAlive).toList();
        if (aliveHeroes.isEmpty()) {
            System.out.println("No heroes available for upgrade.");
        } else {
            for (int i = 0; i < aliveHeroes.size(); i++) {
                Hero h = aliveHeroes.get(i);
                System.out.printf("%d: %s (Lvl.%d) HP: %d/%d, Damage: %d, Range: %d, Speed: %d%n",
                        i, h.getName(), h.getLevel(), h.getHealth(), h.getMaxHealth(), h.getDamage(), h.getRange(), h.getSpeed());
            }
        }
    }

    @Override
    public void displayAvailableTargets(List<? extends Unit> targets, Unit attacker) {
        System.out.println("Available targets for " + attacker.getName() + ":");
        if (targets.isEmpty()) {
            System.out.println("No targets within range.");
            return;
        }
        for (int i = 0; i < targets.size(); i++) {
            Unit u = targets.get(i);
            System.out.printf("%d: %s by %s (HP: %d/%d) Damage: %d%n",
                    i, u.getName(), u.getPosition().toString(), u.getHealth(), u.getMaxHealth(), u.getDamage());
        }
    }
}