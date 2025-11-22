package com.game.units.enemies;
import com.game.board.Position;
import com.game.units.Enemy;

public class GoblinGrunt extends Enemy {
    public GoblinGrunt(Position position) {
        super("Goblin", 70, 15, 1, 2, position, 5);
    }

    @Override
    public void levelUpStats(int wave) {
        this.setBaseHealth(70);
        this.setBaseDamage(15);
        this.setBaseSpeed(2);
        this.setBaseRange(1);

        this.setHealth(this.getBaseHealth() + (wave - 1) * 10);
        this.setDamage(this.getBaseDamage() + (wave - 1) * 2);
        this.setSpeed(this.getBaseSpeed() + (wave - 1) / 4);
        this.setRange(this.getBaseRange());
    }

    @Override
    public String getDisplaySymbol() {
        return "G"; // Goblin Grunt symbol
    }
}