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

    protected HeroAbility ability;

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
        System.out.println(this.name + " переместился на " + targetPosition.toString());
    }

    @Override
    public void levelUp() {
        this.level++;
        this.baseHealth += 30;
        this.baseDamage += 10;
        this.baseSpeed += 1;
        this.health = this.getMaxHealth();
        System.out.println(this.name + " повысил уровень до " + level + "!");
    }

    @Override
    public int getMaxHealth() {
        return baseHealth;
    }

    public void useAbility(List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        if (this.ability != null) {
            this.ability.use(this, allHeroes, allEnemies, board);
        } else {
            System.out.println("Ошибка: У этого героя нет назначенной способности.");
        }
    }

    public boolean isTaunting() {
        return isTaunting;
    }

    public void setTaunting(boolean taunting) {
        this.isTaunting = taunting;
        if (taunting) {
            System.out.println(this.name + " теперь провоцирует!");
        } else {
            System.out.println(this.name + " больше не провоцирует.");
        }
    }

    public void upgradeHealthStat(int amount) {
        this.baseHealth += amount;
        this.health = this.getMaxHealth();
    }

    public void upgradeDamageStat(int amount) {
        this.baseDamage += amount;
        this.damage += amount;
    }

    public void upgradeSpeedStat(int amount) {
        this.baseSpeed += amount;
        this.speed += amount;
    }

    public void upgradeRangeStat(int amount) {
        this.baseRange += amount;
        this.range += amount;
    }

    @Override
    public abstract String getDisplaySymbol();
}