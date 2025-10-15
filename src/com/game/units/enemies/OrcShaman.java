package com.game.units.enemies;
import com.game.board.Board;
import com.game.board.Position;
import com.game.units.Enemy;
import com.game.units.Hero;

import java.util.List;
import java.util.Comparator;
import java.util.Optional;

public class OrcShaman extends Enemy {
    private int healAmount = 25;
    private final int healRange = 2;

    public OrcShaman(Position position) {
        super("Орк-Шаман", 100, 5, 1, 2, position, 12);
    }

    @Override
    public void levelUpStats(int wave) {
        this.baseHealth = 100;
        this.baseDamage = 5;
        this.baseSpeed = 2;
        this.baseRange = 1;

        this.health = baseHealth + (wave - 1) * 15;
        this.damage = baseDamage + (wave - 1);
        this.speed = baseSpeed;
        this.range = baseRange;
        this.healAmount = 15 + (wave - 1) * 3;
    }

    @Override
    public void useAbility(List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        // Find the most wounded, alive, non-self ally within range.
        Optional<Enemy> targetToHeal = allEnemies.stream()
                .filter(e -> e != this && e.isAlive() && e.getHealth() < e.getMaxHealth())
                // Optimally filter by range first
                .filter(e -> this.position.distanceTo(e.getPosition()) <= this.healRange)
                // Target the unit with the largest health deficit
                .min(Comparator.comparingInt(e -> (e.getMaxHealth() - e.getHealth())));

        if (targetToHeal.isPresent()) {
            targetToHeal.get().increaseHealth(this.healAmount);
            System.out.printf("%s (%s) на позиции %s исцеляет %s на %d здоровья. Здоровье цели: %d/%d.%n",
                    this.getName(), this.getClass().getSimpleName(), this.getPosition().toString(),
                    targetToHeal.get().getName(), this.healAmount, targetToHeal.get().getHealth(), targetToHeal.get().getMaxHealth());
        } else {
            System.out.printf("%s (%s) ищет цель для исцеления, но не находит раненого союзника в пределах %d клеток.%n",
                    this.getName(), this.getClass().getSimpleName(), this.healRange);
        }
    }

    @Override
    public String getDisplaySymbol() {
        return "O"; // Orc Shaman symbol
    }
}