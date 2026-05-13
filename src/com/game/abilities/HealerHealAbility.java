package com.game.abilities;

import com.game.board.Board;
import com.game.units.Enemy;
import com.game.units.Hero;
import com.game.units.HeroAbility;

import java.util.List;

public class HealerHealAbility implements HeroAbility {
    private static final int HEAL_AMOUNT = 50;

    @Override
    public void use(Hero self, List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        System.out.println(self.getName() + " uses 'Heal Ally'");

        Hero target = null;
        double bestRatio = 2.0;
        for (Hero h : allHeroes) {
            if (!h.isAlive() || h.getHealth() >= h.getMaxHealth()) {
                continue;
            }
            if (self.getPosition().distanceTo(h.getPosition()) > self.getRange()) {
                continue;
            }
            if (h.getMaxHealth() <= 0) {
                continue;
            }
            double ratio = (double) h.getHealth() / h.getMaxHealth();
            if (target == null || ratio < bestRatio) {
                bestRatio = ratio;
                target = h;
            }
        }

        if (target == null) {
            System.out.println("There are no wounded allies within range.");
        } else {
            target.increaseHealth(HEAL_AMOUNT);
            System.out.println("Healed " + target.getName() + " at position " + target.getPosition().toString()
                    + ". Health: " + target.getHealth() + "/" + target.getMaxHealth());
        }
    }
}
