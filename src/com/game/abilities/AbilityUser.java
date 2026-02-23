package com.game.abilities;

import com.game.board.Board;
import com.game.units.Enemy;
import com.game.units.Hero;

import java.util.List;

public interface AbilityUser {
    void useAbility(List<Hero> allHeroes, List<Enemy> allEnemies, Board board);
}
