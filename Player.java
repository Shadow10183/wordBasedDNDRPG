import java.util.ArrayList;

public class Player {
    private int level;
    private int health;
    private int maxHealth;
    private int mana;
    private int maxMana;
    private int storage;
    private int maxStorage;
    // whether there are enemies in the same room as player
    private boolean detected = false;
    // whether the player is actively fighting
    private boolean inCombat = false;
    private boolean dead = false;
    private Gamemap map;
    private Weapon equippedweapon;
    private Room currentRoom;
    private Enemy currentEnemy;
    // the taken path of rooms
    private ArrayList<Room> path = new ArrayList<>();
    private ArrayList<Item> inventory = new ArrayList<>();
    private ArrayList<Spell> spells = new ArrayList<>();

    /**
     * initialise the player
     */
    public Player() {
        health = 7;
        maxHealth = 7;
        mana = 5;
        maxMana = 5;
        level = 1;
        storage = 0;
        maxStorage = 10;
        // initialise default weapon of player
        equippedweapon = new Weapon("fists", "Your bare fists.", 0, 1);
    }

    /**
     * used to place the player at start of the game or by the teleporter
     * 
     * @param room
     */
    public void setRoom(Room room) {
        currentRoom = room;
        System.out.println(currentRoom.getLongDescription());
        path.add(currentRoom);
    }

