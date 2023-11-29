
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
    protected int weight;
    protected boolean movable = true;

    public Item(String name, String description, int weight) {
        this.name = name;
        this.description = description;
        this.weight = weight;
    }

    public void use() {
        System.out.println("You can't use this here.");
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description + " Weight: " + weight;
    }

    public String getItemtype() {
        return itemtype;
    }

    public int getWeight() {
        return weight;
    }

    public Boolean getMovable() {
        return movable;
    }
}

class Key extends Item {
    private Room roomUnlock;
    private String direction;

    public Key(String name, String description, Room room, String leadingdirection) {
        super(name, description, 0);
        itemtype = "key";
        roomUnlock = room;
        direction = leadingdirection;
    }

    public void use() {
        getUnlock().unlock();
        System.out.println("A room has been unlocked.");
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

    public Weapon(String name, String description, int weight, int damage) {
        super(name, description, weight);
        itemtype = "weapon";
        this.damage = damage;
    }

    public void attack(Enemy enemy) {
        D20 dice = new D20();
        int turnDamage = (int) Math.round((damage * (0.5 + ((double) dice.roll() / 20))));
        enemy.takeDamage(turnDamage);
        System.out.println(String.format(
                "You attack %s with %s, it did %d damage.", enemy.getName(), name, turnDamage));
    }
}

class Book extends Item {
    private Spell spell;

    public Book(String name, String description, Spell spell) {
        super(name, description, 1);
        itemtype = "book";
        this.spell = spell;
    }

    public void use(Player player) {
        player.addSpell(spell);
        System.out.println("You learned a new spell: " + spell.getName());
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

    public void use() {
        System.out.println("You pull out your map.\n" + blurMap);
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

class HealthPotion extends Item {
    private int count;

    public HealthPotion(int count) {
        super("healthPotion", "Restores half of your health.", 1);
        itemtype = "healthPotion";
        this.count = count;
    }

    public void add(int count) {
        this.count += count;
    }

    public void remove(int count) {
        this.count -= count;
    }

    public int getWeight() {
        return count;
    }

    public String getDescription() {
        return description + " Total weight: " + weight * count;
    }

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
    private Item originalItem;
    private Item replacementItem;

    public UpgradeItem(String name, String description, Item originalItem, Item replacementItem) {
        super(name, description, 1);
        this.originalItem = originalItem;
        this.replacementItem = replacementItem;
        itemtype = "upgradeItem";
    }

    public boolean isOriginalItem(String itemName) {
        return originalItem.getName().equals(itemName);
    }

    public Item getOriginalItem() {
        return originalItem;
    }

    public Item getReplacementItem() {
        return replacementItem;
    }
}

class UpgradePoint extends Item {
    private String upgradeType;

    public UpgradePoint(String name, String description, String upgradeType) {
        super(name, description, 100);
        itemtype = "upgradePoint";
        this.upgradeType = upgradeType;
        movable = false;
    }

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
    public Backpack() {
        super("backpack", "Increases storage", 0);
        itemtype = "backpack";
    }
}