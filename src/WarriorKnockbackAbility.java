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
            // Calculate the special damage for the ability
            int abilityDamage = (int)(self.getDamage() * 1.5);

            // Use the new attack method that accepts a custom damage value
            self.attackWithDamage(closest, abilityDamage);

            Position currentPos = closest.getPosition();
            Position heroPos = self.getPosition();

            int pushDistance = 2;

            // Calculate the vector from hero to enemy
            int deltaX = currentPos.getX() - heroPos.getX();
            int deltaY = currentPos.getY() - heroPos.getY();

            // Normalize the vector to get the direction
            int dirX = (deltaX > 0) ? 1 : (deltaX < 0) ? -1 : 0;
            int dirY = (deltaY > 0) ? 1 : (deltaY < 0) ? -1 : 0;

            Position finalPushPos = currentPos;

            // Find the furthest possible empty spot in the push direction
            for (int i = 0; i < pushDistance; i++) {
                Position nextPos = new Position(finalPushPos.getX() + dirX, finalPushPos.getY() + dirY);

                if (board.isValidPosition(nextPos) && board.isEmpty(nextPos)) {
                    finalPushPos = nextPos;
                } else {
                    // Stop if we hit an obstacle or the edge of the board
                    break;
                }
            }

            if (!finalPushPos.equals(currentPos)) {
                board.updatePosition(closest, finalPushPos);
                System.out.println(closest.getName() + " отброшен на " + finalPushPos.toString() + "!");
            } else {
                System.out.println(closest.getName() + " не удалось отбросить.");
            }
        } else {
            System.out.println("Нет цели для 'Удара с отбрасыванием' или цель вне досягаемости.");
        }
    }
}