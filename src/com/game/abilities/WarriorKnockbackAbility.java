package com.game.abilities;

import com.game.board.Board;
import com.game.board.Position;
import com.game.units.Enemy;
import com.game.units.Hero;
import com.game.units.HeroAbility;

import java.util.List;

public class WarriorKnockbackAbility implements HeroAbility {

    @Override
    public void use(Hero self, List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        System.out.println(self.getName() + " uses 'Knockdown blow'");

        Enemy closest = null;
        int bestDistance = Integer.MAX_VALUE;
        for (Enemy e : allEnemies) {
            if (!e.isAlive()) {
                continue;
            }
            int d = e.getPosition().distanceTo(self.getPosition());
            if (d < bestDistance) {
                bestDistance = d;
                closest = e;
            }
        }

        if (closest != null && self.isInRange(closest)) {
            int abilityDamage = (int) (self.getDamage() * 1.5);

            self.attack(closest, abilityDamage);

            Position currentPos = closest.getPosition();
            Position heroPos = self.getPosition();

            int pushDistance = 2;

            int deltaX = currentPos.getX() - heroPos.getX();
            int deltaY = currentPos.getY() - heroPos.getY();

            int dirX = Integer.compare(deltaX, 0);
            int dirY = Integer.compare(deltaY, 0);

            Position finalPushPos = currentPos;

            for (int i = 0; i < pushDistance; i++) {
                Position nextPos = new Position(finalPushPos.getX() + dirX, finalPushPos.getY() + dirY);

                if (board.isValidPosition(nextPos) && board.isEmpty(nextPos)) {
                    finalPushPos = nextPos;
                } else {
                    break;
                }
            }

            if (!finalPushPos.equals(currentPos)) {
                board.updatePosition(closest, finalPushPos);
                System.out.println(closest.getName() + " discarded on " + finalPushPos + "!");
            } else {
                System.out.println(closest.getName() + " couldn't discard.");
            }
        } else {
            System.out.println("There is no target for 'Knockback Strike' or the target is out of range.");
        }
    }
}
