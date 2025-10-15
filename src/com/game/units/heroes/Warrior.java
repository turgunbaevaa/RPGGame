package com.game.units.heroes;
import com.game.board.Position;
import com.game.units.Hero;
import com.game.abilities.WarriorKnockbackAbility;

public class Warrior extends Hero {
    public Warrior(Position position) {
        super("Воин", 150, 25, 1, 1, position, 1, new WarriorKnockbackAbility());
    }

    @Override
    public String getDisplaySymbol() {
        return "W";
    }
}