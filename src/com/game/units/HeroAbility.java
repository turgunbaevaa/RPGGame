package com.game.units;
import com.game.board.Board;

import java.util.List;

/**
 * Represents a special ability that a hero can use during the game.
 */
public interface HeroAbility {
    void use(Hero self, List<Hero> allHeroes, List<Enemy> allEnemies, Board board);
}