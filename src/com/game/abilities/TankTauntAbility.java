package com.game.abilities;
import com.game.board.Board;
import com.game.units.Enemy;
import com.game.units.Hero;
import com.game.units.HeroAbility;

import java.util.List;

public class TankTauntAbility implements HeroAbility {
    @Override
    public void use(Hero self, List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        // Assuming com.game.units.Hero has isTaunting and setTaunting methods accessible or passed as 'self'
        if (self.isTaunting()) {
            System.out.println("The tank is already taunting. Ability is unavailable.");
            return;
        }
        System.out.println("TANK activates Provocation! All enemies will attack it on the next turn.");
        self.setTaunting(true);
    }
}