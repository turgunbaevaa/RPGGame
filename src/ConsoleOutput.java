import java.util.List;

public class ConsoleOutput implements GameOutput {

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void displayError(String message) {
        System.err.println("ОШИБКА: " + message); // Use System.err for errors
    }

    @Override
    public void printBoard(Board board, List<Hero> heroes, List<Enemy> enemies) {
        // We'll keep the actual board printing logic here, but move it out of Board if Board should only manage data.
        // For now, Board.printBoard is fine, but we'll adapt this method to call it.
        board.printBoard(); // Assuming Board still has printBoard() method
    }

    @Override
    public void displayUnitStats(List<Hero> heroes, List<Enemy> enemies) {
        System.out.println("\n--- Состояние Героев ---");
        heroes.stream().filter(Unit::isAlive).forEach(hero -> {
            // Prepare the taunt status string with a leading space if it exists
            String tauntStatus = hero.isTaunting() ? " (Провокация)" : "";

            System.out.printf("  %s%s (Ур.%d) Позиция: %s, HP: %d/%d, Урон: %d, Дальность: %d, Скорость: %d%n",
                    hero.getName(),
                    tauntStatus, // This now includes the leading space or is an empty string
                    hero.getLevel(),
                    hero.getPosition().toString(), hero.getHealth(), hero.getMaxHealth(),
                    hero.getDamage(), hero.getRange(), hero.getSpeed());
        });

        System.out.println("\n--- Состояние Врагов ---");
        enemies.stream().filter(Unit::isAlive).forEach(enemy ->
                System.out.printf("%s (%s) (Ур.%d) Позиция: %s, HP: %d/%d, Урон: %d, Дальность: %d, Скорость: %d%n",
                        enemy.getName(), enemy.getClass().getSimpleName(), enemy.getLevel(), enemy.getPosition().toString(),
                        enemy.getHealth(), enemy.getMaxHealth(), enemy.getDamage(),
                        enemy.getRange(), enemy.getSpeed())
        );
        System.out.println("------------------------");
    }

    @Override
    public void displayShop(int gold, List<Hero> heroes) {
        System.out.println("\n===== МАГАЗИН =====");
        System.out.println("Ваше золото: " + gold);
        System.out.println("Доступные апгрейды (стоимость):");
        System.out.println("1. Увеличить HP героя на 20 (20 золота)");
        System.out.println("2. Увеличить Урон героя на 5 (15 золота)");
        System.out.println("3. Увеличить Скорость героя на 1 (25 золота)");
        System.out.println("4. Увеличить Дальность героя на 1 (20 золота)");
        System.out.println("5. Выход из магазина");

        System.out.println("Выберите героя для апгрейда:");
        List<Hero> aliveHeroes = heroes.stream().filter(Hero::isAlive).collect(java.util.stream.Collectors.toList());
        if (aliveHeroes.isEmpty()) {
            System.out.println("Нет живых героев для апгрейда.");
        } else {
            for (int i = 0; i < aliveHeroes.size(); i++) {
                Hero h = aliveHeroes.get(i);
                System.out.printf("%d: %s (Ур.%d) HP: %d/%d, Урон: %d, Дальность: %d, Скорость: %d%n",
                        i, h.getName(), h.getLevel(), h.getHealth(), h.getMaxHealth(), h.getDamage(), h.getRange(), h.getSpeed());
            }
        }
    }

    @Override
    public void displayAvailableTargets(List<? extends Unit> targets, Unit attacker) {
        System.out.println("Доступные цели для " + attacker.getName() + ":");
        if (targets.isEmpty()) {
            System.out.println("Нет целей в зоне досягаемости.");
            return;
        }
        for (int i = 0; i < targets.size(); i++) {
            Unit u = targets.get(i);
            System.out.printf("%d: %s на %s (HP: %d/%d) Урон: %d%n",
                    i, u.getName(), u.getPosition().toString(), u.getHealth(), u.getMaxHealth(), u.getDamage());
        }
    }
}