public abstract class Character {
    protected String name;
    protected int health;
    protected int damage;

    public Character(String name, int health, int damage) {
        this.name = name;
        this.health = health;
        this.damage = damage;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void receiveDamage(int dmg) {
        health -= dmg;
        if (health < 0) health = 0;
    }

    public String getName() { return name; }
    public int getHealth() { return health; }
    public int getDamage() { return damage; }
}