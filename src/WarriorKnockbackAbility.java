import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

public class WarriorKnockbackAbility implements HeroAbility {
    @Override
    public void use(Hero self, List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        System.out.println(self.getName() + " использует 'Удар с отбрасыванием'");
        Enemy closest = allEnemies.stream()
                .filter(Enemy::isAlive)
                .min(Comparator.comparingInt(e -> e.getPosition().distanceTo(self.getPosition())))
                .orElse(null);

        if (closest != null && board.isInRange(self, closest)) {
            int originalDamage = self.getDamage(); // Use getter to get current damage
            self.setDamage((int)(originalDamage * 1.5)); // Temporarily set damage
            self.attack(closest); // Attack method now takes a Unit
            self.setDamage(originalDamage); // Reset damage

            Position oldPos = closest.getPosition();
            int pushX = oldPos.getX();
            int pushY = oldPos.getY();

            int deltaX = oldPos.getX() - self.getPosition().getX();
            int deltaY = oldPos.getY() - self.getPosition().getY();

            int pushDistance = 2;

            for (int i = 0; i < pushDistance; i++) {
                int nextPushX = pushX + (deltaX != 0 ? (deltaX > 0 ? 1 : -1) : 0);
                int nextPushY = pushY + (deltaY != 0 ? (deltaY > 0 ? 1 : -1) : 0);

                Position potentialPushBack = new Position(nextPushX, nextPushY);
                if (board.isValidPosition(potentialPushBack) && board.isEmpty(potentialPushBack)) {
                    pushX = nextPushX;
                    pushY = nextPushY;
                } else {
                    break;
                }
            }

            Position finalPushBack = new Position(pushX, pushY);

            if (!finalPushBack.equals(oldPos)) {
                board.updatePosition(closest, finalPushBack);
                System.out.println(closest.getName() + " отброшен на " + finalPushBack.toString() + "!");
            } else {
                System.out.println(closest.getName() + " не удалось отбросить.");
            }
        } else {
            System.out.println("Нет цели для 'Удара с отбрасыванием' или цель вне досягаемости.");
        }
    }
}