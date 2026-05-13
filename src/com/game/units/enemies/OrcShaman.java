package com.game.units.enemies;

import com.game.board.Board;
import com.game.board.Position;
import com.game.units.Enemy;
import com.game.units.Hero;

import java.util.List;

public class OrcShaman extends Enemy {
    private static final int HEAL_RANGE = 2;

    private int healAmount;

    public OrcShaman(Position position) {
        super("Orc-Shaman", 100, 5, 1, 2, position, 12);
    }

    @Override
    public void levelUpStats(int wave) {
        super.levelUpStats(wave);
        this.setBaseHealth(100);
        this.setBaseDamage(5);
        this.setBaseSpeed(2);
        this.setBaseRange(1);

        this.setHealth(this.getBaseHealth() + (wave - 1) * 15);
        this.setDamage(this.getBaseDamage() + (wave - 1));
        this.setSpeed(this.getBaseSpeed());
        this.setRange(this.getBaseRange());
        this.healAmount = 15 + (wave - 1) * 3;
    }

    @Override
    public void useAbility(List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        Enemy targetToHeal = null;
        int bestDeficit = -1;
        for (Enemy e : allEnemies) {
            if (e == this || !e.isAlive() || e.getHealth() >= e.getMaxHealth()) {
                continue;
            }
            if (this.getPosition().distanceTo(e.getPosition()) > HEAL_RANGE) {
                continue;
            }
            int deficit = e.getMaxHealth() - e.getHealth();
            if (deficit > bestDeficit) {
                bestDeficit = deficit;
                targetToHeal = e;
            }
        }

        if (targetToHeal != null) {
            targetToHeal.increaseHealth(this.healAmount);
            System.out.printf("%s (%s) at position %s heals %s by %d health. Health of the target: %d/%d.%n",
                    this.getName(), this.getClass().getSimpleName(), this.getPosition().toString(),
                    targetToHeal.getName(), this.healAmount, targetToHeal.getHealth(), targetToHeal.getMaxHealth());
        } else {
            System.out.printf("%s (%s) searches for a target to heal, but finds no wounded ally within %d cells.%n",
                    this.getName(), this.getClass().getSimpleName(), HEAL_RANGE);
        }
    }

    @Override
    public String getDisplaySymbol() {
        return "O";
    }
}
