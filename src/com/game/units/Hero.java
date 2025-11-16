package com.game.units;
import com.game.board.Board;
import com.game.board.Position;

import java.util.List;

public abstract class Hero extends Unit {
    protected boolean isTaunting = false;
    protected int baseHealth;
    protected int baseDamage;
    protected int baseSpeed;
    protected int baseRange;

    private final HeroAbility ability;

    protected Hero(String name, int health, int damage, int range, int speed, Position position, int level, HeroAbility ability) {
        super(name, health, damage, range, speed, position, level);
        this.baseHealth = health;
        this.baseDamage = damage;
        this.baseSpeed = speed;
        this.baseRange = range;
        this.ability = ability;
    }

    @Override
    public void move(Position targetPosition, Board board) {
        board.updatePosition(this, targetPosition);
        System.out.println(this.getName() + " moved to " + targetPosition.toString());
    }

    @Override
    public void levelUp() {
        this.setLevel(this.getLevel() + 1);
        this.baseHealth += 30;
        this.baseDamage += 10;
        this.baseSpeed += 1;
        this.setHealth(this.getMaxHealth());
        System.out.println(this.getName() + " increased the level to " + getLevel() + "!");
    }

    @Override
    public int getMaxHealth() {
        return baseHealth;
    }

    public void useAbility(List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        if (this.ability != null) {
            this.ability.use(this, allHeroes, allEnemies, board);
        } else {
            System.out.println("Error: This character has no assigned ability.");
        }
    }

    public boolean isTaunting() {
        return isTaunting;
    }

    public void setTaunting(boolean taunting) {
        this.isTaunting = taunting;
        if (taunting) {
            System.out.println(this.getName() + " is now provoking!");
        } else {
            System.out.println(this.getName() + " no longer provokes.");
        }
    }

    public void upgradeHealthStat(int amount) {
        this.baseHealth += amount;
        this.setHealth(this.getMaxHealth());
    }

    public void upgradeDamageStat(int amount) {
        this.baseDamage += amount;
        this.setDamage(this.getDamage() + amount);
    }

    public void upgradeSpeedStat(int amount) {
        this.baseSpeed += amount;
        this.setSpeed(this.getSpeed() + amount);
    }

    public void upgradeRangeStat(int amount) {
        this.baseRange += amount;
        this.setRange(this.getRange() + amount);
    }

    @Override
    public abstract String getDisplaySymbol();
}