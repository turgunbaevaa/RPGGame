package com.game.abilities;

import com.game.board.Board;
import com.game.units.Enemy;
import com.game.units.Hero;
import com.game.units.HeroAbility;
import com.game.units.Tauntable;

import java.util.List;

public class TankTauntAbility implements HeroAbility {
    @Override
    public void use(Hero self, List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        if (!(self instanceof Tauntable tauntable)) {
            System.out.println("Hero is not tauntalbe");
            return;
        }

        // Assuming com.game.units.Hero has isTaunting and setTaunting methods accessible or passed as 'self'
        if (tauntable.isTaunting()) {
            System.out.println("The tank is already taunting. Ability is unavailable.");
            return;
        }
        System.out.println("TANK activates Provocation! All enemies will attack it on the next turn.");
        tauntable.setTaunting(true);
    }
}