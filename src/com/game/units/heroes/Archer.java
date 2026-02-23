package com.game.units.heroes;
import com.game.board.Board;
import com.game.board.Position;
import com.game.abilities.AbilityUser;
import com.game.units.Enemy;
import com.game.units.Hero;
import com.game.abilities.ArcherMultiShotAbility;
import com.game.units.HeroAbility;

import java.util.List;

public class Archer extends Hero implements AbilityUser {
    private final HeroAbility ability;

    public Archer(Position position) {
        super("Archer", 90, 35, 3, 2, position, 1);
        ability = new ArcherMultiShotAbility();
    }

    @Override
    public String getDisplaySymbol() {
        return "A";
    }

    @Override
    public void useAbility(List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        ability.use(this, allHeroes, allEnemies, board);
    }
}