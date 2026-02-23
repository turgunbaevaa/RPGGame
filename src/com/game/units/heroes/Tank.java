package com.game.units.heroes;
import com.game.abilities.AbilityUser;
import com.game.board.Board;
import com.game.board.Position;
import com.game.units.*;
import com.game.abilities.TankTauntAbility;

import java.util.List;

public class Tank extends Hero implements Tauntable, AbilityUser {

    private final HeroAbility ability;
    private boolean isTaunting = false;

    public Tank(Position position) {
        super("Tank", 100, 15, 1, 2, position, 1);
        ability = new TankTauntAbility();
    }

    @Override
    public String getDisplaySymbol() {
        return "T";
    }

    @Override
    public boolean isTaunting() {
        return isTaunting;
    }

    public final void setTaunting(boolean taunting) {
        isTaunting = taunting;
        if (taunting) {
            System.out.println(this.getName() + " is now provoking!");
        } else {
            System.out.println(this.getName() + " no longer provokes.");
        }
    }

    @Override
    public void useAbility(List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        ability.use(this, allHeroes, allEnemies, board);
    }
}