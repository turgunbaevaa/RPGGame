import java.util.Random;

public interface UnitFactory {
    Hero createHero(HeroType type, Position position); // Changed Hero.HeroType to HeroType
    Enemy createEnemy(String enemyTypeName, Position position, int wave);
}