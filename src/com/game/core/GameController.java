package com.game.core;
import com.game.board.Board;
import com.game.board.Locatable;
import com.game.board.Position;

import com.game.abilities.AbilityUser;
import com.game.units.Hero;
import com.game.units.Enemy;
import com.game.units.Tauntable;

import com.game.factory.UnitFactory;
import com.game.factory.HeroType;
import com.game.factory.EnemyType;

import com.game.io.GameInput;
import com.game.io.GameOutput;
import com.game.exceptions.GameException;
import com.game.units.heroes.Healer;
import com.game.units.enemies.OrcShaman;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameController {
    private static final int MAX_TURNS = 8;
    private static final int MAX_WAVES = 3;

    private final Board board;
    private final List<Hero> heroes;
    private final List<Enemy> enemies;
    private int turnCount = 0;
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
        for (Hero h : heroes) {
            board.removeUnit(h);
        }
        heroes.clear();

        HeroType[] types = HeroType.values();
        for (HeroType type : types) {
            Position pos = getRandomEmptyPosition(0, 4, 0, 4);
            if (pos != null) {
                Hero hero = unitFactory.createHero(type, pos);
                heroes.add(hero);
                board.place(hero);
            } else {
                output.displayError("Unable to place hero " + type + " due to lack of available slots.");
            }
        }
    }

    private void spawnEnemiesWave() {
        for (Enemy e : enemies) {
            board.removeUnit(e);
        }
        enemies.clear();

        int enemyCount = 2 + wave;
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
            board.place(enemy);
            output.displayMessage(String.format("Enemy %s (%s) created on %s. HP: %d/%d, Damage: %d, Gold: %d",
                    enemy.getName(), enemy.getClass().getSimpleName(), enemy.getPosition().toString(),
                    enemy.getHealth(), enemy.getMaxHealth(), enemy.getDamage(), enemy.getGoldValue()));
        }

        output.displayMessage("Wave " + wave + " has started. Enemies: " + enemies.size());
    }

    private EnemyType selectRandomEnemyType() {
        EnemyType[] values = EnemyType.values();
        return values[random.nextInt(values.length)];
    }

    // --- GAME LOOP ---

    public void startGame() {
        while (turnCount < MAX_TURNS && !isGameOver()) {
            output.displayMessage("\n===== MOVE " + (turnCount + 1) + " =====");
            output.printBoard(board, heroes, enemies);
            output.displayUnitStats(heroes, enemies);
            output.displayMessage("Gold: " + gold);

            // --- HERO PHASE ---
            output.displayMessage("\n--- HEROES PHASE ---");
            // 1. Process all hero actions
            for (Hero hero : heroes) {
                if (hero.isAlive()) {
                    playerTurnIndividual(hero);
                }
            }
            // Remove casualties after the hero phase (board updates before enemies act)
            cleanupDeadUnits();

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

            // Board and stats once per full turn (after hero + enemy phases)
            cleanupDeadUnits();
            output.printBoard(board, heroes, enemies);
            output.displayUnitStats(heroes, enemies);


            // --- END OF TURN CLEANUP ---
            for (Hero hero : heroes) {
                if (hero instanceof Tauntable tauntable && tauntable.isTaunting()) {
                    tauntable.setTaunting(false);
                }
            }

            if (!anyAliveEnemy()) {
                handleWaveCompletion();
            }
            turnCount++;
        }
        concludeGame();
    }

    private void handleWaveCompletion() {
        output.displayMessage("\nWave " + wave + " is over.");

        for (Hero hero : heroes) {
            if (hero.isAlive()) {
                hero.levelUp();
            }
        }

        if (wave < MAX_WAVES) {
            Shop shop = new Shop(input, output);
            gold = shop.run(gold, heroes, enemies);
        }

        wave++;
        if (wave <= MAX_WAVES) {
            spawnEnemiesWave();
        }
    }

    // --- PLAYER ACTIONS ---

    private void playerTurnIndividual(Hero hero) {
        output.displayMessage("\n--- Hero's turn: " + hero.getName() + " (HP: " + hero.getHealth() + "/" + hero.getMaxHealth() + ") ---");

        for (Enemy e : enemies) {
            if (!e.isAlive()) {
                continue;
            }
            if (hero.getPosition().distanceTo(e.getPosition()) <= hero.getRange() + 2) {
                output.displayMessage(String.format("  - %s at %s HP: %d/%d",
                        e.getName(),
                        e.getPosition().toString(),
                        e.getHealth(),
                        e.getMaxHealth()));
            }
        }

        try {
            int choice = input.getIntInput(buildHeroMenu(hero));
            if (choice == 1) {
                handleHeroMovement(hero);
            } else if (choice == 2) {
                handleHeroAttack(hero);
            } else if (choice == 3 && hero instanceof AbilityUser abilityUser){
                handleHeroAbility(abilityUser);
            } else {
                throw new GameException("Wrong choice. Turn skipped.");
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
        if (hero instanceof AbilityUser) {
            menu.append("3. Ability\n");
        }
        menu.append("Your choice is: ");
        return menu.toString();
    }

    private void handleHeroMovement(Hero hero) throws GameException {
        Position newPos = input.getPositionInput("Enter the X and Y coordinates for movement (separated by a space): ");

        if (!board.isValidPosition(newPos)) {
            throw new GameException("Position outside the map boundaries.");
        }

        Locatable unitAtNewPos = board.getLocatableAt(newPos);
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
            if (hero.isInRange(target)) {
                output.displayMessage(hero.attack(target));
            } else {
                throw new GameException("The goal is out of reach.");
            }
        } else {
            output.displayMessage("The attack is canceled or there are no available targets.");
        }
    }

    private void handleHeroAbility(AbilityUser user) {
        user.useAbility(heroes, enemies, board);
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

        // --- 2. ATTACK FIRST ---
        if (enemy.isInRange(targetHero)) {
            performEnemyAttack(enemy, targetHero, "Immediate Attack");
            return;
        }

        // --- 3. MOVEMENT: one grid step at a time toward the hero; stop if blocked or out of moves. ---
        moveToward(enemy, targetHero.getPosition());

        if (!enemy.getPosition().equals(originalPosition)) {
            output.displayMessage(String.format("%s (%s) moved toward %s. Current position: %s",
                    enemy.getName(), enemy.getClass().getSimpleName(), targetHero.getName(), enemy.getPosition().toString()));
        }

        // --- 4. POST-MOVEMENT ATTACK ---
        if (enemy.isInRange(targetHero)) {
            performEnemyAttack(enemy, targetHero, "Post-Move Attack");
        } else if (enemy.getPosition().equals(originalPosition)) {
            output.displayMessage(enemy.getName() + " couldn't find a free space to move and got stuck.");
        } else {
            output.displayMessage(enemy.getName() + " moved, but the goal is still out of reach.");
        }
    }

    private void performEnemyAttack(Enemy enemy, Hero targetHero, String phaseDescription) {
        output.displayMessage(String.format("%s (%s) at position %s attacks %s (%s) at position %s (%s).",
                enemy.getName(), enemy.getClass().getSimpleName(), enemy.getPosition().toString(),
                targetHero.getName(), targetHero.getClass().getSimpleName(), targetHero.getPosition().toString(),
                phaseDescription));
        output.displayMessage(enemy.attack(targetHero));
    }

    private Hero findClosestHero(Enemy enemy) {
        for (Hero hero : heroes) {
            if (hero.isAlive() && hero instanceof Tauntable tauntable && tauntable.isTaunting()) {
                return hero;
            }
        }

        Hero closest = null;
        int bestDistance = Integer.MAX_VALUE;
        Position enemyPos = enemy.getPosition();
        for (Hero hero : heroes) {
            if (!hero.isAlive()) {
                continue;
            }
            int distance = hero.getPosition().distanceTo(enemyPos);
            if (distance < bestDistance) {
                bestDistance = distance;
                closest = hero;
            }
        }
        return closest;
    }

    private Enemy chooseTarget(Hero hero) {
        List<Enemy> inRangeEnemies = new ArrayList<>();
        for (Enemy e : enemies) {
            if (e.isAlive() && hero.isInRange(e)) {
                inRangeEnemies.add(e);
            }
        }

        output.displayAvailableTargets(inRangeEnemies, hero);

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
        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (!enemy.isAlive()) {
                board.removeUnit(enemy);
                gold += enemy.getGoldValue();
                output.displayMessage(String.format("   %s (%s) was eliminated. Received %d gold. Total gold: %d%n",
                        enemy.getName(), enemy.getClass().getSimpleName(), enemy.getGoldValue(), gold));
                enemyIterator.remove();
            }
        }

        Iterator<Hero> heroIterator = heroes.iterator();
        while (heroIterator.hasNext()) {
            Hero hero = heroIterator.next();
            if (!hero.isAlive()) {
                board.removeUnit(hero);
                output.displayMessage("Hero " + hero.getName() + " at position " + hero.getPosition().toString() + " has fallen.");
                heroIterator.remove();
            }
        }
    }

    /**
     * Living ally at or below 50% HP with the lowest current HP (OrcShaman heal target).
     */
    private Enemy findWoundedAlly(List<Enemy> enemyList) {
        Enemy best = null;
        int lowestHp = Integer.MAX_VALUE;
        for (Enemy e : enemyList) {
            if (!e.isAlive()) {
                continue;
            }
            if (e.getMaxHealth() <= 0) {
                continue;
            }
            double ratio = (double) e.getHealth() / e.getMaxHealth();
            if (ratio > 0.5) {
                continue;
            }
            if (e.getHealth() < lowestHp) {
                lowestHp = e.getHealth();
                best = e;
            }
        }
        return best;
    }

    private boolean hasAliveHero() {
        for (Hero h : heroes) {
            if (h.isAlive()) {
                return true;
            }
        }
        return false;
    }

    private boolean anyAliveEnemy() {
        for (Enemy e : enemies) {
            if (e.isAlive()) {
                return true;
            }
        }
        return false;
    }

    // --- GAME END ---

    private boolean isGameOver() {
        return !hasAliveHero() || wave > MAX_WAVES;
    }

    private void concludeGame() {
        output.displayMessage("\n===== GAME IS OVER =====");
        if (!hasAliveHero()) {
            output.displayMessage("Defeat. All heroes have fallen.");
        } else if (wave > MAX_WAVES) {
            output.displayMessage("Victory! All " + MAX_WAVES + " waves repelled.");
        } else {
            output.displayMessage("The game is over. Number of moves: " + turnCount);
        }
        output.displayMessage("\n--- Final score ---");
        output.displayMessage("Waves passed: " + (wave - 1));
        output.displayMessage("Remaining gold: " + gold);
        output.displayMessage("Heroes:");
        for (Hero hero : heroes) {
            output.displayMessage(String.format("  %s: %s (Lvl.%d) HP: %d/%d, Damage: %d, Range: %d, Speed: %d",
                    hero.getName(), hero.isAlive() ? "Alive" : "Fell",
                    hero.getLevel(), hero.getHealth(), hero.getMaxHealth(),
                    hero.getDamage(), hero.getRange(), hero.getSpeed()));
        }
    }

    /**
     * Greedy chase: each step moves one cell along x toward the target, else along y.
     * Stops early if the next cell is off the map or occupied.
     */
    private void moveToward(Enemy enemy, Position targetPos) {
        int targetX = targetPos.getX();
        int targetY = targetPos.getY();

        for (int step = 0; step < enemy.getSpeed(); step++) {
            Position here = enemy.getPosition();
            int curX = here.getX();
            int curY = here.getY();

            if (curX == targetX && curY == targetY) {
                break;
            }

            int nextX = curX;
            int nextY = curY;
            if (curX != targetX) {
                nextX = curX + (curX < targetX ? 1 : -1);
            } else {
                nextY = curY + (curY < targetY ? 1 : -1);
            }

            Position nextPos = new Position(nextX, nextY);
            if (!board.isValidPosition(nextPos) || !board.isEmpty(nextPos)) {
                break;
            }

            board.updatePosition(enemy, nextPos);
        }
    }
}