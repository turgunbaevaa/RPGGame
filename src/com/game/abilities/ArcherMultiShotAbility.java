package com.game.abilities;
import com.game.board.Board;
import com.game.units.Enemy;
import com.game.units.Hero;
import com.game.units.HeroAbility;

import java.util.Comparator;
import java.util.List;

public class ArcherMultiShotAbility implements HeroAbility {

    private static final int MAX_TARGETS = 3;

    @Override
    public void use(Hero self, List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        System.out.println(self.getName() + " uses 'Multiple Shot' on up to " + MAX_TARGETS + " targets.");

        List<Enemy> targets = allEnemies.stream()
                .filter(Enemy::isAlive)
                .filter(e -> self.getPosition().distanceTo(e.getPosition()) <= self.getRange())
                // **NEW: Order by distance and limit the count**
                .sorted(Comparator.comparingInt(e -> self.getPosition().distanceTo(e.getPosition())))
                .limit(MAX_TARGETS)
                .toList();
        if (targets.isEmpty()) {
            System.out.println("No enemies within range for ‘Multi Shot’.");
            return;
        }
        int multiShotDamage = self.getDamage() / 2; // Calculate the damage
        for (Enemy e : targets) {
            self.attackWithDamage(e, multiShotDamage); // Use the new method
        }
        System.out.println("Multiple shots are completed.");
    }
}