package com.game.units.enemies;
import com.game.board.Position;
import com.game.units.Enemy;

public class SkeletonArcher extends Enemy {
    public SkeletonArcher(Position position) {
        super("Skeleton-Archer", 50, 10, 4, 1, position, 8);
    }

    @Override
    public void levelUpStats(int wave) {
        this.baseHealth = 50;
        this.baseDamage = 10;
        this.baseSpeed = 1;
        this.baseRange = 4;

        this.setHealth(this.baseHealth + (wave - 1) * 8);
        this.setDamage(this.baseDamage + (wave - 1) * 3);
        this.setSpeed(this.baseSpeed + (wave - 1) / 5);
        this.setRange(this.baseRange);
    }

    @Override
    public String getDisplaySymbol() {
        return "S";
    }
}