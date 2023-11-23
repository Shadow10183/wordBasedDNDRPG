import java.util.HashMap;

public class Enemy {
    private String name;
    private int level;
    private int health;
    private int damage;
    private boolean hasDrop = false;
    private HashMap<Item, Double> drops = new HashMap<>();

    public Enemy(String name, int level) {
        this.name = name;
        this.level = level;
        switch (level) {
            case 1:
                health = 5;
                damage = 2;
                break;
            case 2:
                health = 12;
                damage = 3;
                break;
            case 3:
                health = 20;
                damage = 5;
                break;
            case 4:
                health = 40;
                damage = 8;
                break;
        }
    }

    public void addDrop(Item item, double chance) {
        drops.put(item, chance);
    }

    public HashMap<Item, Double> getDrops() {
        return drops;
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public boolean hasDrop() {
        return hasDrop;
    }
}
