//parent class
public abstract class Character {
    protected String name;
    protected Integer health;
    protected Integer damage;

    public Character(String name, Integer health, Integer damage) {
        this.name = name;
        this.health = health;
        this.damage = damage;
    }

    public abstract void attack(Character enemy);
    public abstract void receiveDamage(int damage);

    public boolean isAlive() {
        return health > 0;
    }

    public String getName() {
        return name;
    }

    public Integer getHealth() {
        return health;
    }

    public Integer getDamage() {
        return damage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHealth(Integer health) {
        this.health = health;
    }

    public void setDamage(Integer damage) {
        this.damage = damage;
    }
}
