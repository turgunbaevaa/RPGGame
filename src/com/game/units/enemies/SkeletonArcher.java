package com.game.units.enemies;
import com.game.board.Position;
import com.game.units.Enemy;

public class SkeletonArcher extends Enemy {
    public SkeletonArcher(Position position) {
        super("Скелет-Лучник", 50, 10, 4, 1, position, 8);
    }

    @Override
    public void levelUpStats(int wave) {
        this.baseHealth = 50;
        this.baseDamage = 10;
        this.baseSpeed = 1;
        this.baseRange = 4;

        this.health = baseHealth + (wave - 1) * 8;
        this.damage = baseDamage + (wave - 1) * 3;
        this.speed = baseSpeed + (wave - 1) / 5;
        this.range = baseRange;
    }

    @Override
    public String getDisplaySymbol() {
        return "S"; // Skeleton com.game.units.heroes.Archer symbol
    }
}