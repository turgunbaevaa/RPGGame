package com.game.units.heroes;
import com.game.board.Position;
import com.game.units.Hero;
import com.game.abilities.TankTauntAbility;

public class Tank extends Hero {
    public Tank(Position position) {
        super("Танк", 250, 15, 1, 2, position, 1, new TankTauntAbility());
    }

    @Override
    public String getDisplaySymbol() {
        return "T";
    }
}