public class Archer extends Hero {
    public Archer(Position position) {
        super("Archer", 90, 35, 3, 2, position, 1, new ArcherMultiShotAbility());
    }

    @Override
    public String getDisplaySymbol() {
        return "A";
    }
}