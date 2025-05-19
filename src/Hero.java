public class Hero {
    public String name;
    public int health;
    public int damage;

    Hero(String name, int health, int damage) {
        this.name = name;
        this.health = health;
        this.damage = damage;
    }

    public int getHealth() {
        return health;
    }

    public void receiveDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            this.health = 0;
        }
    }

    public void receiveHealing(int healing) {
        this.health += healing;
        System.out.println(this.name + " has been treated for " + healing + " amount of heals. " + "Current health: " + this.health);
    }
}
