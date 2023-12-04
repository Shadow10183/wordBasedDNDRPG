
/**
 * Class Room - a room in an adventure game.
 *
 * This class is part of the "Castle of Shmorgenyorg" application.
 * "Castle of Shmorgenyorg" is a very simple, text based adventure game.
 *
 * A "Room" represents one location in the scenery of the game. It is
 * connected to other rooms via exits. For each existing exit, the room
 * stores a reference to the neighboring room.
 * 
 * @author Aidan Leung Yau Hei (k23093432), Michael Kölling and David J. Barnes
 * @version 2023.11.30
 */

import java.util.Set;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class Room {
    public static final List<String> validDirections = Arrays.asList("east", "southeast", "south", "southwest", "west",
            "northwest", "north", "northeast");
    private String name;
    private String description;
    private boolean locked = false;
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<>();
    private HashMap<String, Room> exits; // stores exits of this room.

    /**
     * Create a room described "description" with name "name". Initially, it has
     * no exits. "description" is something like "a kitchen" or
     * "an open court yard".
     * 
     * @param name        The room's name.
     * @param description The room's description.
     */
    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        exits = new HashMap<>();
    }

    /**
     * Alternative constructor to specify if the room is locked.
     * By default, the room is not locked.
     * 
     * @param name        The room's name.
     * @param description The room's description.
     * @param locked      Whether the room is locked.
     */
    public Room(String name, String description, boolean locked) {
        this.name = name;
        this.description = description;
        this.locked = locked;
        exits = new HashMap<>();
    }

    /**
     * Define an exit from this room.
     * 
     * @param direction The direction of the exit.
     * @param neighbor  The room to which the exit leads.
     */
    public void setExit(String direction, Room neighbor) {
        exits.put(direction, neighbor);
    }

    /**
     * @return The name of the room
     *         (the one that was defined in the constructor).
     */
    public String getName() {
        return name;
    }

    /**
     * @return The short description of the room
     *         (the one that was defined in the constructor).
     */
    public String getShortDescription() {
        return description;
    }

    /**
     * Return a description of the room in the form:
     * You are in the kitchen.
     * Exits: north west
     * 
     * @return A long description of this room
     */
    public String getLongDescription() {
        return description + "\n" + getExitString();
    }

    /**
     * Return whether or not this room is locked
     * 
     * @return the locked state of this room
     */
    public boolean isLocked() {
        return locked;
    }

    // unlocks the room
    public void unlock() {
        locked = false;
    }

    /**
     * Return a string describing the room's exits, for example
     * "Exits: north west".
     * 
     * @return Details of the room's exits.
     */
    private String getExitString() {
        String returnString = "Exits:";
        Set<String> keys = exits.keySet();
        for (String exit : keys) {
            returnString += " " + exit;
        }
        return returnString;
    }

    /**
     * @return The a set of all exits of the room
     */
    public ArrayList<Room> getAllExits() {
        ArrayList<Room> result = new ArrayList<Room>(exits.values());
        return result;
    }

    /**
     * @return A random room that is connected to this room.
     *         If the room has no exits, returns null.
     */
    public Room getRandomExit() {
        int randomIndex = (int) (Math.random() * exits.size());
        int index = 0;
        for (Room exit : exits.values()) {
            if (randomIndex == index) {
                return exit;
            }
            index += 1;
        }
        return null;
    }

    /**
     * Return the room that is reached if we go from this room in direction
     * "direction". If there is no room in that direction, return null.
     * 
     * @param direction The exit's direction.
     * @return The room in the given direction.
     */
    public Room getExit(String direction) {
        return exits.get(direction);
    }

    /**
     * Adds an enemy to the room and adjusts the arraylist accordingly.
     * Used when an enemy is set to be in the room
     * or moves into the room by themselves.
     * 
     * @param enemy
     */
    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    /**
     * Removes an enemy from the room and adjusts the arraylist accordingly.
     * Used when an enemy dies or moves to another room.
     * 
     * @param enemy
     */
    public void removeEnemy(Enemy enemy) {
        enemies.remove(enemy);
    }

    /**
     * @return a list of all enemies in the room.
     */
    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    /**
     * @return Whether there are enemies in the room
     */
    public boolean hasEnemy() {
        if (enemies.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * If there are enemies in the room, prints a message notifying the user.
     */
    public void showEnemy() {
        if (hasEnemy()) {
            for (Enemy enemy : enemies) {
                System.out.println(String.format("A(n) %s sizes you up.", enemy.getName()));
            }
        }
    }

    /**
     * Adds an item to the room and adjusts the arraylist accordingly.
     * Used when an item is set or dropped into the room.
     * 
     * @param item
     */
    public void addItem(Item item) {
        items.add(item);
    }

    /**
     * Removes an item from the room and adjusts the arraylist accordingly.
     * Used when the user picks it up from the room.
     * 
     * @param item
     */
    public void removeItem(Item item) {
        items.remove(item);
    }

    /**
     * If there are items in the room, a message will be printed to show them all.
     * Otherwise the message will notify user that there are no items.
     */
    public void showItems() {
        System.out.println("You search the room and find");
        String result = "";
        for (Item item : items) {
            if (item.getItemtype() != "healthPotion") {
                result += item.getName() + " ";
            } else {
                result += item.getName() + String.format("x%d ", ((HealthPotion) item).getWeight());
            }
        }
        if (result == "") {
            System.out.println("nothing. There is nothing but the void here...");
        } else {
            System.out.println(result);
        }
    }

    /**
     * @return a list of all items in the room.
     */
    public ArrayList<Item> getItems() {
        return items;
    }
}

class Teleporter extends Room {
    // A list of all rooms that the player can be teleported to.
    private ArrayList<Room> roomList = new ArrayList<>();

    /**
     * Initialises the teleporter as a room and sets its name and description.
     * 
     * @param description
     */
    public Teleporter(String description) {
        super("Teleporter", description);
    }

    /**
     * Adds a room that the player can be teleported to.
     * 
     * @param room
     */
    public void addRoom(Room room) {
        roomList.add(room);
    }

    /**
     * @return a room that the player will be teleported to that is randomly chosen.
     */
    public Room getRandomExit() {
        return roomList.get((int) (Math.random() * roomList.size()));
    }
}