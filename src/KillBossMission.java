import java.util.*;

public class KillBossMission implements Mission {
    @Override
    public String getDescription() {
        return "Kill the Boss";
    }

    @Override
    public boolean isCompleted(List<Hero> heroes, Boss boss, int turn) {
        return boss.getHealth() <= 0;
    }
}