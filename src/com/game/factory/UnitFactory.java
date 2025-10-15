package com.game.factory;
import com.game.board.Position;
import com.game.units.Enemy;
import com.game.units.Hero;

public interface UnitFactory {
    Hero createHero(HeroType type, Position position);
    Enemy createEnemy(EnemyType type, Position position, int wave);
}