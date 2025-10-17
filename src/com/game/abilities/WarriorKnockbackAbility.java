package com.game.abilities;
import com.game.board.Board;
import com.game.board.Position;
import com.game.units.Enemy;
import com.game.units.Hero;
import com.game.units.HeroAbility;

import java.util.List;
import java.util.Comparator;

public class WarriorKnockbackAbility implements HeroAbility {

    @Override
    public void use(Hero self, List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        System.out.println(self.getName() + " uses 'Knockdown blow'");
        Enemy closest = allEnemies.stream()
                .filter(Enemy::isAlive)
                .min(Comparator.comparingInt(e -> e.getPosition().distanceTo(self.getPosition())))
                .orElse(null);

        if (closest != null && board.isInRange(self, closest)) {
            // Calculate the special damage for the ability
            int abilityDamage = (int)(self.getDamage() * 1.5);

            // Use the new attack method that accepts a custom damage value
            self.attackWithDamage(closest, abilityDamage);

            Position currentPos = closest.getPosition();
            Position heroPos = self.getPosition();

            int pushDistance = 2;

            // Calculate the vector from hero to enemy
            int deltaX = currentPos.x() - heroPos.x();
            int deltaY = currentPos.y() - heroPos.y();

            // Normalize the vector to get the direction
            int dirX = Integer.compare(deltaX, 0);
            int dirY = Integer.compare(deltaY, 0);

            Position finalPushPos = currentPos;

            // Find the furthest possible empty spot in the push direction
            for (int i = 0; i < pushDistance; i++) {
                Position nextPos = new Position(finalPushPos.x() + dirX, finalPushPos.y() + dirY);

                if (board.isValidPosition(nextPos) && board.isEmpty(nextPos)) {
                    finalPushPos = nextPos;
                } else {
                    // Stop if we hit an obstacle or the edge of the board
                    break;
                }
            }

            if (!finalPushPos.equals(currentPos)) {
                board.updatePosition(closest, finalPushPos);
                // 🛠️ OPTIMIZATION: Removed redundant .toString() call
                System.out.println(closest.getName() + " discarded on " + finalPushPos + "!");
            } else {
                System.out.println(closest.getName() + " couldn't discard.");
            }
        } else {
            System.out.println("There is no target for 'Knockback Strike' or the target is out of range.");
        }
    }
}