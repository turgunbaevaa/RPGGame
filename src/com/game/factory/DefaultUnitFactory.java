package com.game.factory;
import com.game.board.Position;
import com.game.units.Enemy;
import com.game.units.Hero;
import com.game.units.heroes.Tank;
import com.game.units.heroes.Warrior;
import com.game.units.heroes.Archer;
import com.game.units.heroes.Healer;
import com.game.units.enemies.GoblinGrunt;
import com.game.units.enemies.OrcShaman;
import com.game.units.enemies.SkeletonArcher;

public class DefaultUnitFactory implements UnitFactory {

    public DefaultUnitFactory() {}

    @Override
    public Hero createHero(HeroType type, Position position) {
        return switch (type) {
            case TANK -> new Tank(position);
            case WARRIOR -> new Warrior(position);
            case ARCHER -> new Archer(position);
            case HEALER -> new Healer(position);
            default -> throw new IllegalArgumentException("Unknown hero type: " + type);
        };
    }

    @Override
    public Enemy createEnemy(EnemyType type, Position position, int wave) {
        Enemy enemy = switch (type) {
            case GOBLIN_GRUNT -> new GoblinGrunt(position);
            case ORC_SHAMAN -> new OrcShaman(position);
            case SKELETON_ARCHER -> new SkeletonArcher(position);
            default -> throw new IllegalArgumentException("Unknown enemy type: " + type);
        };

        enemy.levelUpStats(wave);
        return enemy;
    }
}