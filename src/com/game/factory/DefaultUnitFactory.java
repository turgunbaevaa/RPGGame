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
            case HeroType.TANK -> new Tank(position);
            case HeroType.WARRIOR -> new Warrior(position);
            case HeroType.ARCHER -> new Archer(position);
            case HeroType.HEALER -> new Healer(position);
            default -> throw new IllegalArgumentException("Unknown hero type: " + type);
        };
    }

    @Override
    public Enemy createEnemy(EnemyType type, Position position, int wave) {
        Enemy enemy = switch (type) {
            case EnemyType.GOBLIN_GRUNT -> new GoblinGrunt(position);
            case EnemyType.ORC_SHAMAN -> new OrcShaman(position);
            case EnemyType.SKELETON_ARCHER -> new SkeletonArcher(position);
            default -> throw new IllegalArgumentException("Unknown enemy type: " + type);
        };

        enemy.levelUpStats(wave);
        return enemy;
    }
}