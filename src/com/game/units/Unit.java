package com.game.units;
import com.game.board.Board;
import com.game.board.Position;

import java.util.Objects;

public abstract class Unit {
    protected String name;
    protected int health;
    protected int damage;
    protected int range;
    protected int speed;
    protected int level;
    protected Position position;

    public Unit(String name, int health, int damage, int range, int speed, Position position, int level) {
        this.name = name;
        this.health = health;
        this.damage = damage;
        this.range = range;
        this.speed = speed;
        this.position = position;
        this.level = level;
    }

    public abstract void move(Position targetPosition, Board board);

    public abstract void levelUp();

    public abstract int getMaxHealth();

    public abstract String getDisplaySymbol();

    // This is the default attack method for normal attacks
    public void attack(Unit target) {
        attackWithDamage(target, this.damage);
    }

    public void attackWithDamage(Unit target, int damageAmount) {
        if (this.position.distanceTo(target.getPosition()) <= this.range) {
            int oldTargetHealth = target.getHealth();
            target.takeDamage(damageAmount);

            System.out.printf("%s (%s) at position %s attacks %s (%s) at position %s, dealing %d damage.%n",
                    this.getName(), this.getClass().getSimpleName(), this.getPosition().toString(),
                    target.getName(), target.getClass().getSimpleName(), target.getPosition().toString(),
                    damageAmount);
            System.out.printf("  Health %s: %d/%d (was %d)%n",
                    target.getName(), target.getHealth(), target.getMaxHealth(), oldTargetHealth);

            boolean targetDied = !target.isAlive();
            if (targetDied) {
                System.out.printf("   %s (%s) was defeated!%n", target.getName(), target.getClass().getSimpleName());
            }

        }
    }

    public void takeDamage(int amount) {
        this.health = Math.max(0, this.health - amount);
    }

    public boolean isAlive() {
        return this.health > 0;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position pos) {
        this.position = pos;
    }

    public int getHealth() {
        return health;
    }

    public String getName() { return name; }
    public int getDamage() { return damage; }
    public int getRange() { return range; }
    public int getSpeed() { return speed; }
    public int getLevel() { return level; }

    public void increaseHealth(int amount) {
        this.health = Math.min(this.health + amount, getMaxHealth());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unit unit = (Unit) o;
        return health == unit.health && damage == unit.damage && range == unit.range && speed == unit.speed && level == unit.level && Objects.equals(position, unit.position) && Objects.equals(name, unit.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, health, damage, range, speed, level, position);
    }
}