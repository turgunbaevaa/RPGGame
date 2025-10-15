import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

public class HealerHealAbility implements HeroAbility {
    private final int HEAL_AMOUNT = 50; // Could be a constant or passed in constructor

    @Override
    public void use(Hero self, List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        System.out.println(self.getName() + " использует 'Исцеление союзника'");

        Hero target = allHeroes.stream()
                .filter(h -> h.isAlive() && h.getHealth() < h.getMaxHealth())
                // **NEW: Filter by range first**
                .filter(h -> self.getPosition().distanceTo(h.getPosition()) <= self.getRange())
                .min(Comparator.comparingDouble(h -> (double)h.getHealth() / h.getMaxHealth()))
                .orElse(null);

        if (target == null) {
            // This covers both "No wounded allies" and "No reachable wounded allies."
            System.out.println("Нет раненых союзников в пределах досягаемости.");
        } else {
            target.increaseHealth(HEAL_AMOUNT);
            System.out.println("Исцелен " + target.getName() + " на позиции " + target.getPosition().toString() + ". Здоровье: " + target.getHealth() + "/" + target.getMaxHealth());
        }
    }
}