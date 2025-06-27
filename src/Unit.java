// Unit.java
import java.util.Objects;

public abstract class Unit {
    protected String name;
    protected int health;
    protected int damage; // Make sure this is protected so Hero can set it temporarily
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

    public boolean attack(Unit target) {
        if (this.position.distanceTo(target.getPosition()) <= this.range) {
            int oldTargetHealth = target.health;
            target.health = Math.max(0, target.health - this.damage);

            System.out.printf("%s (%s) на позиции %s атакует %s (%s) на позиции %s, нанося %d урона.%n",
                    this.getName(), this.getClass().getSimpleName(), this.getPosition().toString(),
                    target.getName(), target.getClass().getSimpleName(), target.getPosition().toString(),
                    this.damage);
            System.out.printf("   Здоровье %s: %d/%d (было %d)%n",
                    target.getName(), target.getHealth(), target.getMaxHealth(), oldTargetHealth);

            boolean targetDied = !target.isAlive();
            if (targetDied) {
                System.out.printf("   %s (%s) был побежден!%n", target.getName(), target.getClass().getSimpleName());
            }
            return targetDied;
        }
        return false;
    }

    public abstract void levelUp();

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

    public abstract int getMaxHealth();

    public String getName() { return name; }
    public int getDamage() { return damage; }
    public int getRange() { return range; }
    public int getSpeed() { return speed; }
    public int getLevel() { return level; }

    public void setDamage(int damage) { this.damage = damage; } // NEW METHOD

    public void increaseHealth(int amount) {
        this.health = Math.min(this.health + amount, getMaxHealth());
    }

    public void increaseMaxHealth(int amount) {
        // This method is problematic as noted before.
        // For upgrades, Hero/Enemy classes should modify their baseHealth directly.
        // Keeping it for now but note that it's probably not used as intended.
    }

    public void increaseDamage(int amount) { this.damage += amount; }
    public void increaseSpeed(int amount) { this.speed += amount; }
    public void increaseRange(int amount) { this.range += amount; }


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

    // Add this abstract method for the next refactoring step
    public abstract String getDisplaySymbol();
}