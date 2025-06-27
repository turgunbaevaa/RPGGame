import java.util.List;
import java.util.stream.Collectors;

public class ArcherMultiShotAbility implements HeroAbility {
    @Override
    public void use(Hero self, List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        System.out.println(self.getName() + " использует 'Множественный выстрел'");
        boolean attackedSomeone = false;
        List<Enemy> targets = allEnemies.stream()
                .filter(Enemy::isAlive)
                .filter(e -> self.getPosition().distanceTo(e.getPosition()) <= self.getRange()) // Use getter for range
                .collect(Collectors.toList());

        if (targets.isEmpty()) {
            System.out.println("Нет врагов в зоне досягаемости для 'Множественного выстрела'.");
            return;
        }

        int originalDamage = self.getDamage(); // Use getter
        self.setDamage(originalDamage / 2); // Temporarily set damage

        for (Enemy e : targets) {
            self.attack(e);
            attackedSomeone = true;
        }
        self.setDamage(originalDamage); // Reset damage

        if (!attackedSomeone) {
            System.out.println("Ошибка при использовании способности: нет врагов в зоне досягаемости.");
        }
    }
}