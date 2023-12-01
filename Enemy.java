
/**
 * This class is part of the "Castle of Shmorgenyorg" application.
 * "Castle of Shmorgenyorg" is a very simple, text based adventure game.
 *
 * This class represents an enemy and stores information about itself
 * e.g. Name, Level, Health, Damage, if it has drops, if it can move, its current room
 * 
 * @author Aidan Leung Yau Hei (k23093432)
 * @version 2023.11.30
 */

import java.util.HashMap;

public class Enemy {
    private String name; // holds the name of the enemy
    private int level; // holds the level of the enemy
    private int health; // holds the health of the enemy
    private int damage; // holds the damage of the enemy
    private boolean hasDrop = false; // holds whether the enemy has any drops
    private boolean moving; // holds whether the enemy should move
    private Room currentRoom; // holds the current room of the enemy
    // holds a hashmap of drops and their drop chance
    private HashMap<Item, Double> drops = new HashMap<>();

    /**
     * Create a new enemy with the given parameters.
     * By default, the enemy has 0.5 chance of dropping
     * 2 health potions.
     * The health and damage are determined by the given level.
     * 
     * @param name   the name of the enemy
     * @param level  the level of the enemy
     * @param moving whether the enemy should move
     */
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

    /**
     * Adds an item drop and its dropchance to the "drops" hashmap.
     * 
     * @param item
     * @param chance
     */
    public void addDrop(Item item, double chance) {
        drops.put(item, chance);
    }

    /**
     * @return the "drops" hashmap
     */
    public HashMap<Item, Double> getDrops() {
        return drops;
    }

    /**
     * Deals damage to the player when it is called.
     * 
     * @param player reference to the current player
     */
    public void attack(Player player) {
        player.takeDamage(damage);
        System.out.println(String.format("%s does %d damage to you.", name, damage));
    }

    /**
     * Places the enemy in the given room
     * 
     * @param room the room that the enemy should be placed in
     */
    public void setRoom(Room room) {
        currentRoom = room;
        currentRoom.addEnemy(this);
    }

    /**
     * If the enemy is allowed to move, it will go to a random room that is
     * connected to the current room.
     * However, the Throne Room and teleporter are forbidden for enemies to move to.
     * 
     * Otherwise the enemy stays in the current room.
     */
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

    /**
     * Decreases the health based on given damage
     * 
     * @param damage integer value of damage
     */
    public void takeDamage(int damage) {
        health -= damage;
    }

    /**
     * @return the current health of this enemy.
     */
    public int getHealth() {
        return health;
    }

    /**
     * @return the level of this enemy
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return the name of this enemy
     */
    public String getName() {
        return name;
    }

    /**
     * @return whether the enemy should move
     */
    public boolean getMoving() {
        return moving;
    }

    /**
     * @return whether the enemy has any drops
     */
    public boolean hasDrop() {
        return hasDrop;
    }
}