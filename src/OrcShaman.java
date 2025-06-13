import java.util.List;
import java.util.Comparator;
import java.util.Optional;

public class OrcShaman extends Enemy {
    private int healAmount = 25;
    private int healRange = 2;

    public OrcShaman(Position position) {
        super("Орк-Шаман", 100, 5, 1, 2, position, 12);
    }

    @Override
    public void levelUpStats(int wave) {
        this.baseHealth = 100;
        this.baseDamage = 5;
        this.baseSpeed = 2;
        this.baseRange = 1;

        this.health = baseHealth + (wave - 1) * 15;
        this.damage = baseDamage + (wave - 1) * 1;
        this.speed = baseSpeed;
        this.range = baseRange;
        this.healAmount = 15 + (wave - 1) * 3;
    }

    @Override
    public void useAbility(List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        Optional<Enemy> targetToHeal = allEnemies.stream()
                .filter(e -> e != this && e.isAlive() && e.getHealth() < e.getMaxHealth())
                .min(Comparator.comparingInt(e -> (e.getMaxHealth() - e.getHealth())));

        if (targetToHeal.isPresent() && this.position.distanceTo(targetToHeal.get().getPosition()) <= this.healRange) {
            targetToHeal.get().increaseHealth(this.healAmount);
            System.out.printf("%s (%s) на позиции %s исцеляет %s на %d здоровья. Здоровье цели: %d/%d.%n",
                    this.getName(), this.getClass().getSimpleName(), this.getPosition().toString(),
                    targetToHeal.get().getName(), this.healAmount, targetToHeal.get().getHealth(), targetToHeal.get().getMaxHealth());
        } else {
            // No valid heal target or out of range.
        }
    }
}