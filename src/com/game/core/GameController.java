package com.game.core;
import com.game.board.Board;
import com.game.board.Position;

import com.game.units.Hero;
import com.game.units.Enemy;

import com.game.factory.UnitFactory;
import com.game.factory.HeroType;
import com.game.factory.EnemyType;

import com.game.io.GameInput;
import com.game.io.GameOutput;
import com.game.exceptions.GameException;
import com.game.units.Unit;
import com.game.units.heroes.Healer;
import com.game.units.enemies.OrcShaman;

import java.util.*;
import java.util.stream.Collectors;

public class GameController {
    private final Board board;
    private final List<Hero> heroes;
    private final List<Enemy> enemies;
    private int turnCount = 0;
    private final int MAX_TURNS = 50;
    private final int MAX_WAVES = 5;
    private int wave = 1;
    private int gold = 0;
    private final Random random;
    private final GameInput input;
    private final GameOutput output;
    private final UnitFactory unitFactory;

    public GameController(Board board, Random random, GameInput input, GameOutput output, UnitFactory unitFactory) {
        this.board = board;
        this.random = random;
        this.input = input;
        this.output = output;
        this.unitFactory = unitFactory;

        heroes = new ArrayList<>();
        enemies = new ArrayList<>();

        setupHeroes();
        spawnEnemiesWave();
    }

    // --- SETUP & SPAWN METHODS ---

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
        // Cleanup logic simplified to ensure all unit references are clear
        heroes.forEach(board::removeUnit);
        heroes.clear();

