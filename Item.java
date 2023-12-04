
/**
 * Class Item - an item in an adventure game.
 *
 * This class is part of the "Castle of Shmorgenyorg" application.
 * "Castle of Shmorgenyorg" is a very simple, text based adventure game.
 *
 * An "Item" is an object that is either placed in a room, carried by an enemy
 * or the player.
 * Items can be used to interact with other elements in the game.
 * The "Item" class has children classes "Key", "Weapon", "Book", "Gamemap",
 * "HealPotion", "UpgradeItem", "UpgradePoint" and "Backpack".
 * 
 * @author Aidan Leung Yau Hei (k23093432)
 * @version 2023.11.30
 */

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

public abstract class Item {
    protected String name; // name of the item
    protected String itemtype; // type of the item
    protected String description; // description of the item
    protected int weight; // weight of the item
    protected boolean movable = true; // whether it can be moved

    /**
     * Creates a new Item and initializes the properties
     * 
     * @param name
     * @param description
     * @param weight
     */
    public Item(String name, String description, int weight) {
        this.name = name;
        this.description = description;
        this.weight = weight;
    }

    /**
     * By default the item cannot be used.
     */
    public void use() {
        System.out.println("You can't use this here.");
    }

    /**
     * @return the name of the item
     */
    public String getName() {
        return name;
    }

    /**
     * @return a concatenated string of the description and weight of the item
     */
    public String getDescription() {
        return description + " Weight: " + weight;
    }

    /**
     * @return the type of the item
     */
    public String getItemtype() {
        return itemtype;
    }

    /**
     * @return the weight of the item as integer
     */
    public int getWeight() {
        return weight;
    }

    /**
     * @return whether the item can be moved
     */
    public Boolean getMovable() {
        return movable;
    }
}

class Key extends Item {
    private Room roomUnlock; // the room it unlocks

    /**
     * Creates a key that is used to unlock a specified room
     * 
     * @param name
     * @param description
     * @param room
     */
    public Key(String name, String description, Room room) {
        super(name, description, 0);
        itemtype = "key";
        roomUnlock = room;
    }

    /**
     * Overrides the parent method and unlocks a specific room.
     * Outputs a message notifying user that it unlocked a room.
     * 
     * @param room
     * @return true if room was unlocked, false otherwise
     */
    public boolean use(Room room) {
        if (room == roomUnlock) {
            roomUnlock.unlock();
            System.out.println("A room has been unlocked.");
            return true;
        }
        return false;
    }
}

class Weapon extends Item {
    private int damage; // base damage of the weapon

    /**
     * Creates a new weapon and sets its item type to weapon.
     * 
     * @param name
     * @param description
     * @param weight
     * @param damage
     */
    public Weapon(String name, String description, int weight, int damage) {
        super(name, description, weight);
        itemtype = "weapon";
        this.damage = damage;
    }

    /**
     * Deals damage to the given enemy.
     * The damage dealt is based on the result of the dice roll.
     * 
     * @param enemy
     */
    public void attack(Enemy enemy) {
        D20 dice = new D20();
        int turnDamage = (int) Math.round((damage * (0.5 + ((double) dice.roll() / 20))));
        enemy.takeDamage(turnDamage);
        System.out.println(String.format(
                "You attack %s with %s, it did %d damage.", enemy.getName(), name, turnDamage));
    }
}

class Book extends Item {
    private Spell spell; // the spell that the book unlocks

    /**
     * Creates a book that can be used to unlock a new spell.
     * 
     * @param name
     * @param description
     * @param spell
     */
    public Book(String name, String description, Spell spell) {
        super(name, description, 1);
        itemtype = "book";
        this.spell = spell;
    }

    /**
     * Used to add a new spell to the player.
     * 
     * @param player
     */
    public void use(Player player) {
        player.addSpell(spell);
        System.out.println("You learned a new spell: " + spell.getName());
    }
}

class Gamemap extends Item {
    // initialises the original map
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
    private String blurMap = map; // A version of the map with names blurred
    // a hashmap between room names and whether the player has visited the room
    private HashMap<String, Boolean> visitedFacts = new HashMap<String, Boolean>();

    /**
     * Creates a new map and initialises the values in visitedFacts.
     * Updates the pointer to be at the spawn.
     * 
     * @param name
     * @param description
     */
    public Gamemap(String name, String description) {
        super(name, description, 0);
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

    /**
     * Prints the current state of the map.
     */
    public void use() {
        System.out.println("You pull out your map.\n" + blurMap);
    }

    /**
     * Updates the position of the arrow that points to the room that the player is
     * currently in.
     * Used whenever the player moves to a different room.
     * 
     * @param roomname
     */
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

    /**
     * Checks all the rooms for whether the player has ever been there.
     * Blurs the name of the room on the map if they have not.
     */
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

    /**
     * Removes the symbols used to indicate locked doors.
     * 
     * @param roomname
     */
    public void unlock(String roomname) {
        map = map.replace((roomname == "Black Hall") ? "--" : "==", "  ");
        updateBlurred();
    }
}

class HealthPotion extends Item {
    private int count; // number of potions in the 'stack'

