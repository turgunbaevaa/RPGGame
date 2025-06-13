import java.util.*;
import java.util.stream.Collectors;

public class GameController {
    private Board board;
    private List<Hero> heroes;
    private List<Enemy> enemies;
    private int turnCount = 0;
    private final int MAX_TURNS = 50;
    private final int MAX_WAVES = 5;
    private int wave = 1;
    private Scanner scanner;
    private int gold = 0;
    private Set<Enemy> enemiesActedThisRound; // To track which enemies have acted in the interleaved turns

    public GameController() {
        board = new Board();
        heroes = new ArrayList<>();
        enemies = new ArrayList<>();
        scanner = new Scanner(System.in);
        enemiesActedThisRound = new HashSet<>(); // Initialize the set
        setupHeroes();
        spawnEnemiesWave();
    }

    private void setupHeroes() {
        heroes.add(new Hero(Hero.HeroType.TANK, new Position(0, 4)));
        heroes.add(new Hero(Hero.HeroType.WARRIOR, new Position(0, 5)));
        heroes.add(new Hero(Hero.HeroType.ARCHER, new Position(1, 4)));
        heroes.add(new Hero(Hero.HeroType.HEALER, new Position(1, 5)));

        for (Hero hero : heroes) {
            board.placeUnit(hero);
        }
    }

    private void spawnEnemiesWave() {
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (enemy.isAlive()) {
                board.removeUnit(enemy); // Remove living enemies from board for re-spawning
            }
        }
        enemies.clear(); // Clear the list

        int enemyCount = 3 + wave;

        System.out.println("\n--- СПАВН ВРАГОВ ---");
        for (int i = 0; i < enemyCount; i++) {
            Enemy enemy;
            Position spawnPos = new Position(9, i % 10);
            int attempts = 0;
            // Find an empty spot for the new enemy
            while (board.getUnitAt(spawnPos) != null && attempts < 100) {
                spawnPos = new Position(spawnPos.getX(), (spawnPos.getY() + 1) % 10);
                // If we wrap around the Y-axis, try shifting X
                if (spawnPos.getY() == 0 && attempts > 0) {
                    spawnPos = new Position(spawnPos.getX() - 1, spawnPos.getY());
                }
                attempts++;
            }
            if (attempts >= 100 || !board.isValidPosition(spawnPos)) {
                // Fallback: search for any empty spot if initial strategy fails
                boolean foundSpot = false;
                for (int x = 9; x >= 0; x--) {
                    for (int y = 0; y < 10; y++) {
                        Position tempPos = new Position(x,y);
                        if (board.isEmpty(tempPos)) {
                            spawnPos = tempPos;
                            foundSpot = true;
                            break;
                        }
                    }
                    if (foundSpot) break;
                }
                if (!foundSpot) {
                    System.out.println("ОШИБКА: Невозможно найти место для спавна всех врагов!");
                    break; // Exit loop if no spot can be found
                }
            }


            double rand = Math.random();
            if (rand < 0.4) {
                enemy = new GoblinGrunt(spawnPos);
            } else if (rand < 0.75) {
                enemy = new SkeletonArcher(spawnPos);
            } else {
                enemy = new OrcShaman(spawnPos);
            }

            enemy.levelUpStats(wave);
            enemies.add(enemy);
            board.placeUnit(enemy);
            System.out.printf("Враг %s (%s) создан на %s. HP: %d/%d, Урон: %d, Золото: %d%n",
                    enemy.getName(), enemy.getClass().getSimpleName(), enemy.getPosition().toString(),
                    enemy.getHealth(), enemy.getMaxHealth(), enemy.getDamage(), enemy.getGoldValue());
        }

