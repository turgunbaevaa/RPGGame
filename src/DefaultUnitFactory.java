import java.util.Random;

public class DefaultUnitFactory implements UnitFactory {
    private Random random;

    public DefaultUnitFactory(Random random) {
        this.random = random;
    }

    @Override
    public Hero createHero(HeroType type, Position position) { // Changed Hero.HeroType to HeroType
        switch (type) {
            case TANK:
                return new Tank(position);
            case WARRIOR:
                return new Warrior(position);
            case ARCHER:
                return new Archer(position);
            case HEALER:
                return new Healer(position);
            default:
                throw new IllegalArgumentException("Unknown hero type: " + type);
        }
    }

    @Override
    public Enemy createEnemy(String enemyTypeName, Position position, int wave) {
        Enemy enemy;
        // In a more complex game, you might use an enum for enemyTypeName
        // For now, we'll keep the random logic as in GameController, but encapsulated here.
        double rand = random.nextDouble();
        if (rand < 0.4) {
            enemy = new GoblinGrunt(position);
        } else if (rand < 0.75) {
            enemy = new SkeletonArcher(position);
        } else {
            enemy = new OrcShaman(position);
        }

        enemy.levelUpStats(wave); // Apply wave scaling here
        return enemy;
    }
}