import java.util.List;

public class TankTauntAbility implements HeroAbility {
    @Override
    public void use(Hero self, List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        // Assuming Hero has isTaunting and setTaunting methods accessible or passed as 'self'
        if (self.isTaunting()) {
            System.out.println("Танк уже провоцирует. Способность недоступна.");
            return;
        }
        System.out.println("ТАНК активирует провокацию! Все враги будут атаковать его в следующем ходу.");
        // We need a way for the Tank to set its taunting status.
        // Assuming setTaunting is a public method on Hero.
        self.setTaunting(true);
    }
}