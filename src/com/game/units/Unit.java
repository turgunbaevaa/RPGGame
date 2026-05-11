package com.game.units;
import com.game.board.Board;
import com.game.board.Locatable;
import com.game.board.Position;

import java.util.Objects;

public abstract class Unit implements Locatable {
    private final String name;
    private int health;
    private int damage;
    private int range;
    private int speed;
    private int level;
    private Position position;

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
    public final String attack(Unit target) {
        return attack(target, this.damage);
    }

    public final String attack(Unit target, int damageAmount) {
        if (target == null) {
            return this.getName() + " cannot attack because the target is missing.";
        }
        if (this.position == null || target.getPosition() == null) {
            return this.getName() + " cannot attack because one of the units has no position.";
        }
        if (!isInRange(target)) {
            return this.getName() + " cannot attack because the target is out of range.";
        }

        int oldTargetHealth = target.getHealth();
        target.takeDamage(damageAmount);

        StringBuilder result = new StringBuilder();
        result.append(String.format("%s (%s) at position %s attacks %s (%s) at position %s, dealing %d damage.%n",
                this.getName(), this.getClass().getSimpleName(), this.getPosition().toString(),
                target.getName(), target.getClass().getSimpleName(), target.getPosition().toString(),
                damageAmount));
        result.append(String.format("  Health %s: %d/%d (was %d)",
                target.getName(), target.getHealth(), target.getMaxHealth(), oldTargetHealth));

        if (!target.isAlive()) {
            result.append(String.format("%n   %s (%s) was defeated!", target.getName(), target.getClass().getSimpleName()));
        }

        return result.toString();
    }

    public final void takeDamage(int amount) {
        this.health = Math.max(0, this.health - amount);
    }

    public final boolean isAlive() { return this.health > 0; }
    public final boolean isInRange(Unit target) {
        if (target == null || this.position == null || target.getPosition() == null) {
            return false;
        }
        return this.position.distanceTo(target.getPosition()) <= this.range;
    }

    public final Position getPosition() { return this.position;}
    public final int getHealth() { return health; }
    public final String getName() { return name; }
    public final int getDamage() { return damage; }
    public final int getRange() { return range; }
    public final int getSpeed() { return speed; }
    public final int getLevel() { return level; }

    public final void setPosition(Position pos) { this.position = pos; }
    protected final void setLevel(int level) { this.level = level; }
    protected final void setHealth(int health) { this.health = health; }
    protected final void setDamage(int damage) { this.damage = damage; }
    protected final void setSpeed(int speed) { this.speed = speed; }
    protected final void setRange(int range) { this.range = range; }

    public final void increaseHealth(int amount) {
        this.health = Math.min(this.health + amount, getMaxHealth());
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unit unit = (Unit) o;
        return health == unit.health && damage == unit.damage && range == unit.range && speed == unit.speed && level == unit.level && Objects.equals(position, unit.position) && Objects.equals(name, unit.name);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(name, health, damage, range, speed, level, position);
    }
}