    /**
     * Try to in to one direction. If there is an exit, enter the new room and
     * updates the map, otherwise print an error message.
     * 
     * @param command The command to be processed.
     * @return true if player successfully enters a new room, false otherwise
     */
    public boolean goRoom(Command command) {
        if (inCombat) {
            // if player is in combat, they can't go to a different room.
            System.out.println("You can't do that while in a fight.");
            return false;
        }
        if (detected) {
            // if there are enemies in the same room, the player cannot go past them.
            System.err.println("You cannot do that until all enemies have been defeated.");
            System.out.println("If you are not ready, go back.");
            return false;
        }
        if (!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Where you goin bucko?");
            return false;
        }
        String direction = command.getSecondWord();
        if (!Room.validDirections.contains(direction)) {
            // if the second word is not a valid direction, player can't go there.
            System.out.println("Where you goin bucko?");
            return false;
        }
        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);
        if (nextRoom == null) {
            // there is no room in the given direction
            System.out.println("You can't phase through a wall like that.");
            return false;
        } else if (nextRoom.isLocked() == true) {
            System.out.println("The door is locked. Maybe you should find a key.");
            return false;
        } else {
            currentRoom = nextRoom;
            if (currentRoom.getName() == "Teleporter") {
                // the player will get teleported to a random room
                System.out.println(currentRoom.getShortDescription());
                currentRoom = currentRoom.getRandomExit();
            }
            System.out.println(currentRoom.getLongDescription());
            map.updatePointer(currentRoom.getName());
            path.add(currentRoom);
            detected = currentRoom.hasEnemy();
            return true;
        }
    }

    /**
     * Try to go back to the previous room. If successful, updates the map,
     * otherwise print an error message.
     * 
     * @return true if player successfully enters a different room, false otherwise
     */
    public boolean goBack() {
        if (inCombat) {
            // if player is in combat, they can't go to a different room.
            System.out.println("You can't do that while in a fight.");
            return false;
        }
        if (path.size() <= 1) {
            System.out.println("There is no path to retrace, go explore.");
            return false;
        }
        int lastIndex = path.size() - 1;
        currentRoom = path.get(lastIndex - 1);
        path.remove(lastIndex);
        System.out.println("You have retraced your path.");
        if (currentRoom.hasEnemy()) {
            System.out.println(currentRoom.getShortDescription());
            currentRoom.showEnemy();
        } else {
            System.out.println(currentRoom.getLongDescription());
        }
        map.updatePointer(currentRoom.getName());
        detected = currentRoom.hasEnemy();
        return true;
    }

    /**
     * searches the current room for items
     * 
     * @return false as this will not lead to quitting or death of player.
     */
    public boolean search() {
        if (inCombat) {
            // if player is in combat, they can't search the room.
            System.out.println("You can't do that while in a fight.");
            return false;
        }
        if (detected) {
            // if there are enemies in the same room, the player can't search the room
            // either.
            System.err.println("You cannot do that until all enemies have been defeated.");
            System.out.println("If you are not ready, go back.");
            return false;
        }
        currentRoom.showItems();
        return false;
    }

    /**
     * Tries to pick up an item in the room. If unsuccessful, prints an error
     * message.
     * 
     * @param command The command to be processed.
     * @return false as this will not lead to quitting or death of player.
     */
    public boolean pickup(Command command) {
        if (inCombat) {
            // if player is in combat, they can't pick up items.
            System.out.println("You can't do that while in a fight.");
            return false;
        }
        if (detected) {
            // if there are enemies in the same room, the player can't pick up items either.
            System.err.println("You cannot do that until all enemies have been defeated.");
            System.out.println("If you are not ready, go back.");
            return false;
        }
        if (!command.hasSecondWord()) {
            // if there is no second word, we don't know what to pick up.
            System.out.println("What are you picking up bozo?");
            return false;
        }
        String itemName = command.getSecondWord();
        for (Item item : currentRoom.getItems()) {
            if (item.getName().equals(itemName)) {
                if (!item.movable) {
                    System.out.println("This thing is affixed to the ground, it won't budge.");
                    return false;
                }
                switch (item.getItemtype()) {
                    // different case for different item type
                    case "backpack":
                        maxStorage += 5;
                        System.out.println("You put on the backpack and increase your storage capacity.");
                        break;
                    case "healthPotion":
                        HealthPotion healthPotion = (HealthPotion) item;
                        int count = healthPotion.getWeight();
                        if (command.hasThirdWord()) {
                            try {
                                count = Math.min(Integer.parseInt(command.getThirdWord()),
                                        healthPotion.getWeight());
                            } catch (NumberFormatException ex) {
                                ex.printStackTrace();
                            }
                        }
                        if (storage + count > maxStorage) {
                            // cannot pick up the item as that would exceed max storage capacity
                            System.out.println("You are too heavy to pick this up. Lose weight.");
                            return false;
                        }
                        storage += count;
                        boolean itemfound = false;
                        for (Item searchItem : inventory) {
                            if (searchItem.getItemtype() == "healthPotion") {
                                itemfound = true;
                                ((HealthPotion) searchItem).add(count);
                                break;
                            }
                        }
                        if (!itemfound) {
                            inventory.add(new HealthPotion(count));
                        }
                        System.out.println(
                                String.format("You picked up %sx%d and put it in your inventory.",
                                        healthPotion.getName(), count));
                        if (count != healthPotion.getWeight()) {
                            healthPotion.remove(count);
                            return false;
                        }
                        break;
                    default:
                        if (item.getWeight() + storage > maxStorage) {
                            // cannot pick up the item as that would exceed max storage capacity
                            System.out.println("You are too heavy to pick this up. Lose weight.");
                            return false;
                        }
                        storage += item.getWeight();
                        inventory.add(item);
                        System.out.println(
                                String.format("You picked up the %s and put it in your inventory.", item.getName()));
                }
                currentRoom.removeItem(item);
                return false;
            }
        }
        // the item is not found in the room
        System.out.println("What were you trying to pick up... the air?");
        return false;
    }

    /**
     * try to drop an item from the inventory
     * 
     * @param command The command to be processed.
     * @return false as this will not lead to quitting or death of player.
     */
    public boolean drop(Command command) {
        if (inCombat) {
            // if player is in combat, they can't drop items.
            System.out.println("You can't do that while in a fight.");
            return false;
        }
        if (detected) {
            // if there are enemies in the same room, the player can't drop items either.
            System.err.println("You cannot do that until all enemies have been defeated.");
            System.out.println("If you are not ready, go back.");
            return false;
        }
        if (!command.hasSecondWord()) {
            // if there is no second word, we don't know what to pick up.
            System.out.println("What are you dropping bozo?");
            return false;
        }
        String itemName = command.getSecondWord();
        for (Item item : inventory) {
            if (item.getName().equals(itemName)) {
                if (item.getItemtype() == "healthPotion") {
                    // special case for health potions
                    HealthPotion healthPotion = (HealthPotion) item;
                    int count = healthPotion.getWeight();
                    if (command.hasThirdWord()) {
                        try {
                            count = Math.min(Integer.parseInt(command.getThirdWord()),
                                    healthPotion.getWeight());
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                        }
                    }
                    boolean itemfound = false;
                    for (Item searchItem : currentRoom.getItems()) {
                        if (searchItem.getItemtype() == "healthPotion") {
                            itemfound = true;
                            ((HealthPotion) searchItem).add(count);
                            break;
                        }
                    }
                    if (!itemfound) {
                        currentRoom.addItem(new HealthPotion(count));
                    }
                    System.out.println(
                            String.format("You dropped %sx%d ", healthPotion.getName(),
                                    (Math.min(count, healthPotion.getWeight()))));
                    if (count != healthPotion.getWeight()) {
                        healthPotion.remove(count);
                        storage -= count;
                        return false;
                    }
                } else {
                    if (item == equippedweapon) {
                        System.out.println("You can't drop something that is currently equipped.");
                        return false;
                    }
                    currentRoom.addItem(item);
                    System.out.println(String.format("You dropped %s.", itemName));
                }
                inventory.remove(item);
                storage -= item.getWeight();
                return false;
            }
        }
        // the item is not found in the inventory
        System.out.println("What are you trying to drop.");
        return false;
    }

    /**
     * shows the player's inventory
     * if there is a specified item, try to show its description
     * 
     * @param command The command to be processed.
     * @return false as this will not lead to quitting or death of player.
     */
    public boolean showInventory(Command command) {
        if (!command.hasSecondWord()) {
            // no item is specified
            if (inventory.size() == 0) {
                System.out.println("You have no items in inventory.");
            } else {
                // print the storage capacity and each item's name
                System.out.println(
                        String.format("Your inventory contains: [%d/%d]", storage, maxStorage));
                for (Item item : inventory) {
                    if (item.getItemtype() != "healthPotion") {
                        System.out.print(item.getName() + " ");
                    } else {
                        System.out.print(item.getName() + String.format("x%d ", ((HealthPotion) item).getWeight()));
                    }
                }
                System.out.println();
            }
            return false;
        }
        String itemName = command.getSecondWord();
        for (Item item : inventory) {
            if (item.getName().equals(itemName)) {
                // the specified item is in the inventory
                System.out.println(item.getDescription());
                return false;
            }
        }
        // the specified item is not found
        System.out.println("You don't have that item.");
        return false;
    }

    /**
     * try to use an item in the inventory
     * 
     * @param command The command to be processed.
     * @return false as this will not lead to quitting or death of player.
     */
    public boolean use(Command command) {
        if (!command.hasSecondWord()) {
            // if there is no second word, we don't know what to use
            System.out.println("You can't just produce something out of thin air.");
            System.out.println("Use something you actually have.");
            return false;
        }
        String itemName = command.getSecondWord();
        // if the item is an upgrade point, check if it is in the room
        for (Item item : currentRoom.getItems()) {
            if (item.getItemtype() == "upgradePoint" && item.getName().equals(itemName)) {
                UpgradePoint upgradePoint = (UpgradePoint) item;
                upgradePoint.use(command, this);
                return false;
            }
        }
        for (Item item : inventory) {
            if (item.getName().equals(itemName)) {
                // check if the item is in the inventory
                switch (item.getItemtype()) {
                    // use the item based on the item type
                    case "map":
                        if (inCombat) {
                            // if player is in combat, they can't use this.
                            System.out.println("Why are you trying to use a map when you are getting killed.");
                            return false;
                        }
                        item.use();
                        System.out.println(currentRoom.getLongDescription());
                        return false;
                    case "key":
                        if (inCombat) {
                            // if player is in combat, they can't use this.
                            System.out.println("Why are you trying to use a key when you are getting killed.");
                            return false;
                        }
                        Key key = (Key) item;
                        for (Room room : currentRoom.getAllExits()) {
                            if (key.use(room)) {
                                map.unlock(room.getName());
                                inventory.remove(item);
                                System.out.println(currentRoom.getLongDescription());
                                return false;
                            }
                        }
                        System.out.println("You can't use this here.");
                        return false;
                    case "book":
                        Book book = (Book) item;
                        book.use(this);
                        inventory.remove(book);
                        return false;
                    case "healthPotion":
                        HealthPotion healthPotion = (HealthPotion) item;
                        if (healthPotion.use(this) == 0) {
                            inventory.remove(item);
                        }
                        if (inCombat) {
                            // counts as a turn and recovers mana
                            if (mana < maxMana) {
                                mana += 1;
                                System.out.println(String.format("Current mana: %d/%d", mana, maxMana));
                            }
                            attackResult();
                        }
                        storage -= 1;
                        return false;
                    default:
                        // the specified item can't be used
                        System.out.println("You can't use this here.");
                        return false;
                }
            }
        }
        // the item is not found in the inventory
        System.out.println("You can't use what you don't have.");
        return false;
    }

    /**
     * Tries to equip the given item
     * 
     * @param command The command to be processed.
     * @return false as this will not lead to quitting or death of player.
     */
    public boolean equip(Command command) {
        if (!command.hasSecondWord()) {
            // If there is no given item, prints a message showing the equipped weapon
            System.out.println(String.format("Your current weapon is/are %s.", equippedweapon.getName()));
            System.out.println(equippedweapon.getDescription());
            return false;
        }
        String itemName = command.getSecondWord();
        for (Item item : inventory) {
            // checks if the player has the item
            if (item.getName().equals(itemName)) {
                if (item.getItemtype() != "weapon") {
                    // item is not a weapon and cannot be equipped
                    System.out.println("You can't equip that as a weapon.");
                    return false;
                }
                equippedweapon = (Weapon) item;
                System.out.println(itemName + " has been equipped.");
                return false;
            }
        }
        // the item is not found in the inventory
        System.out.println("You can't equip what you don't have.");
        return false;
    }

    /**
     * if not in combat, try to enter a fight with the enemy
     * otherwise, attack the enemy with equipped weapon and recover mana
     * 
     * @param command The command to be processed.
     * @return the slain enemy, false if the enemy was not killed
     */
    public Enemy attack(Command command) {
        if (!command.hasSecondWord() && currentEnemy == null) {
            // if there is no second word, we don't know what enemy to start a fight with
            System.out.println("Attack what?");
            return null;
        }
        String enemyName = command.getSecondWord();
        if (!inCombat) {
            // what the player does if not in a fight
            for (Enemy enemy : currentRoom.getEnemies()) {
                // checks if the given enemy is in the room
                if (enemy.getName().equalsIgnoreCase(enemyName)) {
                    currentEnemy = enemy;
                }
            }
            if (currentEnemy == null) {
                // the given enemy is not in the room
                System.out.println("That enemy doesn't exist.");
                return null;
            }
            System.out.println(String.format("You enter in a fight with %s.",
                    currentEnemy.getName()));
            inCombat = true;
            return null;
        } else {
            // attacks the enemy and recover mana
            equippedweapon.attack(currentEnemy);
            if (mana < maxMana) {
                mana += 1;
                System.out.println(String.format("Current mana: %d/%d", mana, maxMana));
            }
            return attackResult();
        }
    }

    /**
     * try to cast a spell and damage the enemy
     * 
     * @param command The command to be processed.
     * @return the slain enemy, false if the enemy was not killed
     */
    public Enemy cast(Command command) {
        if (!inCombat) {
            // player cannot cast a spell if they are not in combat.
            System.out.println("You can only do that in a fight.");
            return null;
        }
        if (!command.hasSecondWord()) {
            // prints known spells
            String allSpells = "";
            for (Spell spell : spells) {
                allSpells += spell.getName() + " ";
            }
            if (allSpells == "") {
                System.out.println("You haven't learnt any spells.");
                return null;
            }
            System.out.println("The spells you have learnt are:\n" + allSpells);
            return null;
        }
        String spellName = command.getSecondWord();
        if (spells.size() == 0) {
            // the player has no spells to be cast
            System.out.println("You haven't learnt any spells.");
            return null;
        }
        for (Spell spell : spells) {
            if (spell.getName().equals(spellName)) {
                if (mana >= spell.getManaCost()) {
                    // only cast if the player has enough mana
                    spell.attack(currentEnemy);
                    mana -= spell.getManaCost();
                    System.out.println(String.format("It consumed %d mana. Current mana: %d/%d", spell.getManaCost(),
                            mana, maxMana));
                    return attackResult();
                } else {
                    System.out.println("You don't have enough mana.");
                    return null;
                }
            }
        }
        // the player doesn't know any spells that match the input
        System.out.println("You haven't learnt this spell.");
        return null;
    }

    /**
     * give the player a specific item
     * 
     * @param item
     */
    public void addItem(Item item) {
        inventory.add(item);
        storage += item.getWeight();
        if (item.getItemtype() == "map") {
            map = (Gamemap) item;
        }
    }

    /**
     * remove a specific item from the player
     * 
     * @param item
     */
    public void removeItem(Item item) {
        storage -= item.getWeight();
        inventory.remove(item);
    }

    /**
     * equip the given weapon
     * 
     * @param weapon
     */
    public void equip(Weapon weapon) {
        equippedweapon = weapon;
    }

    /**
     * @param weapon
     * @return true if the input weapon is the same as the equipped weapon,
     *         false otherwise
     */
    public boolean isEquipped(Weapon weapon) {
        return equippedweapon == weapon;
    }

    /**
     * @return the player's inventory
     */
    public ArrayList<Item> getInventory() {
        return inventory;
    }

    /**
     * @return true if the player is currently in a fight, false otherwise
     */
    public boolean isInCombat() {
        return inCombat;
    }

    /**
     * @return true if the player is dead, false otherwise
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * after attacking or casting a spell at an enemy, check if the enemy is killed
     * if the enemy is killed, try to level up or win the game if the boss was
     * killed
     * otherwise the enemy is told to attack the player
     * 
     * @return the enemy killed, null if the enemy is not killed
     */
    private Enemy attackResult() {
        if (currentEnemy.getHealth() <= 0) {
            Enemy slainEnemy = currentEnemy;
            currentEnemy = null;
            System.out.println(String.format("You have defeated the %s.", slainEnemy.getName()));
            inCombat = false;
            if (slainEnemy.getName() == "Dragon") {
                System.out.println("Congratulations! You have ridded this castle of its infestations.");
                dead = true;
                return null;
            }
            levelUp(slainEnemy.getLevel() + 1);
            if (!slainEnemy.hasDrop()) {
                String dropresult = "";
                for (Item item : slainEnemy.getDrops().keySet()) {
                    if (Math.random() > slainEnemy.getDrops().get(item)) {
                        continue;
                    }
                    String itemType = item.getItemtype();
                    String itemName = item.getName();
                    switch (itemType) {
                        case "healthPotion":
                            HealthPotion healthPotion = (HealthPotion) item;
                            int count = healthPotion.getWeight();
                            boolean itemfound = false;
                            for (Item searchItem : currentRoom.getItems()) {
                                if (searchItem.getItemtype() == "healthPotion") {
                                    itemfound = true;
                                    ((HealthPotion) searchItem).add(count);
                                    break;
                                }
                            }
                            if (!itemfound) {
                                currentRoom.addItem(new HealthPotion(count));
                            }
                            itemName += "x" + Integer.toString(count);
                            dropresult = (dropresult == "") ? itemName : dropresult + " " + itemName;
                            break;
                        default:
                            currentRoom.addItem(item);
                            dropresult = (dropresult == "") ? itemName : dropresult + " " + itemName;
                    }
                }
                if (dropresult != "") {
                    System.out.println("It dropped\n" + dropresult);
                }
            }
            currentRoom.removeEnemy(slainEnemy);
            detected = currentRoom.hasEnemy();
            return slainEnemy;
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        currentEnemy.attack(this);
        if (health <= 0) {
            System.out.println("You got yourself killed, do better.");
            dead = true;
            return null;
        }
        System.out.println(String.format("Your HP: %d/%d \t%s HP: %d", health, maxHealth, currentEnemy.getName(),
                currentEnemy.getHealth()));
        return null;
    }

    /**
     * the player loses health based on the given damage
     * 
     * @param damage
     */
    public void takeDamage(int damage) {
        health -= damage;
    }

    /**
     * restores the player's health by half of the max health
     * print a message showing how much the player was healed
     * 
     * @param health
     */
    public void heal() {
        // amount
        int potency = Math.min(maxHealth - health, (int) Math.ceil((double) maxHealth / 2));
        health += potency;
        System.out.println(String.format("You recovered %d hp, current hp: %d/%d", potency, health, health));
    }

    /**
     * adds a new spell to the player's repertoire
     * 
     * @param spell
     */
    public void addSpell(Spell spell) {
        spells.add(spell);
    }

    /**
     * when the player defeats an enemy, tries to level up.
     * restores health and mana as well
     * 
     * @param level
     */
    public void levelUp(int level) {
        if (level > this.level) {
            this.level = level;
            switch (level) {
                case 1:
                    maxHealth = 7;
                    break;
                case 2:
                    maxHealth = 14;
                    break;
                case 3:
                    maxHealth = 22;
                    break;
                case 4:
                    maxHealth = 30;
                    break;
            }
            System.out.println("You got stronger!");
            System.out.println(String.format("Level increased to %d. Max HP increased to %d.", this.level, maxHealth));
        }
        mana = maxMana;
        health = maxHealth;
        System.out.println("You have recovered your health and mana.");
    }
}