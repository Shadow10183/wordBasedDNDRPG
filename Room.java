
/**
 * Class Room - a room in an adventure game.
 *
 * This class is part of the "World of Zuul" application.
 * "World of Zuul" is a very simple, text based adventure game.
 *
 * A "Room" represents one location in the scenery of the game. It is
 * connected to other rooms via exits. For each existing exit, the room
 * stores a reference to the neighboring room.
 * 
 * @author Aidan Leung Yau Hei, Michael Kölling and David J. Barnes
 * @version 2016.02.29
 */

import java.util.Set;
import java.util.HashMap;
import java.util.Map.Entry;

public class Room {
    private String name;
    private String description;
    private boolean locked = false;
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
        return "You are " + description + ".\n" + getExitString();
    }

    public String getMiddleDescription() {
        return "You are " + description + ".";
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

    public Set<Entry<String, Room>> getAllExits() {
        return exits.entrySet();
    }

    public Room getRandomExit() {
        int randomIndex = (int) (Math.random() * exits.size());
        int index = 0;
        for (Entry<String, Room> entry : exits.entrySet()) {
            if (randomIndex == index) {
                return entry.getValue();
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
}