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
            System.out.println("Танк уже провоцирует. Способность недоступна.");
            return;
        }
        System.out.println("ТАНК активирует провокацию! Все враги будут атаковать его в следующем ходу.");
        self.setTaunting(true);
    }
}