        // Placing units in a loop is cleaner and handles null positions more gracefully
        HeroType[] types = HeroType.values();
        for (HeroType type : types) {
            Position pos = getRandomEmptyPosition(0, 4, 0, 4);
            if (pos != null) {
                Hero hero = unitFactory.createHero(type, pos);
                heroes.add(hero);
                board.placeUnit(hero);
            } else {
                output.displayError("Не удалось разместить героя " + type + " из-за отсутствия свободных мест.");
            }
        }
    }

    private void spawnEnemiesWave() {
        enemies.forEach(board::removeUnit);
        enemies.clear();

        int enemyCount = 3 + wave;
        output.displayMessage("\n--- СПАВН ВРАГОВ ---");

        for (int i = 0; i < enemyCount; i++) {
            Position spawnPos = getRandomEmptyPosition(5, 9, 5, 9);
            if (spawnPos == null) {
                output.displayError("Невозможно найти место для спавна врага #" + (i + 1) + ". Возможно, карта заполнена.");
                break;
            }

            EnemyType type = selectRandomEnemyType();
            Enemy enemy = unitFactory.createEnemy(type, spawnPos, wave);

            enemies.add(enemy);
            board.placeUnit(enemy);
            output.displayMessage(String.format("Враг %s (%s) создан на %s. HP: %d/%d, Урон: %d, Золото: %d",
                    enemy.getName(), enemy.getClass().getSimpleName(), enemy.getPosition().toString(),
                    enemy.getHealth(), enemy.getMaxHealth(), enemy.getDamage(), enemy.getGoldValue()));
        }

        output.displayMessage("⚔️ Волна " + wave + " началась! Врагов: " + enemies.size());
    }

    private EnemyType selectRandomEnemyType() {
        double rand = random.nextDouble();
        if (rand < 0.4) {
            return EnemyType.GOBLIN_GRUNT;
        } else if (rand < 0.75) {
            return EnemyType.SKELETON_ARCHER;
        } else {
            return EnemyType.ORC_SHAMAN;
        }
    }

    // --- GAME LOOP ---

    public void startGame() {
        // ... (unchanged while loop and phase structure)
        while (turnCount < MAX_TURNS && !isGameOver()) {
            output.displayMessage("\n===== ХОД " + (turnCount + 1) + " =====");
            output.printBoard(board, heroes, enemies);
            output.displayUnitStats(heroes, enemies);
            output.displayMessage("💰 Золото: " + gold);

            // --- HERO PHASE ---
            output.displayMessage("\n--- ФАЗА ГЕРОЕВ ---");
            for (Hero hero : heroes) {
                if (hero.isAlive()) {
                    playerTurnIndividual(hero);
                    cleanupDeadUnits();
                    output.printBoard(board, heroes, enemies);
                    output.displayUnitStats(heroes, enemies);
                }
            }

            // --- ENEMY PHASE ---
            output.displayMessage("\n--- ФАЗА ВРАГОВ ---");

            List<Enemy> currentEnemies = new ArrayList<>(enemies);
            for (Enemy enemy : currentEnemies) {
                if (enemy.isAlive()) {
                    performEnemyAction(enemy);
                    cleanupDeadUnits();
                    output.printBoard(board, heroes, enemies);
                    output.displayUnitStats(heroes, enemies);
                }
            }

            // --- END OF TURN CLEANUP ---
            heroes.stream().filter(Hero::isTaunting).forEach(h -> h.setTaunting(false));

            if (enemies.stream().noneMatch(Enemy::isAlive)) {
                handleWaveCompletion();
            }
            turnCount++;
        }
        concludeGame();
    }

    // Extracted wave completion logic
    private void handleWaveCompletion() {
        output.displayMessage("\n🌟 Волна " + wave + " пройдена!");

        heroes.stream().filter(Hero::isAlive).forEach(Hero::levelUp);

        if (wave < MAX_WAVES) {
            showShop();
        }

        wave++;
        if (wave <= MAX_WAVES) {
            spawnEnemiesWave();
        }
    }

    // --- PLAYER ACTIONS (Refactored) ---

    private void playerTurnIndividual(Hero hero) {
        output.displayMessage("\n--- Ход Героя: " + hero.getName() + " (HP: " + hero.getHealth() + "/" + hero.getMaxHealth() + ") ---");

        // Simplified nearby enemy check for readability
        enemies.stream()
                .filter(Enemy::isAlive)
                .filter(e -> hero.getPosition().distanceTo(e.getPosition()) <= hero.getRange() + 2)
                .forEach(e -> output.displayMessage(String.format("  - %s на %s HP: %d/%d", e.getName(), e.getPosition().toString(), e.getHealth(), e.getMaxHealth())));

        try {
            int choice = input.getIntInput(buildHeroMenu(hero));
            switch (choice) {
                case 1 -> handleHeroMovement(hero);
                case 2 -> handleHeroAttack(hero);
                case 3 -> hero.useAbility(heroes, enemies, board);
                default -> throw new GameException("Неверный выбор. Ход пропущен.");
            }
        } catch (GameException e) {
            output.displayError(e.getMessage());
        } catch (Exception e) { // Catch other potential exceptions (like NumberFormatException from PositionInput)
            output.displayError("Ошибка ввода: " + e.getMessage());
        }
    }

    private String buildHeroMenu(Hero hero) {
        StringBuilder menu = new StringBuilder();
        menu.append("1. Двигаться (макс. ").append(hero.getSpeed()).append(" клеток)\n");
        if (!(hero instanceof Healer)) {
            menu.append("2. Атаковать\n");
        }
        menu.append("3. Способность\n");
        menu.append("Ваш выбор: ");
        return menu.toString();
    }

    private void handleHeroMovement(Hero hero) throws GameException {
        Position newPos = input.getPositionInput("Введите координаты X Y для передвижения (через пробел): ");

        if (!board.isValidPosition(newPos)) {
            throw new GameException("Позиция вне границ карты.");
        }

        Unit unitAtNewPos = board.getUnitAt(newPos);
        if (unitAtNewPos != null && unitAtNewPos != hero) {
            throw new GameException("Позиция занята другим юнитом.");
        }

        if (hero.getPosition().distanceTo(newPos) > hero.getSpeed()) {
            throw new GameException("Слишком далеко. Лимит передвижения: " + hero.getSpeed() + " клеток.");
        }

        hero.move(newPos, board);
    }

    private void handleHeroAttack(Hero hero) throws GameException {
        if (hero instanceof Healer) {
            throw new GameException("Целитель не может атаковать!");
        }

        output.displayMessage(hero.getName() + " атакует врагов в пределах " + hero.getRange() + " клетки(ок).");
        Enemy target = chooseTarget(hero);

        if (target != null) {
            if (board.isInRange(hero, target)) {
                hero.attack(target);
            } else {
                // This check is redundant if chooseTarget only shows in-range targets,
                // but kept for robustness if input allows out-of-range selection.
                throw new GameException("Цель вне досягаемости.");
            }
        } else {
            output.displayMessage("Атака отменена или нет доступных целей.");
        }
    }

    // --- ENEMY AI ---

    private void performEnemyAction(Enemy enemy) {
        output.displayMessage("\n--- Ход Врага: " + enemy.getName() + " (" + enemy.getClass().getSimpleName() + ") ---");

        if (enemy instanceof OrcShaman) {
            enemy.useAbility(heroes, enemies, board);
        }

        Hero target = findClosestHero(enemy);
        if (target == null) {
            output.displayMessage(enemy.getName() + " на " + enemy.getPosition().toString() + " не нашел цели.");
            return;
        }

        Position originalPosition = enemy.getPosition();

        // 1. Move
        if (!board.isInRange(enemy, target)) {
            moveToward(enemy, target.getPosition());
            if (!enemy.getPosition().equals(originalPosition)) {
                output.displayMessage(String.format("%s (%s) движется к %s. Текущая позиция: %s",
                        enemy.getName(), enemy.getClass().getSimpleName(), target.getName(), enemy.getPosition().toString()));
            }
        }

        // 2. Attack
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

    private Hero findClosestHero(Enemy enemy) {
        // Find taunting hero first
        Optional<Hero> tauntingHero = heroes.stream()
                .filter(Hero::isAlive)
                .filter(Hero::isTaunting)
                .findFirst();

        // If a taunting hero exists, return them (Taunt overrides proximity)
        return tauntingHero.orElseGet(() -> heroes.stream()
                .filter(Hero::isAlive)
                .min(Comparator.comparingInt(h -> h.getPosition().distanceTo(enemy.getPosition())))
                .orElse(null));

        // Otherwise, find the closest hero
    }

    private Enemy chooseTarget(Hero hero) {
        List<Enemy> inRangeEnemies = enemies.stream()
                .filter(Enemy::isAlive)
                .filter(e -> board.isInRange(hero, e))
                .collect(Collectors.toList());

        output.displayAvailableTargets(inRangeEnemies, hero); // This method must be in com.game.io.GameOutput

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

    // --- UTILITIES ---

    private void cleanupDeadUnits() {
        // Collect gold and remove dead enemies from the board
        enemies.removeIf(enemy -> {
            if (!enemy.isAlive()) {
                board.removeUnit(enemy);
                gold += enemy.getGoldValue();
                output.displayMessage(String.format("   %s (%s) был устранен. Получено %d золота. Всего золота: %d%n",
                        enemy.getName(), enemy.getClass().getSimpleName(), enemy.getGoldValue(), gold));
                return true;
            }
            return false;
        });

        // Remove dead heroes from the board
        heroes.stream().filter(h -> !h.isAlive()).forEach(hero -> {
            board.removeUnit(hero);
            output.displayMessage("Герой " + hero.getName() + " на " + hero.getPosition().toString() + " пал.");
        });
    }

    // --- SHOP (Refactored) ---

    private void showShop() {
        output.displayShop(gold, heroes);

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

            Hero targetHero = selectHeroForUpgrade(aliveHeroes);
            if (targetHero == null) continue;

            handleUpgradePurchase(choice, targetHero);
        }
    }

    private Hero selectHeroForUpgrade(List<Hero> aliveHeroes) {
        int heroIndex = input.getIntInput("Введите номер героя: ");

        if (heroIndex >= 0 && heroIndex < aliveHeroes.size()) {
            return aliveHeroes.get(heroIndex);
        } else {
            output.displayError("Неверный номер героя.");
            return null;
        }
    }

    private void handleUpgradePurchase(int choice, Hero targetHero) {
        int cost;
        String upgradeType;

        switch (choice) {
            case 1: cost = 20; upgradeType = "HP"; break;
            case 2: cost = 15; upgradeType = "Урон"; break;
            case 3: cost = 25; upgradeType = "Скорость"; break;
            case 4: cost = 20; upgradeType = "Дальность"; break;
            default:
                output.displayError("Неверный выбор апгрейда.");
                return;
        }

        if (gold >= cost) {
            gold -= cost;
            applyUpgrade(choice, targetHero);
            output.displayMessage(String.format("Успешно улучшен %s %s на %s. Золото: %d%n",
                    targetHero.getName(), upgradeType, targetHero.getPosition().toString(), gold));
            output.displayUnitStats(heroes, enemies);
        } else {
            output.displayMessage(String.format("Недостаточно золота для этого апгрейда! Вам нужно %d, у вас %d.", cost, gold));
        }
    }

    private void applyUpgrade(int choice, Hero targetHero) {
        switch (choice) {
            case 1: targetHero.upgradeHealthStat(20); break;
            case 2: targetHero.upgradeDamageStat(5); break;
            case 3: targetHero.upgradeSpeedStat(1); break;
            case 4: targetHero.upgradeRangeStat(1); break;
        }
    }

    // --- GAME END ---

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
        // ... (rest of the conclusion output)
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

    // Keeping moveToward as it is, since its logic is already quite complex and well-contained.
    private void moveToward(Enemy enemy, Position targetPos) {
        Position current = enemy.getPosition();
        Position bestNextPos = current;
        int minDistance = current.distanceTo(targetPos);

        List<Position> possibleMoves = new ArrayList<>();
        for (int dx = -enemy.getSpeed(); dx <= enemy.getSpeed(); dx++) {
            for (int dy = -enemy.getSpeed(); dy <= enemy.getSpeed(); dy++) {
                if (Math.abs(dx) + Math.abs(dy) <= enemy.getSpeed()) {
                    Position potentialNext = new Position(current.x() + dx, current.y() + dy);
                    if (board.isValidPosition(potentialNext)) {
                        possibleMoves.add(potentialNext);
                    }
                }
            }
        }

        // Sort moves to prioritize those that close the distance the most
        possibleMoves.sort(Comparator.comparingInt(pos -> pos.distanceTo(targetPos)));

        // Try to find the best empty spot that is closer to the target
        for (Position move : possibleMoves) {
            if (board.isEmpty(move)) {
                if (move.distanceTo(targetPos) < minDistance) {
                    bestNextPos = move;
                    break;
                }
            }
        }

        // If the best move is a position that would allow an attack, take it.
        // Otherwise, move to the best available empty spot.
        if (!bestNextPos.equals(current)) {
            board.updatePosition(enemy, bestNextPos);
        } else {
            output.displayMessage(enemy.getName() + " не смог найти свободное место для передвижения и застрял.");
        }
    }
}