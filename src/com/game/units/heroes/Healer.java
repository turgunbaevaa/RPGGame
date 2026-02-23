package com.game.units.heroes;
import com.game.board.Board;
import com.game.board.Position;
import com.game.abilities.AbilityUser;
import com.game.units.Enemy;
import com.game.units.Hero;
import com.game.abilities.HealerHealAbility;
import com.game.units.HeroAbility;

import java.util.List;

public class Healer extends Hero implements AbilityUser {
    private final HeroAbility ability;

    public Healer(Position position) {
        super("Healer", 80, 0, 2, 2, position, 1);
        ability = new HealerHealAbility();
    }

    @Override
    public String getDisplaySymbol() {
        return "H";
    }


    @Override
    public void useAbility(List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        ability.use(this, allHeroes, allEnemies, board);
    }
}