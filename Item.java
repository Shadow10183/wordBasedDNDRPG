
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

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

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

    public void attack(Enemy enemy) {
        D20 dice = new D20();
        // int turnDamage = (int) ((damage * (0.45 + dice.roll() / 20)));
        enemy.takeDamage(damage);
        System.out.println(String.format("You attack %s with %s, it did %d damage.", enemy.getName(), name, damage));
    }
}

class Book extends Item {
    public Book(String name, String description) {
        super(name, description);
        itemtype = "book";
    }
}

class Gamemap extends Item {
    private String map = "" +
            "         N                      ########################################\r\n" +
            "       W   E                    #                                      #\r\n" +
            "         S                      #                                      #\r\n" +
            "                                #                                      #\r\n" +
            "                 ############## #                Throne                #\r\n" +
            "                 #            # #                 Room                 #\r\n" +
            "                 #            # #                                      #\r\n" +
            "                 #            # #                                      #\r\n" +
            "                 #            # #                                      #\r\n" +
            "                 #            # ###################==###################\r\n" +
            "                 #            #####################==####################\r\n" +
            "                 #                                                      #\r\n" +
            "                 #                                                      #\r\n" +
            "                 #                       Library                        #\r\n" +
            "                 #                                                      #\r\n" +
            "                 #                                                      #\r\n" +
            "                 ####  #####################  ###########################\r\n" +
            "                    #  #     ###############  ###############\r\n" +
            "                #####  ##### #                              # ############\r\n" +
            "                #          # #                              # #          #\r\n" +
            "                #          # #                              # #          #\r\n" +
            "                #          # #                              # #          #\r\n" +
            "                #  Dining  # #                              # #          #\r\n" +
            "                #   hall   # #          Courtyard               Armoury  #\r\n" +
            "                #          # #                              # #          #\r\n" +
            "                #          # #                              # #          #\r\n" +
            "                #          # #                              # #          #\r\n" +
            "                #          # #                              # #          #\r\n" +
            "                #####  ##### #                              # ############\r\n" +
            "                    #  #     ###############  ###############\r\n" +
            "                #####  ######### ###########  ###########\r\n" +
            "                #              # #                      #\r\n" +
            "               _#    Pantry             Black Hall      #\r\n" +
            "      ####    /                # #                      #\r\n" +
            "    #      #_/ /################ ###########--###########\r\n" +
            "   #  ????   _/                    #########--#########\r\n" +
            "   #  ????  #                      #                  #\r\n" +
            "    #      #    ################## #                  # ##################\r\n" +
            "      ####      #   West Pier             Spawn             East pier    #\r\n" +
            "                ################## #                  # ##################\r\n" +
            "                                   #                  # \r\n" +
            "                                   ####################\r\n";
    private String blurMap = map;
    private HashMap<String, Boolean> visitedFacts = new HashMap<String, Boolean>();

    public Gamemap(String name, String description) {
        super(name, description);
        itemtype = "map";
        visitedFacts.put("Spawn", true);
        visitedFacts.put("East pier", false);
        visitedFacts.put("West Pier", false);
        visitedFacts.put("Black Hall", false);
        visitedFacts.put("Courtyard", false);
        visitedFacts.put("Pantry", false);
        visitedFacts.put("Dining hall", false);
        visitedFacts.put("Library", false);
        visitedFacts.put("Armoury", false);
        visitedFacts.put("Throne Room", false);
        updatePointer("Spawn");
    }

    public String getMap() {
        return blurMap;
    }

    public void updatePointer(String roomname) {
        visitedFacts.put(roomname, true);
        Scanner words = new Scanner(roomname);
        String lastword = words.next();
        lastword = (words.hasNext()) ? words.next() : lastword;
        map = map.replace("<-", "  ");
        map = map.replace(lastword + "  ", lastword + "<-");
        words.close();
        updateBlurred();
    }

    public void updateBlurred() {
        blurMap = map;
        String word;
        for (Entry<String, Boolean> entry : visitedFacts.entrySet()) {
            if (entry.getValue() == false) {
                Scanner words = new Scanner(entry.getKey());
                word = words.next();
                blurMap = blurMap.replace(word, new String(new char[word.length()]).replace("\0", "?"));
                if (words.hasNext()) {
                    word = words.next();
                    blurMap = blurMap.replace(word, new String(new char[word.length()]).replace("\0", "?"));
                }
                words.close();
            }
        }
    }

    public void unlock(String roomname) {
        map = map.replace((roomname == "Black Hall") ? "--" : "==", "  ");
        updateBlurred();
    }
}
