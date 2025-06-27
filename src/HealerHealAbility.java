import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

public class HealerHealAbility implements HeroAbility {
    private final int HEAL_AMOUNT = 50; // Could be a constant or passed in constructor

    @Override
    public void use(Hero self, List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        System.out.println(self.getName() + " использует 'Исцеление союзника'");
        // Find the most wounded hero (lowest percentage health)
        Hero target = allHeroes.stream()
                .filter(h -> h.isAlive() && h.getHealth() < h.getMaxHealth())
                .min(Comparator.comparingDouble(h -> (double)h.getHealth() / h.getMaxHealth())) // Target lowest percentage
                .orElse(null);

        if (target == null) {
            System.out.println("Нет раненых союзников поблизости.");
        } else if (self.getPosition().distanceTo(target.getPosition()) > self.getRange()) { // Use getter for range
            System.out.println("Цель для лечения вне досягаемости.");
        } else {
            target.increaseHealth(HEAL_AMOUNT);
            System.out.println("Исцелен " + target.getName() + " на позиции " + target.getPosition().toString() + ". Здоровье: " + target.getHealth() + "/" + target.getMaxHealth());
        }
    }
}