        System.out.println("⚔️ Волна " + wave + " началась! Врагов: " + enemies.size());
    }

    public void startGame() {
        while (turnCount < MAX_TURNS && !isGameOver()) {
            System.out.println("\n===== ХОД " + (turnCount + 1) + " =====");
            board.printBoard();
            displayUnitStats();
            System.out.println("💰 Золото: " + gold);

            enemiesActedThisRound.clear(); // Reset which enemies have acted for this main turn

            // Interleaved Player and Enemy Turns
            for (Hero hero : heroes) {
                if (!hero.isAlive()) {
                    continue; // Skip dead heroes
                }
                playerTurnIndividual(hero); // This hero takes an action
                performOneEnemyAction();    // Then one enemy takes an action
                cleanupDeadUnits();         // Clean up dead units immediately
            }

            // After all heroes have acted, make any remaining enemies take their turn
            performRemainingEnemyActions();

            // Final cleanup after all actions for the turn
            cleanupDeadUnits();

            // Check if wave is cleared
            if (enemies.stream().noneMatch(Enemy::isAlive)) {
                System.out.println("\n🌟 Волна " + wave + " пройдена!");

                for (Hero hero : heroes) {
                    if (hero.isAlive()) {
                        hero.levelUp();
                    }
                }

                if (wave < MAX_WAVES) {
                    showShop();
                }

                wave++;
                if (wave <= MAX_WAVES) {
                    for (Hero hero : heroes) {
                        hero.setTaunting(false); // Reset taunt for next wave
                    }
                    spawnEnemiesWave();
                } else {
                    break; // All waves cleared, game ends
                }
            }
            turnCount++;
        }
        scanner.close();
        concludeGame();
    }

    private void displayUnitStats() {
        System.out.println("\n--- Состояние Героев ---");
        heroes.stream().filter(Unit::isAlive).forEach(hero ->
                System.out.printf("%s %s (Ур.%d) Позиция: %s, HP: %d/%d, Урон: %d, Дальность: %d, Скорость: %d%n",
                        hero.getName(), hero.isTaunting() ? "(Провокация)" : "", hero.getLevel(),
                        hero.getPosition().toString(), hero.getHealth(), hero.getMaxHealth(),
                        hero.getDamage(), hero.getRange(), hero.getSpeed())
        );

        System.out.println("\n--- Состояние Врагов ---");
        enemies.stream().filter(Unit::isAlive).forEach(enemy ->
                System.out.printf("%s (%s) (Ур.%d) Позиция: %s, HP: %d/%d, Урон: %d, Дальность: %d, Скорость: %d%n",
                        enemy.getName(), enemy.getClass().getSimpleName(), enemy.getLevel(), enemy.getPosition().toString(),
                        enemy.getHealth(), enemy.getMaxHealth(), enemy.getDamage(),
                        enemy.getRange(), enemy.getSpeed())
        );
        System.out.println("------------------------");
    }

    // Refactored playerTurn to take a single hero
    private void playerTurnIndividual(Hero hero) {
        System.out.println("\n--- Ход Героя: " + hero.getName() + " (HP: " + hero.getHealth() + "/" + hero.getMaxHealth() + ") ---");

        List<Enemy> nearbyEnemies = enemies.stream()
                .filter(Enemy::isAlive)
                .filter(e -> hero.getPosition().distanceTo(e.getPosition()) <= hero.getRange() + 2)
                .toList();
        if (!nearbyEnemies.isEmpty()) {
            System.out.println("⚠️ Рядом враги! (в пределах " + (hero.getRange() + 2) + " клеток):");
            for (Enemy e : nearbyEnemies) {
                System.out.println("  - " + e.getName() + " на " + e.getPosition().toString() + " HP: " + e.getHealth() + "/" + e.getMaxHealth());
            }
        }

        System.out.print("1. Двигаться (макс. " + hero.getSpeed() + " клеток)\n2. Атаковать\n3. Способность\nВаш выбор: ");
        int choice = 0;
        while (true) {
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (choice >= 1 && choice <= 3) break;
                else System.out.println("Неверный выбор. Введите 1, 2 или 3.");
            } else {
                System.out.println("Неверный ввод. Введите число.");
                scanner.next();
            }
        }
        scanner.nextLine();

        switch (choice) {
            case 1 -> {
                System.out.print("Введите координаты X Y для передвижения: ");
                int x = -1, y = -1;
                try {
                    x = scanner.nextInt();
                    y = scanner.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Неверный формат координат. Введите два числа.");
                    scanner.nextLine();
                    // Do not continue, let the loop handle next hero if player inputs invalid value.
                    // Or, for a single hero turn, you might want to re-prompt or force end turn.
                    // For now, it will skip this hero's action for this turn.
                    return;
                }
                scanner.nextLine();
                Position newPos = new Position(x, y);

                if (!board.isValidPosition(newPos)) {
                    System.out.println("Ошибка: Позиция вне границ карты.");
                } else if (board.getUnitAt(newPos) != null && board.getUnitAt(newPos) != hero) {
                    System.out.println("Ошибка: Позиция занята другим юнитом.");
                } else if (hero.getPosition().distanceTo(newPos) > hero.getSpeed()) {
                    System.out.println("Слишком далеко. Лимит передвижения: " + hero.getSpeed() + " клеток.");
                } else {
                    hero.move(newPos, board);
                    board.printBoard();
                }
            }
            case 2 -> {
                System.out.println(hero.getName() + " атакует врагов в пределах " + hero.getRange() + " клетки(ок).");
                Enemy target = chooseTarget(hero);
                if (target != null) {
                    if (board.isInRange(hero, target)) {
                        hero.attack(target);
                    } else {
                        System.out.println("Цель вне досягаемости.");
                    }
                } else {
                    System.out.println("Атака отменена или нет доступных целей.");
                }
            }
            case 3 -> {
                hero.useAbility(heroes, enemies, board);
            }
        }
    }

    private Enemy chooseTarget(Hero hero) {
        List<Enemy> inRangeEnemies = enemies.stream()
                .filter(Enemy::isAlive)
                .filter(e -> board.isInRange(hero, e))
                .collect(Collectors.toList());

        if (inRangeEnemies.isEmpty()) {
            System.out.println("Нет врагов в зоне досягаемости.");
            return null;
        }

        System.out.println("Доступные цели:");
        for (int i = 0; i < inRangeEnemies.size(); i++) {
            Enemy e = inRangeEnemies.get(i);
            Position pos = e.getPosition();
            System.out.printf("%d: %s на %s (HP: %d/%d) Урон: %d%n",
                    i, e.getName(), pos.toString(), e.getHealth(), e.getMaxHealth(), e.getDamage());
        }

        System.out.print("Выберите номер цели или введите 'q' для отмены: ");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("q")) {
            System.out.println("Выбор цели отменен.");
            return null;
        }

        try {
            int idx = Integer.parseInt(input);
            if (idx >= 0 && idx < inRangeEnemies.size()) {
                return inRangeEnemies.get(idx);
            } else {
                System.out.println("Неверный номер цели.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Неверный ввод. Введите номер.");
        }
        return null;
    }

    // New method for one enemy action after a hero's turn
    private void performOneEnemyAction() {
        Optional<Enemy> enemyToAct = enemies.stream()
                .filter(Enemy::isAlive)
                .filter(e -> !enemiesActedThisRound.contains(e))
                .findFirst();

        if (enemyToAct.isPresent()) {
            Enemy enemy = enemyToAct.get();
            System.out.println("\n--- Ход Врага: " + enemy.getName() + " (" + enemy.getClass().getSimpleName() + ") ---");

            // Orc Shaman attempts to use ability first
            if (enemy instanceof OrcShaman) {
                enemy.useAbility(heroes, enemies, board);
                if (enemy.getDamage() == 0) { // Pure support units finish their turn after ability
                    System.out.printf("%s (%s) использовал способность. Ход завершен.%n", enemy.getName(), enemy.getClass().getSimpleName());
                    enemiesActedThisRound.add(enemy); // Mark as acted
                    return;
                }
            }

            Hero target = findClosestHero(enemy);
            if (target == null) {
                System.out.println(enemy.getName() + " на " + enemy.getPosition().toString() + " не нашел цели.");
                enemiesActedThisRound.add(enemy); // Mark as acted even if no target
                return;
            }

            if (board.isInRange(enemy, target)) {
                System.out.printf("%s (%s) на позиции %s атакует %s (%s) на позиции %s.%n",
                        enemy.getName(), enemy.getClass().getSimpleName(), enemy.getPosition().toString(),
                        target.getName(), target.getClass().getSimpleName(), target.getPosition().toString());
                enemy.attack(target);
            } else {
                moveToward(enemy, target.getPosition());
                System.out.printf("%s (%s) движется к %s. Текущая позиция: %s%n",
                        enemy.getName(), enemy.getClass().getSimpleName(), target.getName(), enemy.getPosition().toString());
            }
            enemiesActedThisRound.add(enemy); // Mark as acted
        } else {
            System.out.println("\n--- Нет врагов для действия в этом шаге ---");
        }
    }

    // New method to handle enemies that haven't acted yet after all heroes
    private void performRemainingEnemyActions() {
        System.out.println("\n--- Ход оставшихся Врагов ---");
        List<Enemy> remainingEnemies = enemies.stream()
                .filter(Enemy::isAlive)
                .filter(e -> !enemiesActedThisRound.contains(e))
                .collect(Collectors.toList());

        if (remainingEnemies.isEmpty()) {
            System.out.println("Все враги уже действовали или мертвы.");
            return;
        }

        for (Enemy enemy : remainingEnemies) {
            if (!enemy.isAlive()) continue; // Double check in case it died from another enemy action

            System.out.println("\n--- Ход Врага: " + enemy.getName() + " (" + enemy.getClass().getSimpleName() + ") ---");

            // Orc Shaman attempts to use ability first
            if (enemy instanceof OrcShaman) {
                enemy.useAbility(heroes, enemies, board);
                if (enemy.getDamage() == 0) { // Pure support units finish their turn after ability
                    System.out.printf("%s (%s) использовал способность. Ход завершен.%n", enemy.getName(), enemy.getClass().getSimpleName());
                    continue;
                }
            }

            Hero target = findClosestHero(enemy);
            if (target == null) {
                System.out.println(enemy.getName() + " на " + enemy.getPosition().toString() + " не нашел цели.");
                continue;
            }

            if (board.isInRange(enemy, target)) {
                System.out.printf("%s (%s) на позиции %s атакует %s (%s) на позиции %s.%n",
                        enemy.getName(), enemy.getClass().getSimpleName(), enemy.getPosition().toString(),
                        target.getName(), target.getClass().getSimpleName(), target.getPosition().toString());
                enemy.attack(target);
            } else {
                moveToward(enemy, target.getPosition());
                System.out.printf("%s (%s) движется к %s. Текущая позиция: %s%n",
                        enemy.getName(), enemy.getClass().getSimpleName(), target.getName(), enemy.getPosition().toString());
            }
        }
    }


    private Hero findClosestHero(Enemy enemy) {
        Optional<Hero> tauntingTank = heroes.stream()
                .filter(h -> h.isAlive() && h.getType() == Hero.HeroType.TANK && h.isTaunting())
                .findFirst();

        return tauntingTank.orElse(
                heroes.stream()
                        .filter(Hero::isAlive)
                        .min(Comparator.comparingInt(h -> h.getPosition().distanceTo(enemy.getPosition())))
                        .orElse(null)
        );
    }

    private void moveToward(Enemy enemy, Position targetPos) {
        Position current = enemy.getPosition();
        Position bestNextPos = current;
        int minDistance = current.distanceTo(targetPos);
        boolean foundAttackPosition = false;

        List<Position> possibleMoves = new ArrayList<>();

        for (int dx = -enemy.getSpeed(); dx <= enemy.getSpeed(); dx++) {
            for (int dy = -enemy.getSpeed(); dy <= enemy.getSpeed(); dy++) {
                if (Math.abs(dx) + Math.abs(dy) <= enemy.getSpeed()) {
                    Position potentialNext = new Position(current.getX() + dx, current.getY() + dy);

                    if (board.isValidPosition(potentialNext)) {
                        Unit unitAtPotentialNext = board.getUnitAt(potentialNext);
                        if (unitAtPotentialNext == null || unitAtPotentialNext.equals(enemy) || unitAtPotentialNext instanceof Enemy) {
                            possibleMoves.add(potentialNext);
                        }
                    }
                }
            }
        }

        for (Position move : possibleMoves) {
            int distanceToTargetFromMove = move.distanceTo(targetPos);
            if (distanceToTargetFromMove <= enemy.getRange()) {
                if (!foundAttackPosition || distanceToTargetFromMove < minDistance) {
                    minDistance = distanceToTargetFromMove;
                    bestNextPos = move;
                    foundAttackPosition = true;
                }
            } else if (!foundAttackPosition) {
                if (distanceToTargetFromMove < minDistance) {
                    minDistance = distanceToTargetFromMove;
                    bestNextPos = move;
                }
            }
        }

        if (!bestNextPos.equals(current)) {
            Unit unitAtBestNextPos = board.getUnitAt(bestNextPos);
            if (unitAtBestNextPos != null && unitAtBestNextPos instanceof Enemy && !unitAtBestNextPos.equals(enemy)) {
                Position fallbackPos = current;
                int fallbackMinDistance = current.distanceTo(targetPos);
                for (Position move : possibleMoves) {
                    if (board.isEmpty(move)) {
                        int distance = move.distanceTo(targetPos);
                        if (distance < fallbackMinDistance) {
                            fallbackMinDistance = distance;
                            fallbackPos = move;
                        }
                    }
                }
                if (!fallbackPos.equals(current)) {
                    board.updatePosition(enemy, fallbackPos);
                } else {
                    // No valid empty spot found, enemy might be stuck.
                }

            } else {
                board.updatePosition(enemy, bestNextPos);
            }
        }
    }


    private void cleanupDeadUnits() {
        // Clear dead enemies and award gold
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (!enemy.isAlive()) {
                board.removeUnit(enemy);
                gold += enemy.getGoldValue();
                System.out.printf("   %s (%s) был устранен. Получено %d золота. Всего золота: %d%n",
                        enemy.getName(), enemy.getClass().getSimpleName(), enemy.getGoldValue(), gold);
                enemyIterator.remove();
                // Remove from enemiesActedThisRound if it was there and now dead
                enemiesActedThisRound.remove(enemy);
            }
        }

        // Clear dead heroes from board
        List<Hero> deadHeroes = heroes.stream().filter(h -> !h.isAlive()).toList();
        for (Hero hero : deadHeroes) {
            board.removeUnit(hero);
            System.out.println("Герой " + hero.getName() + " на " + hero.getPosition().toString() + " пал.");
        }
    }

    private void showShop() {
        System.out.println("\n===== МАГАЗИН =====");
        System.out.println("Ваше золото: " + gold);
        System.out.println("Доступные апгрейды (стоимость):");
        System.out.println("1. Увеличить HP героя на 20 (20 золота)");
        System.out.println("2. Увеличить Урон героя на 5 (15 золота)");
        System.out.println("3. Увеличить Скорость героя на 1 (25 золота)");
        System.out.println("4. Увеличить Дальность героя на 1 (20 золота)");
        System.out.println("5. Выход из магазина");

        while (true) {
            System.out.print("Выберите апгрейд или '5' для выхода: ");
            int choice = -1;
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Неверный ввод. Введите число.");
                scanner.nextLine();
                continue;
            }
            scanner.nextLine();

            if (choice == 5) {
                System.out.println("Выход из магазина.");
                break;
            }

            Hero targetHero = null;
            System.out.println("Выберите героя для апгрейда:");
            List<Hero> aliveHeroes = heroes.stream().filter(Hero::isAlive).collect(Collectors.toList());
            if (aliveHeroes.isEmpty()) {
                System.out.println("Нет живых героев для апгрейда.");
                continue;
            }

            for (int i = 0; i < aliveHeroes.size(); i++) {
                Hero h = aliveHeroes.get(i);
                System.out.printf("%d: %s (Ур.%d) HP: %d/%d, Урон: %d, Дальность: %d, Скорость: %d%n",
                        i, h.getName(), h.getLevel(), h.getHealth(), h.getMaxHealth(), h.getDamage(), h.getRange(), h.getSpeed());
            }
            System.out.print("Введите номер героя: ");
            int heroIndex = -1;
            try {
                heroIndex = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Неверный ввод. Введите число.");
                scanner.nextLine();
                continue;
            }
            scanner.nextLine();

            if (heroIndex >= 0 && heroIndex < aliveHeroes.size()) {
                targetHero = aliveHeroes.get(heroIndex);
            } else {
                System.out.println("Неверный номер героя.");
                continue;
            }

            int cost = 0;
            String upgradeType = "";
            switch (choice) {
                case 1:
                    cost = 20;
                    upgradeType = "HP";
                    break;
                case 2:
                    cost = 15;
                    upgradeType = "Урон";
                    break;
                case 3:
                    cost = 25;
                    upgradeType = "Скорость";
                    break;
                case 4:
                    cost = 20;
                    upgradeType = "Дальность";
                    break;
                default:
                    System.out.println("Неверный выбор апгрейда.");
                    continue;
            }

            if (gold >= cost) {
                gold -= cost;
                switch (choice) {
                    case 1:
                        targetHero.upgradeHealthStat(20);
                        break;
                    case 2:
                        targetHero.upgradeDamageStat(5);
                        break;
                    case 3:
                        targetHero.upgradeSpeedStat(1);
                        break;
                    case 4:
                        targetHero.upgradeRangeStat(1);
                        break;
                }
                System.out.printf("Успешно улучшен %s %s на %s. Золото: %d%n",
                        targetHero.getName(), upgradeType, targetHero.getPosition().toString(), gold);
                displayUnitStats();
            } else {
                System.out.println("Недостаточно золота для этого апгрейда! Вам нужно " + cost + ", у вас " + gold + ".");
            }
        }
    }


    private boolean isGameOver() {
        boolean allHeroesDead = heroes.stream().noneMatch(Hero::isAlive);
        boolean allWavesCleared = wave > MAX_WAVES;
        return allHeroesDead || allWavesCleared;
    }

    private void concludeGame() {
        System.out.println("\n===== ИГРА ОКОНЧЕНА =====");
        if (heroes.stream().noneMatch(Hero::isAlive)) {
            System.out.println("Поражение. Все герои пали.");
        } else if (wave > MAX_WAVES) {
            System.out.println("🏆 Победа! Все " + MAX_WAVES + " волн отбиты!");
        } else {
            System.out.println("Игра завершена. Количество ходов: " + turnCount);
        }
        System.out.println("\n--- Итоговый счет ---");
        System.out.println("Волн пройдено: " + (wave - 1));
        System.out.println("Оставшееся золото: " + gold);
        System.out.println("Герои:");
        heroes.forEach(hero ->
                System.out.printf("  %s: %s (Ур.%d) HP: %d/%d, Урон: %d, Дальность: %d, Скорость: %d%n",
                        hero.getName(), hero.isAlive() ? "Жив" : "Пал",
                        hero.getLevel(), hero.getHealth(), hero.getMaxHealth(),
                        hero.getDamage(), hero.getRange(), hero.getSpeed())
        );
    }
}