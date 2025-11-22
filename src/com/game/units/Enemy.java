package com.game.units;
import com.game.board.Board;
import com.game.board.Position;

import java.util.*;

public abstract class Enemy extends Unit {
    private int goldValue;
    private int baseHealth;
    private int baseDamage;
    private int baseSpeed;
    private int baseRange;
    private int currentWave;

    public Enemy(String name, int health, int damage, int range, int speed, Position position, int goldValue) {
        super(name, health, damage, range, speed, position, 1);
        this.goldValue = goldValue;
        this.baseHealth = health;
        this.baseDamage = damage;
        this.baseSpeed = speed;
        this.baseRange = range;
        this.currentWave = 1;
    }

    public int getBaseHealth(){ return baseHealth; }
    protected void setBaseHealth(int baseHealth){ this.baseHealth = baseHealth; }

    public int getBaseDamage(){ return baseDamage; }
    protected void setBaseDamage(int baseDamage){ this.baseDamage = baseDamage; }

    public int getBaseSpeed(){ return baseSpeed; }
    protected void setBaseSpeed(int baseSpeed){ this.baseSpeed = baseSpeed; }

    public int getBaseRange(){ return baseRange; }
    protected void setBaseRange(int baseRange){ this.baseRange = baseRange; }

    @Override
    public void move(Position targetPosition, Board board) {
        board.updatePosition(this, targetPosition);
    }

    @Override
    public void levelUp() {
        // Enemies don't level up traditionally
    }

    public void levelUpStats(int wave) {
        this.currentWave = wave;
        int newMaxHealth = this.baseHealth + (wave - 1) * 10;
        int newDamage = this.baseDamage + (wave - 1) * 2;
        int newSpeed = this.baseSpeed + (wave - 1) / 4;

        this.setHealth(newMaxHealth);
        this.setDamage(newDamage);
        this.setSpeed(newSpeed);
        this.setRange(this.baseRange);
    }

    @Override
    public int getMaxHealth() {
        return this.baseHealth + (this.currentWave - 1) * 10;
    }

    public int getGoldValue() {
        return goldValue;
    }

    public void useAbility(List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {

    }
}