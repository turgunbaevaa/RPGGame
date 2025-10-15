package com.game.units.enemies;
import com.game.board.Position;
import com.game.units.Enemy;

public class GoblinGrunt extends Enemy {
    public GoblinGrunt(Position position) {
        super("Гоблин", 70, 15, 1, 2, position, 5);
    }

    @Override
    public void levelUpStats(int wave) {
        this.baseHealth = 70;
        this.baseDamage = 15;
        this.baseSpeed = 2;
        this.baseRange = 1;

        this.health = baseHealth + (wave - 1) * 10;
        this.damage = baseDamage + (wave - 1) * 2;
        this.speed = baseSpeed + (wave - 1) / 4;
        this.range = baseRange;
    }

    @Override
    public String getDisplaySymbol() {
        return "G"; // Goblin Grunt symbol
    }
}