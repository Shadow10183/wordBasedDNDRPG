/**
 * Class Item - an item in an adventure game.
 *
 * This class is part of the "World of Zuul" application.
 * "World of Zuul" is a very simple, text based adventure game.
 *
 * An "Item" is an object that is either placed in a room, carried by an enemy
 * or the player.
 * Items can be used to interact with other elements in the game.
 * The "Item" class has children classes "Key", "Weapon", "Book" and
 * "HealPotion".
 * 
 * A "Key" is used to unlock certain locked doors.
 * 
 * A "Weapon" is used to perform a melee attack on an enemy.
 * 
 * A "Book" is used to unlock new spells.
 * 
 * A "HealPotion" is used to restore a set amount of hp to the player.
 * 
 * @author Aidan Leung Yau Hei
 * @version 2016.02.29
 */

public abstract class Item {
    protected String name;
    protected String itemtype;
    protected String description;

    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getItemtype() {
        return itemtype;
    }

    public Room getUnlock() {
        return null;
    }

    public String getDirection() {
        return null;
    }
}

class Key extends Item {
    private Room roomUnlock;
    private String direction;

    public Key(String name, String description, Room room, String leadingdirection) {
        super(name, description);
        itemtype = "key";
        roomUnlock = room;
        direction = leadingdirection;
    }

    public Room getUnlock() {
        return roomUnlock;
    }

    public String getDirection() {
        return direction;
    }
}

class Weapon extends Item {
    private int damage;

    public Weapon(String name, String description, int damage) {
        super(name, description);
        itemtype = "weapon";
        this.damage = damage;
    }
}

class Book extends Item {
    public Book(String name, String description) {
        super(name, description);
        itemtype = "book";
    }
}