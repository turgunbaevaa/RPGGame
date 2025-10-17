package com.game.abilities;
import com.game.board.Board;
import com.game.units.Enemy;
import com.game.units.Hero;
import com.game.units.HeroAbility;

import java.util.List;
import java.util.Comparator;

public class HealerHealAbility implements HeroAbility {
    private final int HEAL_AMOUNT = 50; // Could be a constant or passed in constructor

    @Override
    public void use(Hero self, List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        System.out.println(self.getName() + " uses 'Heal Ally'");

        Hero target = allHeroes.stream()
                .filter(h -> h.isAlive() && h.getHealth() < h.getMaxHealth())
                // **NEW: Filter by range first**
                .filter(h -> self.getPosition().distanceTo(h.getPosition()) <= self.getRange())
                .min(Comparator.comparingDouble(h -> (double)h.getHealth() / h.getMaxHealth()))
                .orElse(null);

        if (target == null) {
            // This covers both "No wounded allies" and "No reachable wounded allies."
            System.out.println("There are no wounded allies within range.");
        } else {
            target.increaseHealth(HEAL_AMOUNT);
            System.out.println("Healed " + target.getName() + " at position " + target.getPosition().toString() + ". Health: " + target.getHealth() + "/" + target.getMaxHealth());
        }
    }
}