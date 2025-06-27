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
    private int gold = 0;
    private Set<Enemy> enemiesActedThisRound;
    private Random random;

    private GameInput input;
    private GameOutput output;
    private UnitFactory unitFactory;

    public GameController(Board board, Random random, GameInput input, GameOutput output, UnitFactory unitFactory) {
        this.board = board;
        this.random = random;
        this.input = input;
        this.output = output;
        this.unitFactory = unitFactory;

        heroes = new ArrayList<>();
        enemies = new ArrayList<>();
        enemiesActedThisRound = new HashSet<>();

        setupHeroes();
        spawnEnemiesWave();
    }

    private Position getRandomEmptyPosition(int minX, int maxX, int minY, int maxY) {
        Position pos;
        int attempts = 0;
        do {
            int x = random.nextInt(maxX - minX + 1) + minX;
            int y = random.nextInt(maxY - minY + 1) + minY;
            pos = new Position(x, y);
            attempts++;
            if (attempts > 1000) {
                output.displayError("Не удалось найти свободное место после множества попыток. Возможно, доска заполнена.");
                return null;
            }
        } while (!board.isEmpty(pos));
        return pos;
    }

    private void setupHeroes() {
        for (Hero hero : heroes) {
            if (hero.isAlive()) {
                board.removeUnit(hero);
            }
        }
        heroes.clear();

        Position tankPos = getRandomEmptyPosition(0, 4, 0, 4);
        Position warriorPos = getRandomEmptyPosition(0, 4, 0, 4); // Will find a different empty spot
        Position archerPos = getRandomEmptyPosition(0, 4, 0, 4);   // Will find a different empty spot
        Position healerPos = getRandomEmptyPosition(0, 4, 0, 4);   // Will find a different empty spot

        heroes.add(unitFactory.createHero(HeroType.TANK, tankPos));
        heroes.add(unitFactory.createHero(HeroType.WARRIOR, warriorPos));
        heroes.add(unitFactory.createHero(HeroType.ARCHER, archerPos));
        heroes.add(unitFactory.createHero(HeroType.HEALER, healerPos));

        for (Hero hero : heroes) {
            if (hero.getPosition() != null) {
                board.placeUnit(hero);
            } else {
                output.displayError("Не удалось разместить героя " + hero.getName() + " из-за отсутствия свободных мест.");
            }
        }
    }

    private void spawnEnemiesWave() {
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (enemy.isAlive()) {
                board.removeUnit(enemy);
            }
        }
        enemies.clear();

        int enemyCount = 3 + wave;

        output.displayMessage("\n--- СПАВН ВРАГОВ ---");
        for (int i = 0; i < enemyCount; i++) {

            Position spawnPos = getRandomEmptyPosition(5, 9, 5, 9);

            if (spawnPos == null) {
                output.displayError("Невозможно найти место для спавна врага #" + (i + 1) + ". Возможно, карта заполнена.");
                break;
            }

            Enemy enemy = unitFactory.createEnemy("random", spawnPos, wave);

            enemy.levelUpStats(wave);
            enemies.add(enemy);
            board.placeUnit(enemy);
            output.displayMessage(String.format("Враг %s (%s) создан на %s. HP: %d/%d, Урон: %d, Золото: %d",
                    enemy.getName(), enemy.getClass().getSimpleName(), enemy.getPosition().toString(),
                    enemy.getHealth(), enemy.getMaxHealth(), enemy.getDamage(), enemy.getGoldValue()));
        }

        output.displayMessage("⚔️ Волна " + wave + " началась! Врагов: " + enemies.size());
    }

    public void startGame() {
        while (turnCount < MAX_TURNS && !isGameOver()) {
            output.displayMessage("\n===== ХОД " + (turnCount + 1) + " =====");
            output.printBoard(board, heroes, enemies);
            output.displayUnitStats(heroes, enemies);
            output.displayMessage("💰 Золото: " + gold);

            // --- HERO PHASE ---
            output.displayMessage("\n--- ФАЗА ГЕРОЕВ ---");
            for (Hero hero : heroes) {
                if (!hero.isAlive()) {
                    continue;
                }
                playerTurnIndividual(hero);
                cleanupDeadUnits();
                output.printBoard(board, heroes, enemies);
                output.displayUnitStats(heroes, enemies);
            }

            // --- ENEMY PHASE ---
            output.displayMessage("\n--- ФАЗА ВРАГОВ ---");
            List<Enemy> currentEnemies = new ArrayList<>(enemies);
            for (Enemy enemy : currentEnemies) {
                if (!enemy.isAlive()) {
                    continue; // Skip dead enemies
                }
                performEnemyAction(enemy);
                cleanupDeadUnits();
                output.printBoard(board, heroes, enemies);
                output.displayUnitStats(heroes, enemies);
            }

            // --- END OF ROUND CHECKS ---
            if (enemies.stream().noneMatch(Enemy::isAlive)) {
                output.displayMessage("\n🌟 Волна " + wave + " пройдена!");

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
                        hero.setTaunting(false);
                    }
                    spawnEnemiesWave();
                } else {
                    break;
                }
            }
            turnCount++;
        }
        concludeGame();
    }

    private void playerTurnIndividual(Hero hero) {
        output.displayMessage("\n--- Ход Героя: " + hero.getName() + " (HP: " + hero.getHealth() + "/" + hero.getMaxHealth() + ") ---");

        List<Enemy> nearbyEnemies = enemies.stream()
                .filter(Enemy::isAlive)
                .filter(e -> hero.getPosition().distanceTo(e.getPosition()) <= hero.getRange() + 2)
                .toList();
        if (!nearbyEnemies.isEmpty()) {
            output.displayMessage("⚠️ Рядом враги! (в пределах " + (hero.getRange() + 2) + " клеток):");
            for (Enemy e : nearbyEnemies) {
                output.displayMessage(String.format("  - %s на %s HP: %d/%d", e.getName(), e.getPosition().toString(), e.getHealth(), e.getMaxHealth()));
            }
        }

        StringBuilder menu = new StringBuilder();
        menu.append("1. Двигаться (макс. ").append(hero.getSpeed()).append(" клеток)\n");

        if (!(hero instanceof Healer)) {
            menu.append("2. Атаковать\n");
        }
        menu.append("3. Способность\n");
        menu.append("Ваш выбор: ");

        int choice = input.getIntInput(menu.toString());

        switch (choice) {
            case 1 -> {
                String coordsInput = input.getStringInput("Введите координаты X Y для передвижения (через пробел): ");
                String[] parts = coordsInput.trim().split(" ");
                int x, y;
                try {
                    x = Integer.parseInt(parts[0]);
                    y = Integer.parseInt(parts[1]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    output.displayError("Неверный формат координат. Введите два числа через пробел.");
                    return;
                }
                Position newPos = new Position(x, y);

                if (!board.isValidPosition(newPos)) {
                    output.displayError("Позиция вне границ карты.");
                } else if (board.getUnitAt(newPos) != null && board.getUnitAt(newPos) != hero) {
                    output.displayError("Позиция занята другим юнитом.");
                } else if (hero.getPosition().distanceTo(newPos) > hero.getSpeed()) {
                    output.displayError("Слишком далеко. Лимит передвижения: " + hero.getSpeed() + " клеток.");
                } else {
                    hero.move(newPos, board);
                    output.printBoard(board, heroes, enemies);
                }
            }
            case 2 -> {
                if (hero instanceof Healer) {
                    output.displayError("Целитель не может атаковать!");
                    return;
                }

                output.displayMessage(hero.getName() + " атакует врагов в пределах " + hero.getRange() + " клетки(ок).");
                Enemy target = chooseTarget(hero);
                if (target != null) {
                    if (board.isInRange(hero, target)) {
                        hero.attack(target);
                    } else {
                        output.displayMessage("Цель вне досягаемости.");
                    }
                } else {
                    output.displayMessage("Атака отменена или нет доступных целей.");
                }
            }
            case 3 -> {
                hero.useAbility(heroes, enemies, board);
            }
            default -> output.displayMessage("Неверный выбор. Ход пропущен.");
        }
    }

    private void performEnemyAction(Enemy enemy) {
        output.displayMessage("\n--- Ход Врага: " + enemy.getName() + " (" + enemy.getClass().getSimpleName() + ") ---");

        if (enemy instanceof OrcShaman) {
            enemy.useAbility(heroes, enemies, board);
            // If an ability should end the enemy's turn immediately,
            // you would add 'return;' here. Otherwise, it proceeds to attack/move.
        }

        Hero target = findClosestHero(enemy);
        if (target == null) {
            output.displayMessage(enemy.getName() + " на " + enemy.getPosition().toString() + " не нашел цели.");
            return; // No target, enemy's turn ends
        }

        Position originalPosition = enemy.getPosition();

        boolean alreadyInAttackRange = board.isInRange(enemy, target);

        if (!alreadyInAttackRange) {
            moveToward(enemy, target.getPosition());
            if (!enemy.getPosition().equals(originalPosition)) {
                output.displayMessage(String.format("%s (%s) движется к %s. Текущая позиция: %s",
                        enemy.getName(), enemy.getClass().getSimpleName(), target.getName(), enemy.getPosition().toString()));
            }
        }

        if (board.isInRange(enemy, target)) {
            output.displayMessage(String.format("%s (%s) на позиции %s атакует %s (%s) на позиции %s.",
                    enemy.getName(), enemy.getClass().getSimpleName(), enemy.getPosition().toString(),
                    target.getName(), target.getClass().getSimpleName(), target.getPosition().toString()));
            enemy.attack(target);
        } else {
            if (enemy.getPosition().equals(originalPosition)) {
                output.displayMessage(enemy.getName() + " не смог добраться до цели или уже был рядом, но не в диапазоне.");
            } else {
                output.displayMessage(enemy.getName() + " переместился, но цель все еще вне досягаемости.");
            }
        }
    }

    private Enemy chooseTarget(Hero hero) {
        List<Enemy> inRangeEnemies = enemies.stream()
                .filter(Enemy::isAlive)
                .filter(e -> board.isInRange(hero, e))
                .collect(Collectors.toList());

        output.displayAvailableTargets(inRangeEnemies, hero); // Use new method

        if (inRangeEnemies.isEmpty()) {
            return null;
        }

        String inputChoice = input.getStringInput("Выберите номер цели или введите 'q' для отмены: ");
        if (inputChoice.equalsIgnoreCase("q")) {
            output.displayMessage("Выбор цели отменен.");
            return null;
        }

        try {
            int idx = Integer.parseInt(inputChoice);
            if (idx >= 0 && idx < inRangeEnemies.size()) {
                return inRangeEnemies.get(idx);
            } else {
                output.displayError("Неверный номер цели.");
            }
        } catch (NumberFormatException e) {
            output.displayError("Неверный ввод. Введите номер.");
        }
        return null;
    }

    private Hero findClosestHero(Enemy enemy) {
        Optional<Hero> tauntingTank = heroes.stream()
                .filter(h -> h.isAlive() && h.isTaunting()) // Removed getType() check
                .filter(h -> h instanceof Tank) // NEW: Check if it's an instance of Tank class
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
                    output.displayMessage(enemy.getName() + " не смог найти свободное место для передвижения и застрял.");
                }

            } else {
                board.updatePosition(enemy, bestNextPos);
            }
        }
    }


    private void cleanupDeadUnits() {
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (!enemy.isAlive()) {
                board.removeUnit(enemy);
                gold += enemy.getGoldValue();
                output.displayMessage(String.format("   %s (%s) был устранен. Получено %d золота. Всего золота: %d%n",
                        enemy.getName(), enemy.getClass().getSimpleName(), enemy.getGoldValue(), gold));
                enemyIterator.remove();
                enemiesActedThisRound.remove(enemy);
            }
        }

        List<Hero> deadHeroes = heroes.stream().filter(h -> !h.isAlive()).toList();
        for (Hero hero : deadHeroes) {
            board.removeUnit(hero);
            output.displayMessage("Герой " + hero.getName() + " на " + hero.getPosition().toString() + " пал.");
        }
    }

    private void showShop() {
        output.displayShop(gold, heroes); // New method call

        while (true) {
            int choice = input.getIntInput("Выберите апгрейд или '5' для выхода: ");

            if (choice == 5) {
                output.displayMessage("Выход из магазина.");
                break;
            }

            List<Hero> aliveHeroes = heroes.stream().filter(Hero::isAlive).collect(Collectors.toList());
            if (aliveHeroes.isEmpty()) {
                output.displayMessage("Нет живых героев для апгрейда.");
                continue;
            }

            int heroIndex = input.getIntInput("Введите номер героя: ");

            Hero targetHero = null;
            if (heroIndex >= 0 && heroIndex < aliveHeroes.size()) {
                targetHero = aliveHeroes.get(heroIndex);
            } else {
                output.displayError("Неверный номер героя.");
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
                    output.displayError("Неверный выбор апгрейда.");
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
                output.displayMessage(String.format("Успешно улучшен %s %s на %s. Золото: %d%n",
                        targetHero.getName(), upgradeType, targetHero.getPosition().toString(), gold));
                output.displayUnitStats(heroes, enemies); // Updated call
            } else {
                output.displayMessage(String.format("Недостаточно золота для этого апгрейда! Вам нужно %d, у вас %d.", cost, gold));
            }
        }
    }

    private boolean isGameOver() {
        boolean allHeroesDead = heroes.stream().noneMatch(Hero::isAlive);
        boolean allWavesCleared = wave > MAX_WAVES;
        return allHeroesDead || allWavesCleared;
    }

    private void concludeGame() {
        output.displayMessage("\n===== ИГРА ОКОНЧЕНА =====");
        if (heroes.stream().noneMatch(Hero::isAlive)) {
            output.displayMessage("Поражение. Все герои пали.");
        } else if (wave > MAX_WAVES) {
            output.displayMessage("🏆 Победа! Все " + MAX_WAVES + " волн отбиты!");
        } else {
            output.displayMessage("Игра завершена. Количество ходов: " + turnCount);
        }
        output.displayMessage("\n--- Итоговый счет ---");
        output.displayMessage("Волн пройдено: " + (wave - 1));
        output.displayMessage("Оставшееся золото: " + gold);
        output.displayMessage("Герои:");
        heroes.forEach(hero ->
                output.displayMessage(String.format("  %s: %s (Ур.%d) HP: %d/%d, Урон: %d, Дальность: %d, Скорость: %d",
                        hero.getName(), hero.isAlive() ? "Жив" : "Пал",
                        hero.getLevel(), hero.getHealth(), hero.getMaxHealth(),
                        hero.getDamage(), hero.getRange(), hero.getSpeed()))
        );
    }
}