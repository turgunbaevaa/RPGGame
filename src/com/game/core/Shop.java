package com.game.core;

import com.game.io.GameInput;
import com.game.io.GameOutput;
import com.game.units.Enemy;
import com.game.units.Hero;

import java.util.ArrayList;
import java.util.List;

/**
 * Between-wave upgrades; keeps shop flow out of {@link GameController}.
 */
public class Shop {
    private final GameInput input;
    private final GameOutput output;

    public Shop(GameInput input, GameOutput output) {
        this.input = input;
        this.output = output;
    }

    /**
     * @return updated gold balance after the player leaves the shop
     */
    public int run(int gold, List<Hero> heroes, List<Enemy> enemies) {
        int balance = gold;
        output.displayShop(balance, heroes);

        while (true) {
            int choice = input.getIntInput("Select upgrade or '5' to exit: ");

            if (choice == 5) {
                output.displayMessage("Exiting the store.");
                break;
            }

            List<Hero> aliveHeroes = collectAliveHeroes(heroes);
            if (aliveHeroes.isEmpty()) {
                output.displayMessage("No alive heroes to upgrade.");
                continue;
            }

            Hero targetHero = selectHeroForUpgrade(aliveHeroes);
            if (targetHero == null) {
                continue;
            }

            balance = handleUpgradePurchase(choice, targetHero, balance, heroes, enemies);
        }
        return balance;
    }

    private static List<Hero> collectAliveHeroes(List<Hero> heroes) {
        List<Hero> alive = new ArrayList<>();
        for (Hero h : heroes) {
            if (h.isAlive()) {
                alive.add(h);
            }
        }
        return alive;
    }

    private Hero selectHeroForUpgrade(List<Hero> aliveHeroes) {
        int heroIndex = input.getIntInput("Enter hero number: ");

        if (heroIndex >= 0 && heroIndex < aliveHeroes.size()) {
            return aliveHeroes.get(heroIndex);
        }
        output.displayError("Incorrect hero number.");
        return null;
    }

    private int handleUpgradePurchase(int choice, Hero targetHero, int gold, List<Hero> heroes, List<Enemy> enemies) {
        int cost;
        String upgradeType;

        switch (choice) {
            case 1:
                cost = 20;
                upgradeType = "HP";
                break;
            case 2:
                cost = 15;
                upgradeType = "Damage";
                break;
            case 3:
                cost = 25;
                upgradeType = "Speed";
                break;
            case 4:
                cost = 20;
                upgradeType = "Range";
                break;
            default:
                output.displayError("Incorrect upgrade selection.");
                return gold;
        }

        if (gold >= cost) {
            int balance = gold - cost;
            applyUpgrade(choice, targetHero);
            output.displayMessage(String.format("Successfully improved %s %s at %s. Gold: %d%n",
                    targetHero.getName(), upgradeType, targetHero.getPosition().toString(), balance));
            output.displayUnitStats(heroes, enemies);
            return balance;
        }
        output.displayMessage(String.format("Not enough gold for this upgrade! You need %d, you have %d.", cost, gold));
        return gold;
    }

    private static void applyUpgrade(int choice, Hero targetHero) {
        switch (choice) {
            case 1:
                targetHero.upgradeHealthStat(20);
                break;
            case 2:
                targetHero.upgradeDamageStat(5);
                break;
            case 3:
                targetHero.upgradeSpeedStat(1);
                break;
            case 4:
                targetHero.upgradeRangeStat(1);
                break;
            default:
                break;
        }
    }
}
