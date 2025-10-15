package com.game.units.heroes;
import com.game.board.Position;
import com.game.units.Hero;
import com.game.abilities.ArcherMultiShotAbility;

public class Archer extends Hero {
    public Archer(Position position) {
        super("com.game.units.heroes.Archer", 90, 35, 3, 2, position, 1, new ArcherMultiShotAbility());
    }

    @Override
    public String getDisplaySymbol() {
        return "A";
    }
}