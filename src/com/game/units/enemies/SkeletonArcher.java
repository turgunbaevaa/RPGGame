package com.game.units.enemies;
import com.game.board.Position;
import com.game.units.Enemy;

public class SkeletonArcher extends Enemy {
    public SkeletonArcher(Position position) {
        super("Skeleton-Archer", 50, 10, 4, 1, position, 8);
    }

    @Override
    public void levelUpStats(int wave) {
        this.setBaseHealth(50);
        this.setBaseDamage(10);
        this.setBaseSpeed(1);
        this.setBaseRange(4);

        this.setHealth(this.getBaseHealth() + (wave - 1) * 8);
        this.setDamage(this.getBaseDamage() + (wave - 1) * 3);
        this.setSpeed(this.getBaseSpeed() + (wave - 1) / 5);
        this.setRange(this.getBaseRange());
    }

    @Override
    public String getDisplaySymbol() {
        return "S";
    }
}