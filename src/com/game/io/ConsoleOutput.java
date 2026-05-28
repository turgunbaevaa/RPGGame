package com.game.io;

import com.game.board.Board;
import com.game.units.Enemy;
import com.game.units.Hero;
import com.game.units.Tauntable;
import com.game.units.Unit;

import java.util.ArrayList;
import java.util.List;

public class ConsoleOutput implements GameOutput {
    private static final int BOARD_SIZE = 10;

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void displayError(String message) {
        System.err.println("ERROR: " + message);
    }

    @Override
    public void printBoard(Board board, List<Hero> heroes, List<Enemy> enemies) {
        String[][] cells = new String[BOARD_SIZE][BOARD_SIZE];

        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                cells[y][x] = ".";
            }
        }

        for (Hero hero : heroes) {
            if (hero.isAlive()) {
                cells[hero.getPosition().getY()][hero.getPosition().getX()] = hero.getDisplaySymbol();
            }
        }
        for (Enemy enemy : enemies) {
            if (enemy.isAlive()) {
                cells[enemy.getPosition().getY()][enemy.getPosition().getX()] = enemy.getDisplaySymbol();
            }
        }

        System.out.println("   0 1 2 3 4 5 6 7 8 9");
        System.out.println("  ---------------------");
        for (int y = 0; y < BOARD_SIZE; y++) {
            System.out.print(y + " |");
            for (int x = 0; x < BOARD_SIZE; x++) {
                System.out.print(cells[y][x] + " ");
            }
            System.out.println("|");
        }
        System.out.println("  ---------------------");
    }

    @Override
    public void displayUnitStats(List<Hero> heroes, List<Enemy> enemies) {
        System.out.println("\n--- The State of Heroes ---");
        printAliveUnits(heroes, this::printHeroStats);

        System.out.println("\n--- The State of Enemies ---");
        printAliveUnits(enemies, this::printEnemyStats);
        System.out.println("------------------------");
    }

    @Override
    public void displayShop(int gold, List<Hero> heroes) {
        System.out.println("\n===== SHOP =====");
        System.out.println("Your gold: " + gold);
        System.out.println("Available upgrades (cost):");
        System.out.println("1. Increase HP of the hero by 20 (20 gold)");
        System.out.println("2. Increase Damage of the hero by 5 (15 gold)");
        System.out.println("3. Increase Speed of the hero by 1 (25 gold)");
        System.out.println("4. Increase Range of the hero by 1 (20 gold)");
        System.out.println("5. Exit the shop");

        System.out.println("Select a hero to upgrade:");
        List<Hero> aliveHeroes = collectAliveHeroes(heroes);
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
                    i, u.getName(), u.getPosition(), u.getHealth(), u.getMaxHealth(), u.getDamage());
        }
    }

    private static List<Hero> collectAliveHeroes(List<Hero> heroes) {
        List<Hero> aliveHeroes = new ArrayList<>();
        for (Hero hero : heroes) {
            if (hero.isAlive()) {
                aliveHeroes.add(hero);
            }
        }
        return aliveHeroes;
    }

    private static <T extends Unit> void printAliveUnits(List<T> units, java.util.function.Consumer<T> printer) {
        for (T unit : units) {
            if (unit.isAlive()) {
                printer.accept(unit);
            }
        }
    }

    private void printHeroStats(Hero hero) {
        String tauntStatus = (hero instanceof Tauntable tauntable && tauntable.isTaunting()) ? " (Provocation)" : "";
        System.out.printf("  %s%s (Lvl.%d) Position: %s, HP: %d/%d, Damage: %d, Range: %d, Speed: %d%n",
                hero.getName(), tauntStatus, hero.getLevel(), hero.getPosition(), hero.getHealth(),
                hero.getMaxHealth(), hero.getDamage(), hero.getRange(), hero.getSpeed());
    }

    private void printEnemyStats(Enemy enemy) {
        System.out.printf("%s (%s) (Lvl.%d) Position: %s, HP: %d/%d, Damage: %d, Range: %d, Speed: %d%n",
                enemy.getName(), enemy.getClass().getSimpleName(), enemy.getLevel(), enemy.getPosition(),
                enemy.getHealth(), enemy.getMaxHealth(), enemy.getDamage(), enemy.getRange(), enemy.getSpeed());
    }
}
