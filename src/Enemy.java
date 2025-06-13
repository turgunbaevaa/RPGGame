// Enemy.java (Balance changes remain)
import java.util.*;

public abstract class Enemy extends Unit {
    protected int goldValue;
    protected int baseHealth;
    protected int baseDamage;
    protected int baseSpeed;
    protected int baseRange;

    public Enemy(String name, int health, int damage, int range, int speed, Position position, int goldValue) {
        super(name, health, damage, range, speed, position, 1);
        this.goldValue = goldValue;
        this.baseHealth = health;
        this.baseDamage = damage;
        this.baseSpeed = speed;
        this.baseRange = range;
    }

    @Override
    public void move(Position targetPosition, Board board) {
        board.updatePosition(this, targetPosition);
    }

    @Override
    public void levelUp() {
        // Enemies don't level up traditionally
    }

    public void levelUpStats(int wave) {
        this.health = baseHealth + (wave - 1) * 10;
        this.damage = baseDamage + (wave - 1) * 2;
        this.speed = baseSpeed + (wave - 1) / 4;
        this.range = baseRange;
    }

    @Override
    public int getMaxHealth() {
        return baseHealth;
    }

    public int getGoldValue() {
        return goldValue;
    }

    public void useAbility(List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        // Base implementation: nothing. Overridden in subclasses.
    }
}