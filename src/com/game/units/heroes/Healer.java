package com.game.units.heroes;
import com.game.board.Position;
import com.game.units.Hero;
import com.game.abilities.HealerHealAbility;

public class Healer extends Hero {
    public Healer(Position position) {
        super("Целитель", 80, 0, 2, 2, position, 1, new HealerHealAbility());
    }

    @Override
    public String getDisplaySymbol() {
        return "H";
    }
}