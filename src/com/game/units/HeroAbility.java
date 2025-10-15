package com.game.units;
import com.game.board.Board;

import java.util.List;

public interface HeroAbility {
    void use(Hero self, List<Hero> allHeroes, List<Enemy> allEnemies, Board board);
}