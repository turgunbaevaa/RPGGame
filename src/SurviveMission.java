import java.util.*;

public class SurviveMission implements Mission {
    private int requiredTurns;

    public SurviveMission(int requiredTurns) {
        this.requiredTurns = requiredTurns;
    }

    @Override
    public String getDescription() {
        return "Survive " + requiredTurns + " turns";
    }

    @Override
    public boolean isCompleted(List<Hero> heroes, Boss boss, int turn) {
        return turn >= requiredTurns;
    }
}