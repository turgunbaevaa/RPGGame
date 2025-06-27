public class Tank extends Hero {
    public Tank(Position position) {
        // name, health, damage, range, speed, position, level, ability
        super("Танк", 250, 15, 1, 2, position, 1, new TankTauntAbility());
    }

    @Override
    public String getDisplaySymbol() {
        return "T";
    }
}