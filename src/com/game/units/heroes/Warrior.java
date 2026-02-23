package com.game.units.heroes;
import com.game.abilities.AbilityUser;
import com.game.board.Board;
import com.game.board.Position;
import com.game.units.*;
import com.game.abilities.WarriorKnockbackAbility;

import java.util.List;

public class Warrior extends Hero implements AbilityUser {
    private final HeroAbility ability;

    public Warrior(Position position) {
        super("Warrior", 70, 25, 1, 1, position, 1);
        ability = new WarriorKnockbackAbility();
    }

    @Override
    public String getDisplaySymbol() {
        return "W";
    }

    @Override
    public void useAbility(List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        ability.use(this, allHeroes, allEnemies, board);
    }
}