import java.util.HashMap;

public class Enemy {
    private String name;
    private int level;
    private int health;
    private int damage;
    private boolean hasDrop = false;
    private boolean moving;
    private Room currentRoom;
    private HashMap<Item, Double> drops = new HashMap<>();

    public Enemy(String name, int level, boolean moving) {
        this.name = name;
        this.level = level;
        this.moving = moving;
        addDrop(new HealthPotion(2), 0.5);
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

    public void attack(Player player) {
        player.takeDamage(damage);
        System.out.println(String.format("%s does %d damage to you.", name, damage));
    }

    public Room setRoom(Room room) {
        currentRoom = room;
        currentRoom.addEnemy(this);
        return currentRoom;
    }

    public void move() {
        if (!moving) {
            return;
        }
        Room nextRoom = currentRoom.getRandomExit();
        while ((nextRoom.getName().equals("Throne Room") || nextRoom.getName().equals("Teleporter"))) {
            nextRoom = currentRoom.getRandomExit();
        }
        currentRoom.removeEnemy(this);
        currentRoom = nextRoom;
        currentRoom.addEnemy(this);
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

    public boolean getMoving() {
        return moving;
    }

    public boolean hasDrop() {
        return hasDrop;
    }
}
