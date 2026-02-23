package com.game.units;
import com.game.board.Board;
import com.game.board.Position;

import java.util.List;

public abstract class Hero extends Unit {
    private int baseHealth;

    protected Hero(String name, int health, int damage, int range, int speed, Position position, int level) {
        super(name, health, damage, range, speed, position, level);
        this.baseHealth = health;
    }

    @Override
    public final void move(Position targetPosition, Board board) {
        board.updatePosition(this, targetPosition);
        System.out.println(this.getName() + " moved to " + targetPosition.toString());
    }

    @Override
    public final void levelUp() {
        this.setLevel(this.getLevel() + 1);
        this.baseHealth += 30;
        this.setHealth(this.getMaxHealth());
        System.out.println(this.getName() + " increased the level to " + getLevel() + "!");
    }

    @Override
    public final int getMaxHealth() {
        return baseHealth;
    }

    public final void upgradeHealthStat(int amount) {
        this.baseHealth += amount;
        this.setHealth(this.getMaxHealth());
    }

    public final void upgradeDamageStat(int amount) {
        this.setDamage(this.getDamage() + amount);
    }

    public final void upgradeSpeedStat(int amount) {
        this.setSpeed(this.getSpeed() + amount);
    }

    public final void upgradeRangeStat(int amount) {
        this.setRange(this.getRange() + amount);
    }

    @Override
    public abstract String getDisplaySymbol();
}