    /**
     * Creates a new 'stack' of health potions
     * 
     * @param count
     */
    public HealthPotion(int count) {
        super("healthPotion", "Restores half of your health.", 1);
        itemtype = "healthPotion";
        this.count = count;
    }

    /**
     * Adds a potion to the stack
     * 
     * @param count
     */
    public void add(int count) {
        this.count += count;
    }

    /**
     * Removes a potion from the stack
     * 
     * @param count
     */
    public void remove(int count) {
        this.count -= count;
    }

    /**
     * @return weight of individual potion is 1, so total weight = count of potions
     */
    public int getWeight() {
        return count;
    }

    /**
     * @return a specialised description that can show total weight
     */
    public String getDescription() {
        return description + " Total weight: " + count;
    }

    /**
     * Heals the player by half the amount of their current max health.
     * Does not overheal the player if their current health is higher than half of
     * max health.
     * 
     * @param player
     * @return remaining number of health potions
     */
    public int use(Player player) {
        int potency = (int) Math.ceil((double) player.getMaxHealth() / 2);
        System.out.println(potency);
        int health = Math.min(potency, player.getMaxHealth() - player.getHealth());
        player.heal(health);
        count -= 1;
        System.out.println(String.format("You recovered %d hp, current hp: %d/%d", health, player.getHealth(),
                player.getMaxHealth()));
        return count;
    }
}

class UpgradeItem extends Item {
    private Item originalItem; // item to be upgraded
    private Item replacementItem; // what the item is upgraded to

    /**
     * Creates a new UpgradeItem that is required to upgrade a certain item.
     * 
     * @param name
     * @param description
     * @param originalItem
     * @param replacementItem
     */
    public UpgradeItem(String name, String description, Item originalItem, Item replacementItem) {
        super(name, description, 1);
        this.originalItem = originalItem;
        this.replacementItem = replacementItem;
        itemtype = "upgradeItem";
    }

    /**
     * @param itemName
     * @return true if the input name matches the name of the original item, false
     *         otherwise
     */
    public boolean isOriginalItem(String itemName) {
        return originalItem.getName().equals(itemName);
    }

    /**
     * @return the original item
     */
    public Item getOriginalItem() {
        return originalItem;
    }

    /**
     * @return the replacement item
     */
    public Item getReplacementItem() {
        return replacementItem;
    }
}

class UpgradePoint extends Item {
    private String upgradeType; // type of item that it can upgrade

    /**
     * Creates a new UpgradePoint that can be used to upgrade items
     * 
     * @param name
     * @param description
     * @param upgradeType
     */
    public UpgradePoint(String name, String description, String upgradeType) {
        super(name, description, 100);
        itemtype = "upgradePoint";
        this.upgradeType = upgradeType;
        movable = false;
    }

    /**
     * Checks whether the required item to upgrade the specified item is in the
     * player's inventory
     * If it's there, upgrade the item, otherwise print error message
     * 
     * @param command
     * @param player
     */
    public void use(Command command, Player player) {
        if (!command.hasThirdWord()) {
            System.out.println("What are you doing with this.");
            System.out.println("(hint: include item to be upgraded as third word)");
            return;
        }
        String originalItemName = command.getThirdWord();
        for (Item item : player.getInventory()) {
            if (item.getItemtype() == "upgradeItem" && ((UpgradeItem) item).isOriginalItem(originalItemName)) {
                Item newItem = ((UpgradeItem) item).getReplacementItem();
                if (!newItem.getItemtype().equals(upgradeType)) {
                    System.out.println("You can't upgrade this here.");
                    return;
                }
                Item originalItem = ((UpgradeItem) item).getOriginalItem();
                player.pickup(newItem);
                System.out.println(String.format("You upgraded %s into %s.", originalItemName, newItem.getName()));
                if (newItem.getItemtype() == "weapon" && player.isEquipped((Weapon) originalItem)) {
                    player.equip((Weapon) newItem);
                }
                player.removeItem(originalItem);
                player.removeItem(item);
                return;
            }
        }
        System.out.println("You don't have something to upgrade this with.");
    }
}

class Backpack extends Item {
    /**
     * Creates a backpack that is used to increase the player storage capacity.
     */
    public Backpack() {
        super("backpack", "Increases storage", 0);
        itemtype = "backpack";
    }
}