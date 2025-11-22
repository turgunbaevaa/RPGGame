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
    private final int MAX_TURNS = 10;
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
                output.displayError("Unable to find an available spot after numerous attempts. The board may be full.");
                return null;
            }
        } while (!board.isEmpty(pos));
        return pos;
    }

    private void setupHeroes() {
        heroes.forEach(board::removeUnit);
        heroes.clear();

        HeroType[] types = HeroType.values();
        for (HeroType type : types) {
            Position pos = getRandomEmptyPosition(0, 4, 0, 4);
            if (pos != null) {
                Hero hero = unitFactory.createHero(type, pos);
                Unit unitReference = hero;
                heroes.add(hero);
                board.placeUnit(unitReference);
            } else {
                output.displayError("Unable to place hero " + type + " due to lack of available slots.");
            }
        }
    }

    private void spawnEnemiesWave() {
        enemies.forEach(board::removeUnit);
        enemies.clear();

        int enemyCount = 3 + wave;
        output.displayMessage("\n--- ENEMIES SPAWN ---");

        for (int i = 0; i < enemyCount; i++) {
            Position spawnPos = getRandomEmptyPosition(5, 9, 5, 9);
            if (spawnPos == null) {
                output.displayError("Unable to locate enemy spawn point #" + (i + 1) + ". The map may be full.");
                break;
            }

            EnemyType type = selectRandomEnemyType();
            Enemy enemy = unitFactory.createEnemy(type, spawnPos, wave);

            enemies.add(enemy);
            board.placeUnit(enemy);
            output.displayMessage(String.format("Enemy %s (%s) created on %s. HP: %d/%d, Damage: %d, Gold: %d",
                    enemy.getName(), enemy.getClass().getSimpleName(), enemy.getPosition().toString(),
                    enemy.getHealth(), enemy.getMaxHealth(), enemy.getDamage(), enemy.getGoldValue()));
        }

        output.displayMessage("⚔️ Wave has " + wave + " started! Enemies: " + enemies.size());
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
        while (turnCount < MAX_TURNS && !isGameOver()) {
            output.displayMessage("\n===== MOVE " + (turnCount + 1) + " =====");
            output.printBoard(board, heroes, enemies);
            output.displayUnitStats(heroes, enemies);
            output.displayMessage("💰 GOLD: " + gold);

            // --- HERO PHASE ---
            output.displayMessage("\n--- HEROES PHASE ---");
            // 1. Process all hero actions
            for (Hero hero : heroes) {
                if (hero.isAlive()) {
                    playerTurnIndividual(hero);
                }
            }
            // 2. Cleanup and printing occur ONCE after all heroes have acted
            cleanupDeadUnits();
            output.printBoard(board, heroes, enemies);
            output.displayUnitStats(heroes, enemies);


            // --- ENEMY PHASE ---
            output.displayMessage("\n--- ENEMIES PHASE ---");

            // 1. Use a copy to safely iterate over the starting enemies list
            List<Enemy> currentEnemies = new ArrayList<>(enemies);
            for (Enemy enemy : currentEnemies) {
                // Only act if the enemy is still alive (it might have died during a hero's previous turn/ability)
                if (enemy.isAlive()) {
                    performEnemyAction(enemy);
                }
            }

            // 2. Cleanup and printing occur ONCE after ALL enemies have acted
            cleanupDeadUnits();
            output.printBoard(board, heroes, enemies);
            output.displayUnitStats(heroes, enemies);


            // --- END OF TURN CLEANUP ---
            // Remove Taunt status before the next turn
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
        output.displayMessage("\n🌟 Wave " + wave + " is over!");

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
        output.displayMessage("\n--- Hero's turn: " + hero.getName() + " (HP: " + hero.getHealth() + "/" + hero.getMaxHealth() + ") ---");

        // Simplified nearby enemy check for readability
        enemies.stream()
                .filter(Enemy::isAlive)
                .filter(e -> hero.getPosition().distanceTo(e.getPosition()) <= hero.getRange() + 2)
                .forEach(e -> output.displayMessage(String.format("  - %s на %s HP: %d/%d",
                        e.getName(),
                        e.getPosition().toString(),
                        e.getHealth(),
                        e.getMaxHealth())));

        try {
            int choice = input.getIntInput(buildHeroMenu(hero));
            switch (choice) {
                case 1 -> handleHeroMovement(hero);
                case 2 -> handleHeroAttack(hero);
                case 3 -> hero.useAbility(heroes, enemies, board);
                default -> throw new GameException("Wrong choice. Turn skipped.");
            }
        } catch (GameException e) {
            output.displayError(e.getMessage());
        } catch (Exception e) { // Catch other potential exceptions (like NumberFormatException from PositionInput)
            output.displayError("Input error: " + e.getMessage());
        }
    }

    private String buildHeroMenu(Hero hero) {
        StringBuilder menu = new StringBuilder();
        menu.append("1. Move (max. ").append(hero.getSpeed()).append(" cells)\n");
        if (!(hero instanceof Healer)) {
            menu.append("2. Attack\n");
        }
        menu.append("3. Ability\n");
        menu.append("Your choice is : ");
        return menu.toString();
    }

    private void handleHeroMovement(Hero hero) throws GameException {
        Position newPos = input.getPositionInput("Enter the X and Y coordinates for movement (separated by a space): ");

        if (!board.isValidPosition(newPos)) {
            throw new GameException("Position outside the map boundaries.");
        }

        Unit unitAtNewPos = board.getUnitAt(newPos);
        if (unitAtNewPos != null && unitAtNewPos != hero) {
            throw new GameException("The position is occupied by another unit.");
        }

        if (hero.getPosition().distanceTo(newPos) > hero.getSpeed()) {
            throw new GameException("Too far. Movement limit: " + hero.getSpeed() + " cells.");
        }

        hero.move(newPos, board);
    }

    private void handleHeroAttack(Hero hero) throws GameException {
        if (hero instanceof Healer) {
            throw new GameException("The healer cannot attack!");
        }

        output.displayMessage(hero.getName() + " attacks enemies within " + hero.getRange() + " cells.");
        Enemy target = chooseTarget(hero);

        if (target != null) {
            if (board.isInRange(hero, target)) {
                hero.attack(target);
            } else {
                // This check is redundant if chooseTarget only shows in-range targets,
                // but kept for robustness if input allows out-of-range selection.
                throw new GameException("The goal is out of reach.");
            }
        } else {
            output.displayMessage("The attack is canceled or there are no available targets.");
        }
    }

    // --- ENEMY AI ---

    private void performEnemyAction(Enemy enemy) {
        output.displayMessage("\n--- Enemies turn: " + enemy.getName() + " (" + enemy.getClass().getSimpleName() + ") ---");

        Hero targetHero = findClosestHero(enemy);
        // Ensure the target is alive before proceeding
        if (targetHero == null || !targetHero.isAlive()) {
            output.displayMessage(enemy.getName() + " has not found a target and ends its turn.");
            return;
        }

        Position originalPosition = enemy.getPosition();

        // --- 1. ABILITY CHECK (OrcShaman Healing Priority) ---
        if (enemy instanceof OrcShaman) {
            Enemy woundedAlly = findWoundedAlly(enemies);
            if (woundedAlly != null) {
                enemy.useAbility(heroes, enemies, board);
                // Turn continues after healing. NO return here.
            }
        }

        // --- 2. ATTACK FIRST CHECK ---
        // If the unit is already in range, attack immediately and end the turn.
        if (board.isInRange(enemy, targetHero)) {
            output.displayMessage(String.format("%s (%s) at position %s attacks %s (%s) at position %s (Immediate Attack).",
                    enemy.getName(), enemy.getClass().getSimpleName(), enemy.getPosition().toString(),
                    targetHero.getName(), targetHero.getClass().getSimpleName(), targetHero.getPosition().toString()));
            enemy.attack(targetHero);

            return;
        }

        // --- 3. MOVEMENT (Only if NOT in range) ---
        moveToward(enemy, targetHero.getPosition());

        // Output movement message
        if (!enemy.getPosition().equals(originalPosition)) {
            output.displayMessage(String.format("%s (%s) moved toward %s. Current position: %s",
                    enemy.getName(), enemy.getClass().getSimpleName(), targetHero.getName(), enemy.getPosition().toString()));
        }

        // --- 4. POST-MOVEMENT ATTACK ---
        // Check range again after movement
        if (board.isInRange(enemy, targetHero)) {
            output.displayMessage(String.format("%s (%s) at position %s attacks %s (%s) at position %s (Post-Move Attack).",
                    enemy.getName(), enemy.getClass().getSimpleName(), enemy.getPosition().toString(),
                    targetHero.getName(), targetHero.getClass().getSimpleName(), targetHero.getPosition().toString()));
            enemy.attack(targetHero);
        } else {
            // Output stuck message
            if (enemy.getPosition().equals(originalPosition)) {
                output.displayMessage(enemy.getName() + " couldn't find a free space to move and got stuck.");
            } else {
                output.displayMessage(enemy.getName() + " moved, but the goal is still out of reach.");
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

        String inputChoice = input.getStringInput("Select the target number or enter 'q' to cancel: ");
        if (inputChoice.equalsIgnoreCase("q")) {
            output.displayMessage("Target selection canceled.");
            return null;
        }

        try {
            int idx = Integer.parseInt(inputChoice);
            if (idx >= 0 && idx < inRangeEnemies.size()) {
                return inRangeEnemies.get(idx);
            } else {
                output.displayError("Incorrect target number.");
            }
        } catch (NumberFormatException e) {
            output.displayError("Incorrect entry. Please enter the number.");
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
                output.displayMessage(String.format("   %s (%s) was eliminated. Received %d gold(s). Total gold: %d%n",
                        enemy.getName(), enemy.getClass().getSimpleName(), enemy.getGoldValue(), gold));
                return true;
            }
            return false;
        });

        // Remove dead heroes from the board
        heroes.stream().filter(h -> !h.isAlive()).forEach(hero -> {
            board.removeUnit(hero);
            output.displayMessage("Hero " + hero.getName() + " at position " + hero.getPosition().toString() + " fall.");
        });
    }

    /**
     * Finds the most wounded living enemy unit.
     * Only targets enemies below 50% health for 'critical' healing priority.
     * Used by OrcShaman AI.
     */
    private Enemy findWoundedAlly(List<Enemy> enemies) {
        return enemies.stream()
                .filter(Enemy::isAlive)
                // Filter for targets critically wounded (50% or less HP)
                .filter(e -> (double)e.getHealth() / e.getMaxHealth() <= 0.5)
                // Find the one with the lowest absolute current HP
                .min(Comparator.comparingInt(Enemy::getHealth))
                .orElse(null);
    }

    // --- SHOP (Refactored) ---

    private void showShop() {
        output.displayShop(gold, heroes);

        while (true) {
            int choice = input.getIntInput("Select upgrade or ‘5’ to exit: ");

            if (choice == 5) {
                output.displayMessage("Exiting the store.");
                break;
            }

            List<Hero> aliveHeroes = heroes.stream().filter(Hero::isAlive).collect(Collectors.toList());
            if (aliveHeroes.isEmpty()) {
                output.displayMessage("No alive heroes to upgrade.");
                continue;
            }

            Hero targetHero = selectHeroForUpgrade(aliveHeroes);
            if (targetHero == null) continue;

            handleUpgradePurchase(choice, targetHero);
        }
    }

    private Hero selectHeroForUpgrade(List<Hero> aliveHeroes) {
        int heroIndex = input.getIntInput("Enter hero number: ");

        if (heroIndex >= 0 && heroIndex < aliveHeroes.size()) {
            return aliveHeroes.get(heroIndex);
        } else {
            output.displayError("Incorrect hero number.");
            return null;
        }
    }

    private void handleUpgradePurchase(int choice, Hero targetHero) {
        int cost;
        String upgradeType;

        switch (choice) {
            case 1: cost = 20; upgradeType = "HP"; break;
            case 2: cost = 15; upgradeType = "Damage"; break;
            case 3: cost = 25; upgradeType = "Speed"; break;
            case 4: cost = 20; upgradeType = "Range"; break;
            default:
                output.displayError("Incorrect upgrade selection.");
                return;
        }

        if (gold >= cost) {
            gold -= cost;
            applyUpgrade(choice, targetHero);
            output.displayMessage(String.format("Successfully improved %s %s by %s. Gold: %d%n",
                    targetHero.getName(), upgradeType, targetHero.getPosition().toString(), gold));
            output.displayUnitStats(heroes, enemies);
        } else {
            output.displayMessage(String.format("Not enough gold for this upgrade! You need %d, you have %d.", cost, gold));
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
        output.displayMessage("\n===== GAME IS OVER =====");
        if (heroes.stream().noneMatch(Hero::isAlive)) {
            output.displayMessage("Defeat. All heroes have fallen.");
        } else if (wave > MAX_WAVES) {
            output.displayMessage("🏆 Victory! All " + MAX_WAVES + " waves repelled!");
        } else {
            output.displayMessage("The game is over. Number of moves: " + turnCount);
        }
        output.displayMessage("\n--- Final score ---");
        output.displayMessage("Waves passed: " + (wave - 1));
        output.displayMessage("Remaining gold: " + gold);
        output.displayMessage("Heroes:");
        heroes.forEach(hero ->
                output.displayMessage(String.format("  %s: %s (Ур.%d) HP: %d/%d, Damage: %d, Range: %d, Speed: %d",
                        hero.getName(), hero.isAlive() ? "Alive" : "Fell",
                        hero.getLevel(), hero.getHealth(), hero.getMaxHealth(),
                        hero.getDamage(), hero.getRange(), hero.getSpeed()))
        );
    }

    private void moveToward(Enemy enemy, Position targetPos) {
        Position current = enemy.getPosition();
        Position bestNextPos = current;
        int minDistance = current.distanceTo(targetPos);
        // 1. Generate all possible move positions based on speed
        List<Position> possibleMoves = new ArrayList<>();
        for (int dx = -enemy.getSpeed(); dx <= enemy.getSpeed(); dx++) {
            for (int dy = -enemy.getSpeed(); dy <= enemy.getSpeed(); dy++) {
                // Check Manhattan distance for movement limit
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

        // 2. Find the STRICTLY BEST (closest distance) empty spot
        Position bestCloserSpot = current;
        for (Position move : possibleMoves) {
            if (board.isEmpty(move)) {
                if (move.distanceTo(targetPos) < minDistance) {
                    bestCloserSpot = move;
                    break; // Found the best spot that closes the distance, we can stop
                }
            }
        }
        // 3. Determine Final Position
        if (!bestCloserSpot.equals(current)) {
            // If we found a spot that is strictly closer, use it.
            bestNextPos = bestCloserSpot;
        } else {
            // If no strictly closer spot was found (e.g., blocked or already adjacent)
            for (Position move : possibleMoves) {
                if (board.isEmpty(move)) {
                    bestNextPos = move;
                    break;
                }
            }
        }
        // 4. Execute Movement
        if (!bestNextPos.equals(current)) {
            board.updatePosition(enemy, bestNextPos);
        } else {
            output.displayMessage(enemy.getName() + " couldn't find a free space to move and got stuck.");
        }
    }
}