import java.util.List;

public interface Mission {
    String getDescription();
    boolean isCompleted(List<Hero> heroes, Boss boss, int turn);
}