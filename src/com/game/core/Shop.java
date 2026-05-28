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
    private static final int EXIT_CHOICE = 5;
    private static final int UPGRADE_HP = 1;
    private static final int UPGRADE_DAMAGE = 2;
    private static final int UPGRADE_SPEED = 3;
    private static final int UPGRADE_RANGE = 4;

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

            if (choice == EXIT_CHOICE) {
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
        UpgradeOption option = UpgradeOption.fromChoice(choice);
        if (option == null) {
            output.displayError("Incorrect upgrade selection.");
            return gold;
        }

        if (gold >= option.cost) {
            int balance = gold - option.cost;
            applyUpgrade(option, targetHero);
            output.displayMessage(String.format("Successfully improved %s %s at %s. Gold: %d%n",
                    targetHero.getName(), option.label, targetHero.getPosition(), balance));
            output.displayUnitStats(heroes, enemies);
            return balance;
        }
        output.displayMessage(String.format("Not enough gold for this upgrade! You need %d, you have %d.", option.cost, gold));
        return gold;
    }

    private static void applyUpgrade(UpgradeOption option, Hero targetHero) {
        switch (option.choice) {
            case UPGRADE_HP:
                targetHero.upgradeHealthStat(20);
                break;
            case UPGRADE_DAMAGE:
                targetHero.upgradeDamageStat(5);
                break;
            case UPGRADE_SPEED:
                targetHero.upgradeSpeedStat(1);
                break;
            case UPGRADE_RANGE:
                targetHero.upgradeRangeStat(1);
                break;
            default:
                break;
        }
    }

    private static final class UpgradeOption {
        private final int choice;
        private final int cost;
        private final String label;

        private UpgradeOption(int choice, int cost, String label) {
            this.choice = choice;
            this.cost = cost;
            this.label = label;
        }

        private static UpgradeOption fromChoice(int choice) {
            switch (choice) {
                case UPGRADE_HP:
                    return new UpgradeOption(UPGRADE_HP, 20, "HP");
                case UPGRADE_DAMAGE:
                    return new UpgradeOption(UPGRADE_DAMAGE, 15, "Damage");
                case UPGRADE_SPEED:
                    return new UpgradeOption(UPGRADE_SPEED, 25, "Speed");
                case UPGRADE_RANGE:
                    return new UpgradeOption(UPGRADE_RANGE, 20, "Range");
                default:
                    return null;
            }
        }
    }
}
