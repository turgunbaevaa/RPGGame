public class Board {
    private final int SIZE = 10;
    private Unit[][] grid;

    public Board() {
        grid = new Unit[SIZE][SIZE];
    }

    public boolean isValidPosition(Position pos) {
        return pos.getX() >= 0 && pos.getX() < SIZE && pos.getY() >= 0 && pos.getY() < SIZE;
    }

    public boolean isEmpty(Position pos) {
        return isValidPosition(pos) && grid[pos.getX()][pos.getY()] == null;
    }

    public void placeUnit(Unit unit) {
        Position pos = unit.getPosition();
        if (isEmpty(pos)) {
            grid[pos.getX()][pos.getY()] = unit;
        } else {
            System.out.println("Ошибка: Позиция " + pos.toString() + " уже занята. Не удалось разместить юнит.");
        }
    }

    public void updatePosition(Unit unit, Position newPos) {
        Position oldPos = unit.getPosition();
        if (!isValidPosition(newPos)) {
            return;
        }
        if (grid[newPos.getX()][newPos.getY()] != null && grid[newPos.getX()][newPos.getY()] != unit) {
            if (unit instanceof Hero || grid[newPos.getX()][newPos.getY()] instanceof Hero) {
                System.out.println("Ошибка: Позиция " + newPos.toString() + " уже занята другим юнитом.");
                return;
            }
        }
        grid[oldPos.getX()][oldPos.getY()] = null;
        grid[newPos.getX()][newPos.getY()] = unit;
        unit.setPosition(newPos);
    }

    public void removeUnit(Unit unit) {
        Position pos = unit.getPosition();
        if (isValidPosition(pos) && grid[pos.getX()][pos.getY()] == unit) {
            grid[pos.getX()][pos.getY()] = null;
        }
    }

    public boolean isInRange(Unit attacker, Unit target) {
        return attacker.getPosition().distanceTo(target.getPosition()) <= attacker.getRange();
    }

    public void printBoard() {
        System.out.println("   0 1 2 3 4 5 6 7 8 9");
        System.out.println("  ---------------------");
        for (int i = 0; i < SIZE; i++) {
            System.out.print(i + " |");
            for (int j = 0; j < SIZE; j++) {
                Unit unit = grid[i][j];
                if (unit instanceof Hero hero) {
                    switch (hero.getType()) {
                        case TANK:
                            System.out.print("T ");
                            break;
                        case WARRIOR:
                            System.out.print("W ");
                            break;
                        case ARCHER:
                            System.out.print("A ");
                            break;
                        case HEALER:
                            System.out.print("L ");
                            break;
                    }
                } else if (unit instanceof GoblinGrunt) {
                    System.out.print("G ");
                } else if (unit instanceof SkeletonArcher) {
                    System.out.print("S ");
                } else if (unit instanceof OrcShaman) {
                    System.out.print("M ");
                }
                else {
                    System.out.print(". ");
                }
            }
            System.out.println("|");
        }
        System.out.println("  ---------------------");
    }

    public Unit getUnitAt(Position pos) {
        if (isValidPosition(pos)) {
            return grid[pos.getX()][pos.getY()];
        }
        return null;
    }
}