import java.util.*;
import java.util.stream.*;

public class Hero extends Unit {

    public enum HeroType { WARRIOR, ARCHER, TANK, HEALER }

    private HeroType type;
    private boolean isTaunting = false;
    private int baseHealth;
    private int baseDamage;
    private int baseSpeed;
    private int baseRange;

    public Hero(HeroType type, Position position) {
        super(type.name(), 0, 0, 0, 0, position, 1);
        this.type = type;
        switch (type) {
            case TANK:
                this.name = "Танк";
                this.health = 250;
                this.damage = 15;
                this.range = 1;
                this.speed = 2;
                break;
            case ARCHER:
                this.name = "Лучник";
                this.health = 90;
                this.damage = 35;
                this.range = 3;
                this.speed = 2;
                break;
            case HEALER:
                this.name = "Целитель";
                this.health = 80;
                this.damage = 5;
                this.range = 2;
                this.speed = 2;
                break;
            case WARRIOR:
            default:
                this.name = "Воин";
                this.health = 150;
                this.damage = 25;
                this.range = 1;
                this.speed = 1;
                break;
        }
        this.baseHealth = this.health;
        this.baseDamage = this.damage;
        this.baseSpeed = this.speed;
        this.baseRange = this.range;
    }

    @Override
    public void move(Position targetPosition, Board board) {
        board.updatePosition(this, targetPosition);
        System.out.println(this.name + " переместился на " + targetPosition.toString());
    }

    @Override
    public void levelUp() {
        this.level++;
        this.baseHealth += 30;
        this.baseDamage += 7;
        this.baseSpeed += 1;
        this.health = this.getMaxHealth();
        System.out.println(this.name + " повысил уровень до " + level + "!");
    }

    @Override
    public int getMaxHealth() {
        return baseHealth;
    }

    public HeroType getType() {
        return type;
    }

    public void useAbility(List<Hero> allHeroes, List<Enemy> allEnemies, Board board) {
        switch (type) {
            case TANK -> {
                if (isTaunting) {
                    System.out.println("Танк уже провоцирует. Способность недоступна.");
                    return;
                }
                System.out.println("ТАНК активирует провокацию! Все враги будут атаковать его в следующем ходу.");
                this.setTaunting(true);
            }
            case WARRIOR -> {
                System.out.println(this.name + " использует 'Удар с отбрасыванием'");
                Enemy closest = allEnemies.stream()
                        .filter(Enemy::isAlive)
                        .min(Comparator.comparingInt(e -> e.getPosition().distanceTo(this.position)))
                        .orElse(null);
                if (closest != null && board.isInRange(this, closest)) {
                    int originalDamage = this.damage;
                    this.damage *= 1.5;
                    attack(closest);
                    this.damage = originalDamage;

                    Position oldPos = closest.getPosition();
                    int pushX = oldPos.getX();
                    int pushY = oldPos.getY();

                    int deltaX = oldPos.getX() - this.position.getX();
                    int deltaY = oldPos.getY() - this.position.getY();

                    int pushDistance = 2;

                    for (int i = 0; i < pushDistance; i++) {
                        int nextPushX = pushX + (deltaX != 0 ? (deltaX > 0 ? 1 : -1) : 0);
                        int nextPushY = pushY + (deltaY != 0 ? (deltaY > 0 ? 1 : -1) : 0);

                        Position potentialPushBack = new Position(nextPushX, nextPushY);
                        if (board.isValidPosition(potentialPushBack) && board.isEmpty(potentialPushBack)) {
                            pushX = nextPushX;
                            pushY = nextPushY;
                        } else {
                            break;
                        }
                    }

                    Position finalPushBack = new Position(pushX, pushY);

                    if (!finalPushBack.equals(oldPos)) {
                        board.updatePosition(closest, finalPushBack);
                        System.out.println(closest.getName() + " отброшен на " + finalPushBack.toString() + "!");
                    } else {
                        System.out.println(closest.getName() + " не удалось отбросить.");
                    }
                } else {
                    System.out.println("Нет цели для 'Удара с отбрасыванием' или цель вне досягаемости.");
                }
            }
            case ARCHER -> {
                System.out.println(this.name + " использует 'Множественный выстрел'");
                boolean attackedSomeone = false;
                List<Enemy> targets = allEnemies.stream()
                        .filter(Enemy::isAlive)
                        .filter(e -> this.position.distanceTo(e.getPosition()) <= this.range)
                        .collect(Collectors.toList());

                if (targets.isEmpty()) {
                    System.out.println("Нет врагов в зоне досягаемости для 'Множественного выстрела'.");
                    return;
                }

                int originalDamage = this.damage;
                this.damage /= 2;

                for (Enemy e : targets) {
                    attack(e);
                    attackedSomeone = true;
                }
                this.damage = originalDamage;

                if (!attackedSomeone) {
                    System.out.println("Ошибка при использовании способности: нет врагов в зоне досягаемости.");
                }
            }
            case HEALER -> {
                System.out.println(this.name + " использует 'Исцеление союзника'");
                Hero target = allHeroes.stream()
                        .filter(h -> h.isAlive() && h.getHealth() < h.getMaxHealth())
                        .min(Comparator.comparingInt(h -> (h.getMaxHealth() - h.getHealth())))
                        .orElse(null);
                if (target == null) {
                    System.out.println("Нет раненых союзников поблизости.");
                } else if (this.position.distanceTo(target.getPosition()) > this.range) {
                    System.out.println("Цель для лечения вне досягаемости.");
                } else {
                    int healAmount = 50;
                    target.increaseHealth(healAmount);
                    System.out.println("Исцелен " + target.getName() + " на позиции " + target.getPosition().toString() + ". Здоровье: " + target.getHealth() + "/" + target.getMaxHealth());
                }
            }
        }
    }

    public boolean isTaunting() {
        return isTaunting;
    }

    public void setTaunting(boolean taunting) {
        this.isTaunting = taunting;
        if (taunting) {
            System.out.println(this.name + " теперь провоцирует!");
        } else {
            System.out.println(this.name + " больше не провоцирует.");
        }
    }

    public void upgradeHealthStat(int amount) {
        this.baseHealth += amount;
        this.health = this.getMaxHealth();
    }

    public void upgradeDamageStat(int amount) {
        this.baseDamage += amount;
        this.damage += amount;
    }

    public void upgradeSpeedStat(int amount) {
        this.baseSpeed += amount;
        this.speed += amount;
    }

    public void upgradeRangeStat(int amount) {
        this.baseRange += amount;
        this.range += amount;
    }
}