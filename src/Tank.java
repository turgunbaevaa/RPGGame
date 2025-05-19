public class Tank extends Hero{
    double parry;

    Tank(double parry, String name, int health, int damage){
        super(name, health, damage);
        this.parry = parry;
    }

    public int reflectDamage(int amount){
        int reflectDamage = (int)(amount * parry);
        return reflectDamage;
    }
}
