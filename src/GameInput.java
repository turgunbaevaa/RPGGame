public interface GameInput {
    int getIntInput(String prompt);
    String getStringInput(String prompt);
    Position getPositionInput(String prompt) throws GameException;
}