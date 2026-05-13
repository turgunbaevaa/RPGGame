package com.game.abilities;

import com.game.board.Board;
import com.game.units.Enemy;
import com.game.units.Hero;
import com.game.units.HeroAbility;

import java.util.ArrayList;
import java.util.List;

public class ArcherMultiShotAbility implements HeroAbility {

    private static final int MAX_TARGETS = 3;

    @Override
    public void use(Hero self, List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        System.out.println(self.getName() + " uses 'Multiple Shot' on up to " + MAX_TARGETS + " targets.");

        List<Enemy> inRange = new ArrayList<>();
        for (Enemy e : allEnemies) {
            if (e.isAlive() && self.getPosition().distanceTo(e.getPosition()) <= self.getRange()) {
                inRange.add(e);
            }
        }

        sortEnemiesByDistanceFrom(self, inRange);

        if (inRange.isEmpty()) {
            System.out.println("No enemies within range for 'Multi Shot'.");
            return;
        }

        int multiShotDamage = self.getDamage() / 2;
        int count = Math.min(MAX_TARGETS, inRange.size());
        for (int i = 0; i < count; i++) {
            self.attack(inRange.get(i), multiShotDamage);
        }
        System.out.println("Multiple shots are completed.");
    }

    private static void sortEnemiesByDistanceFrom(Hero self, List<Enemy> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.size() - 1 - i; j++) {
                int dj = self.getPosition().distanceTo(list.get(j).getPosition());
                int dj1 = self.getPosition().distanceTo(list.get(j + 1).getPosition());
                if (dj > dj1) {
                    Enemy tmp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, tmp);
                }
            }
        }
    }
}
