import java.util.ArrayList;

public class Player {
    private int level;
    private int health;
    private int maxHealth;
    private int mana;
    private int maxMana;
    private int storage;
    private int maxStorage;
    private boolean detected = false;
    private boolean inCombat = false;
    private boolean dead = false;
    private Gamemap map;
    private Weapon equippedweapon;
    private Room currentRoom;
    private Enemy currentEnemy;
    private ArrayList<Room> path = new ArrayList<>();
    private ArrayList<Item> inventory = new ArrayList<>();
    private ArrayList<Spell> spells = new ArrayList<>();

    public Player() {
        health = 7;
        maxHealth = 7;
        mana = 5;
        maxMana = 5;
        level = 1;
        storage = 0;
        maxStorage = 10;
        equippedweapon = new Weapon("fists", "Your bare fists.", 0, 1);
    }

    public void setRoom(Room room) {
        currentRoom = room;
        Printer.println(currentRoom.getLongDescription());
        path.add(currentRoom);
    }

    public Boolean goRoom(Command command) {
        if (inCombat) {
            Printer.println("You can't do that while in a fight.");
            return false;
        }
        if (detected) {
            System.err.println("You cannot do that until all enemies have been defeated.");
            Printer.println("If you are not ready, go back.");
            return false;
        }
        if (!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            Printer.println("Where you goin bucko?");
            return false;
        }
        String direction = command.getSecondWord();
        if (!Room.validDirections.contains(direction)) {
            Printer.println("Where you goin bucko?");
            return false;
        }
        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);
        if (nextRoom == null) {
            Printer.println("You can't phase through a wall like that.");
            return false;
        } else if (nextRoom.isLocked() == true) {
            Printer.println("The door is locked. Maybe you should find a key.");
            return false;
        } else {
            currentRoom = nextRoom;
            if (currentRoom.getName() == "Teleporter") {
                Printer.println(currentRoom.getShortDescription());
                currentRoom = currentRoom.getRandomExit();
            }
            Printer.println(currentRoom.getLongDescription());
            map.updatePointer(currentRoom.getName());
            path.add(currentRoom);
            return true;
        }
    }

    public void goBack() {
        if (inCombat) {
            Printer.println("You can't do that while in a fight.");
            return;
        }
        if (path.size() <= 1) {
            Printer.println("There is no path to retrace, go explore.");
            return;
        }
        int lastIndex = path.size() - 1;
        currentRoom = path.get(lastIndex - 1);
        path.remove(lastIndex);
        Printer.println("You have retraced your path.");
        if (currentRoom.hasEnemy()) {
            Printer.println(currentRoom.getShortDescription());
            currentRoom.showEnemy();
        } else {
            Printer.println(currentRoom.getLongDescription());
        }
        map.updatePointer(currentRoom.getName());
    }

    public boolean search() {
        if (inCombat) {
            Printer.println("You can't do that while in a fight.");
            return false;
        }
        if (detected) {
            System.err.println("You cannot do that until all enemies have been defeated.");
            Printer.println("If you are not ready, go back.");
            return false;
        }
        currentRoom.showItems();
        return false;
    }

    public boolean pickup(Command command) {
        if (inCombat) {
            Printer.println("You can't do that while in a fight.");
            return false;
        }
        if (detected) {
            System.err.println("You cannot do that until all enemies have been defeated.");
            Printer.println("If you are not ready, go back.");
            return false;
        }
        if (!command.hasSecondWord()) {
            Printer.println("What are you picking up bozo?");
            return false;
        }
        String itemName = command.getSecondWord();
        for (Item item : currentRoom.getItems()) {
            if (item.getName().equals(itemName)) {
                if (!item.movable) {
                    Printer.println("This thing is affixed to the ground, it won't budge.");
                    return false;
                }
                switch (item.getItemtype()) {
                    case "backpack":
                        maxStorage += 5;
                        Printer.println("You put on the backpack and increase your storage capacity.");
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
                            Printer.println("You are too heavy to pick this up. Lose weight.");
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
                        Printer.println(
                                String.format("You picked up %sx%d and put it in your inventory.",
                                        healthPotion.getName(), count));
                        if (count != healthPotion.getWeight()) {
                            healthPotion.remove(count);
                            return false;
                        }
                        break;
                    default:
                        if (item.getWeight() + storage > maxStorage) {
                            Printer.println("You are too heavy to pick this up. Lose weight.");
                            return false;
                        }
                        storage += item.getWeight();
                        inventory.add(item);
                        Printer.println(
                                String.format("You picked up the %s and put it in your inventory.", item.getName()));
                }
                currentRoom.removeItem(item);
                return false;
            }
        }
        Printer.println("What were you trying to pick up... the air?");
        return false;
    }

    public boolean drop(Command command) {
        if (inCombat) {
            Printer.println("You can't do that while in a fight.");
            return false;
        }
        if (detected) {
            System.err.println("You cannot do that until all enemies have been defeated.");
            Printer.println("If you are not ready, go back.");
            return false;
        }
        if (!command.hasSecondWord()) {
            Printer.println("What are you dropping bozo?");
            return false;
        }
        String itemName = command.getSecondWord();
        for (Item item : inventory) {
            if (item.getName().equals(itemName)) {
                switch (item.getItemtype()) {
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
                        Printer.println(
                                String.format("You dropped %sx%d ", healthPotion.getName(),
                                        (Math.min(count, healthPotion.getWeight()))));
                        if (count != healthPotion.getWeight()) {
                            healthPotion.remove(count);
                            storage -= count;
                            return false;
                        }
                        break;
                    default:
                        if (item == equippedweapon) {
                            Printer.println("You can't drop something that is currently equipped.");
                            return false;
                        }
                        currentRoom.addItem(item);
                        Printer.println(String.format("You dropped %s.", itemName));
                }
                inventory.remove(item);
                storage -= item.getWeight();
                return false;
            }
        }
        Printer.println("What are you trying to drop.");
        return false;
    }

    public boolean showInventory(Command command) {
        if (!command.hasSecondWord()) {
            if (inventory.size() == 0) {
                Printer.println("You have no items in inventory.");
            } else {
                Printer.println(
                        String.format("Your inventory contains: [%d/%d]", storage, maxStorage));
                for (Item item : inventory) {
                    if (item.getItemtype() != "healthPotion") {
                        Printer.print(item.getName() + " ");
                    } else {
                        Printer.print(item.getName() + String.format("x%d ", ((HealthPotion) item).getWeight()));
                    }
                }
                Printer.println();
            }
            return false;
        }
        String itemName = command.getSecondWord();
        for (Item item : inventory) {
            if (item.getName().equals(itemName)) {
                Printer.println(item.getDescription());
                return false;
            }
        }
        Printer.println("You don't have that item.");
        return false;
    }

    public boolean use(Command command) {
        if (!command.hasSecondWord()) {
            Printer.println("You can't just produce something out of thin air.");
            Printer.println("Use something you actually have.");
            return false;
        }
        String itemName = command.getSecondWord();
        for (Item item : currentRoom.getItems()) {
            if (item.getName().equals(itemName) && item.getItemtype() == "upgradePoint") {
                UpgradePoint upgradePoint = (UpgradePoint) item;
                upgradePoint.use(command, this);
                return false;
            }
        }
        for (Item item : inventory) {
            if (item.getName().equals(itemName)) {
                switch (item.getItemtype()) {
                    case "map":
                        if (inCombat) {
                            Printer.println("Why are you trying to use a map when you are getting killed.");
                            return false;
                        }
                        item.use();
                        Printer.println(currentRoom.getLongDescription());
                        return false;
                    case "key":
                        Key key = (Key) item;
                        for (Room room : currentRoom.getAllExits()) {
                            if (room == key.getUnlock()) {
                                key.use();
                                map.unlock(room.getName());
                                inventory.remove(item);
                                Printer.println(currentRoom.getLongDescription());
                                return false;
                            }
                        }
                        Printer.println("You can't use this here.");
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
                            if (mana < maxMana) {
                                mana += 1;
                                Printer.println(String.format("Current mana: %d/%d", mana, maxMana));
                            }
                            attackResult();
                        }
                        storage -= 1;
                        return false;
                    default:
                        Printer.println("You can't use this here.");
                        return false;
                }
            }
        }
        Printer.println("You can't use what you don't have.");
        return false;
    }

    public boolean equip(Command command) {
        if (!command.hasSecondWord()) {
            Printer.println(String.format("Your current weapon is/are %s.", equippedweapon.getName()));
            Printer.println(equippedweapon.getDescription());
            return false;
        }
        String itemName = command.getSecondWord();
        for (Item item : inventory) {
            if (item.getName().equals(itemName)) {
                if (item.getItemtype() != "weapon") {
                    Printer.println("You can't equip that as a weapon.");
                    return false;
                }
                equippedweapon = (Weapon) item;
                Printer.println(itemName + " has been equipped.");
                return false;
            }
        }
        Printer.println("You can't equip what you don't have.");
        return false;
    }

    public Enemy attack(Command command) {
        if (!command.hasSecondWord() && currentEnemy == null) {
            Printer.println("Attack what?");
            return null;
        }
        String enemyName = command.getSecondWord();
        for (Enemy enemy : currentRoom.getEnemies()) {
            if (enemy.getName().equalsIgnoreCase(enemyName)) {
                currentEnemy = enemy;
            }
        }
        if (!inCombat) {
            if (currentEnemy == null) {
                Printer.println("That enemy doesn't exist.");
                return null;
            }
            Printer.println(String.format("You enter in a fight with %s.",
                    currentEnemy.getName()));
            inCombat = true;
            return null;
        } else {
            equippedweapon.attack(currentEnemy);
            if (mana < maxMana) {
                mana += 1;
                Printer.println(String.format("Current mana: %d/%d", mana, maxMana));
            }
            return attackResult();
        }
    }

    public Enemy cast(Command command) {
        if (!inCombat) {
            Printer.println("You can only do that in a fight.");
            return null;
        }
        if (!command.hasSecondWord()) {
            String allSpells = "";
            for (Spell spell : spells) {
                allSpells += spell.getName() + " ";
            }
            if (allSpells == "") {
                Printer.println("You haven't learnt any spells.");
                return null;
            }
            Printer.println("The spells you have learnt are:\n" + allSpells);
            return null;
        }
        String spellName = command.getSecondWord();
        if (spells.size() == 0) {
            Printer.println("You haven't learnt any spells.");
            return null;
        }
        for (Spell spell : spells) {
            if (spell.getName().equals(spellName)) {
                if (mana >= spell.getManaCost()) {
                    spell.attack(currentEnemy);
                    mana -= spell.getManaCost();
                    Printer.println(String.format("It consumed %d mana. Current mana: %d/%d", spell.getManaCost(),
                            mana, maxMana));
                    return attackResult();
                } else {
                    Printer.println("You don't have enough mana.");
                    return null;
                }
            }
        }
        Printer.println("You haven't learnt this spell.");
        return null;
    }

    public void pickup(Item item) {
        inventory.add(item);
        storage += item.getWeight();
        if (item.getItemtype() == "map") {
            map = (Gamemap) item;
        }
    }

    public void removeItem(Item item) {
        storage -= item.getWeight();
        inventory.remove(item);
    }

    public void equip(Weapon weapon) {
        equippedweapon = weapon;
    }

    public boolean isEquipped(Weapon weapon) {
        return equippedweapon == weapon;
    }

    public ArrayList<Item> getInventory() {
        return inventory;
    }

    public boolean isInCombat() {
        return inCombat;
    }

    public boolean isDead() {
        return dead;
    }

    public void checkEnemy() {
        detected = false;
        if (currentRoom.hasEnemy()) {
            detected = true;
            currentRoom.showEnemy();
        }
    }

    private Enemy attackResult() {
        if (currentEnemy.getHealth() <= 0) {
            Enemy slainEnemy = currentEnemy;
            currentEnemy = null;
            Printer.println(String.format("You have defeated the %s.", slainEnemy.getName()));
            inCombat = false;
            if (slainEnemy.getName() == "Dragon") {
                Printer.println("Congratulations! You have ridded this castle of its infestations.");
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
                    Printer.println("It dropped\n" + dropresult);
                }
            }
            currentRoom.removeEnemy(slainEnemy);
            checkEnemy();
            return slainEnemy;
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        currentEnemy.attack(this);
        if (health <= 0) {
            Printer.println("You got yourself killed, do better.");
            dead = true;
            return null;
        }
        Printer.println(String.format("Your HP: %d/%d \t%s HP: %d", health, maxHealth, currentEnemy.getName(),
                currentEnemy.getHealth()));
        return null;
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    public void heal(int health) {
        this.health = Math.min(maxHealth, this.health + health);
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void addSpell(Spell spell) {
        spells.add(spell);
    }

    public void cast(Spell spell, Enemy currentEnemy) {
        spell.attack(currentEnemy);
        return;
    }

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
            Printer.println("You got stronger!");
            Printer.println(String.format("Level increased to %d. Max HP increased to %d.", this.level, maxHealth));
        }
        mana = maxMana;
        health = maxHealth;
        Printer.println("You have recovered your health and mana.");
    }
}
