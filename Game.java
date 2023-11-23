
/**
 * This class is the main class of the "World of Zuul" application.
 * "World of Zuul" is a very simple, text based adventure game. Users
 * can walk around some scenery. That's all. It should really be extended
 * to make it more interesting!
 * 
 * To play this game, create an instance of this class and call the "play"
 * method.
 * 
 * This main class creates and initialises all the others: it creates all
 * rooms, creates the parser and starts the game. It also evaluates and
 * executes the commands that the parser returns.
 * 
 * @author Aidan Leung Yau Hei, Michael Kölling and David J. Barnes
 * @version 2016.02.29
 */
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ArrayList;

public class Game {
    private Parser parser;
    private Room currentRoom;
    private ArrayList<Item> inventory = new ArrayList<>();
    private ArrayList<Room> path = new ArrayList<>();
    private HashMap<Item, Room> itemLocations = new HashMap<>();
    private ArrayList<Room> randomRooms = new ArrayList<>();
    private HashMap<Enemy, Room> enemyList = new HashMap<>();
    private Room teleporter;
    private Gamemap map;
    private boolean inCombat = false;
    private boolean detected = false;
    private Player player;
    private Enemy currentEnemy;
    private boolean died = false;

    public static void main(String[] args) {
        Game mygame = new Game();
    }

    /**
     * Create the game and initialise its internal map.
     */
    public Game() {
        createEntities();
        parser = new Parser();
        play();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createEntities() {
        // initialize the player
        player = new Player();

        // create the rooms
        Room spawn, eastPier, westPier, blackHall, courtyard, pantry, diningHall, library, armoury, throneRoom;
        randomRooms.add(spawn = new Room("Spawn", "at the spawn point"));
        randomRooms.add(eastPier = new Room("East pier", "on the east pier"));
        randomRooms.add(westPier = new Room("West Pier", "on the west pier"));
        randomRooms.add(blackHall = new Room("Black Hall", "in the Black Hall", true));
        randomRooms.add(courtyard = new Room("Courtyard", "in the courtyard"));
        randomRooms.add(pantry = new Room("Pantry", "in the pantry"));
        randomRooms.add(diningHall = new Room("Dining hall", "in the dining hall"));
        randomRooms.add(library = new Room("Library", "in the library"));
        randomRooms.add(armoury = new Room("Armoury", "in the armoury"));
        throneRoom = new Room("Throne Room", "in the Throne Room. You feel a sinister presence", true);
        teleporter = new Room("Teleporter",
                "You go through a strange doorway and a bright flash dazzles your vision.");

        // create the items
        Item blackHallKey, throneRoomKey, stick, butterKnife, rustySword, honedSword, healthPotion1, healthPotion2;
        map = new Gamemap("map", "Gives you a bird's eye view.");
        blackHallKey = new Key("blackHallKey", "Unlocks the way to Black Hall.", blackHall, "north");
        throneRoomKey = new Key("throneRoomKey", "Unlocks the way to the Throne Room.", throneRoom, "north");
        stick = new Weapon("stick", "Cool pointy stick you found.", 2);
        butterKnife = new Weapon("butterKnife", "A common utensil with an uncommonly sharp edge.", 4);
        rustySword = new Weapon("rustySword", "This must've been discarded for quite some time.", 5);
        honedSword = new Weapon("honedSword", "Given new life from a fresh sharpening, it thanks your benevolence.", 8);
        healthPotion1 = new HealthPotion(2);
        healthPotion2 = new HealthPotion(3);

        // create the enemies
        Enemy imp, goblin, troll, ogre, bigbad;
        enemyList.put(imp = new Enemy("Imp", 1), blackHall);
        enemyList.put(goblin = new Enemy("Goblin", 2), courtyard);
        enemyList.put(troll = new Enemy("Troll", 2), diningHall);
        enemyList.put(ogre = new Enemy("Ogre", 3), library);
        enemyList.put(bigbad = new Enemy("Titan", 4), throneRoom);

        // place the items
        itemLocations.put(blackHallKey, westPier);
        itemLocations.put(throneRoomKey, armoury);
        itemLocations.put(stick, eastPier);
        itemLocations.put(butterKnife, pantry);
        itemLocations.put(rustySword, armoury);
        itemLocations.put(healthPotion1, eastPier);
        itemLocations.put(healthPotion2, westPier);
        inventory.add(map);

        // add enemy drops
        imp.addDrop(new HealthPotion(2), 1);

        // initialise room exits
        spawn.setExit("east", eastPier);
        spawn.setExit("north", blackHall);
        spawn.setExit("west", westPier);

        eastPier.setExit("west", spawn);

        westPier.setExit("east", spawn);

        blackHall.setExit("south", spawn);
        blackHall.setExit("west", pantry);
        blackHall.setExit("north", courtyard);

        pantry.setExit("east", blackHall);
        pantry.setExit("north", diningHall);
        pantry.setExit("southwest", teleporter);

        courtyard.setExit("east", armoury);
        courtyard.setExit("south", blackHall);
        courtyard.setExit("north", library);

        armoury.setExit("west", courtyard);

        diningHall.setExit("south", pantry);
        diningHall.setExit("north", library);

        library.setExit("south", courtyard);
        library.setExit("southwest", diningHall);
        library.setExit("north", throneRoom);

        throneRoom.setExit("south", library);

        currentRoom = spawn; // start game in spawn area
        path.add(currentRoom);
    }

    /**
     * Main play routine. Loops until end of play.
     */
    public void play() {
        printWelcome();

        // Enter the main command loop. Here we repeatedly read commands and
        // execute them until the game is over.

        boolean finished = false;
        while (!finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome() {
        System.out.println();
        System.out.println("Welcome to the World of Zuul!");
        System.out.println("World of Zuul is a new, incredibly exciting adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * 
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) {
        boolean wantToQuit = false;

        if (command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord().toLowerCase();

        if (died) {
            switch (commandWord) {
                case "quit":
                    wantToQuit = quit(command);
                    return wantToQuit;
                case "retry":
                    retry();
                    return wantToQuit;
                default:
                    System.out.println("Dude you died.");
                    return wantToQuit;
            }
        } else if (inCombat) {
            switch (commandWord) {
                case "help":
                    printCombatHelp();
                    return wantToQuit;
                case "inventory":
                    getInventory(command);
                    return wantToQuit;
                case "use":
                    useItem(command);
                    return wantToQuit;
                case "attack":
                    attack(command);
                    return wantToQuit;
                case "quit":
                    wantToQuit = quit(command);
                    return wantToQuit;
                default:
                    System.out.println("You can't do that while in a fight.");
                    return wantToQuit;
            }
        } else {
            switch (commandWord) {
                case "help":
                    printHelp();
                    return wantToQuit;
                case "go":
                    goRoom(command);
                    return wantToQuit;
                case "back":
                    goBack(command);
                    return wantToQuit;
                case "search":
                    search(command);
                    return wantToQuit;
                case "pickup":
                    pickup(command);
                    return wantToQuit;
                case "inventory":
                    getInventory(command);
                    return wantToQuit;
                case "use":
                    useItem(command);
                    return wantToQuit;
                case "drop":
                    drop(command);
                    return wantToQuit;
                case "equip":
                    equipWeapon(command);
                    return wantToQuit;
                case "attack":
                    attack(command);
                    return wantToQuit;
                case "quit":
                    wantToQuit = quit(command);
                    return wantToQuit;
                default:
                    System.out.println("You can't do that during exploration.");
                    return wantToQuit;
            }
        }
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the
     * command words.
     */
    private void printHelp() {
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("around the Castle of Shmorgenyorg.");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showExplorationCommands();
    }

    private void printCombatHelp() {
        System.out.println("You are in the middle of a battle.");
        System.out.println("Stand strong adventurer, and prevail.");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCombatCommands();
    }

    /**
     * Try to in to one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private void goRoom(Command command) {
        if (detected) {
            System.out.println(
                    "You cannot proceed until all enemies have been defeated.\nIf you are not ready, go back.");
            return;
        }

        if (!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is no door!");
        } else if (nextRoom.isLocked() == true) {
            System.out.println("The door is locked. Maybe you should find a key.");
        } else if (nextRoom == teleporter) {
            System.out.println(teleporter.getShortDescription());
            currentRoom = randomRooms.get((int) (Math.random() * randomRooms.size()));
            map.updatePointer(currentRoom.getName());
            path.add(currentRoom);
            if (hasEnemy()) {
                System.out.println(currentRoom.getMiddleDescription());
                showEnemy();
            } else {
                System.out.println(currentRoom.getLongDescription());
            }
        } else {
            currentRoom = nextRoom;
            map.updatePointer(currentRoom.getName());
            path.add(currentRoom);
            if (hasEnemy()) {
                System.out.println(currentRoom.getMiddleDescription());
                showEnemy();
            } else {
                System.out.println(currentRoom.getLongDescription());
            }
        }
    }

    private void goBack(Command command) {
        if (path.size() > 1) {
            currentRoom = path.get(path.size() - 2);
            map.updatePointer(currentRoom.getName());
            path.remove(path.size() - 1);
            System.out.println("You have retraced your path.");
            if (hasEnemy()) {
                System.out.println(currentRoom.getMiddleDescription());
                showEnemy();
            } else {
                System.out.println(currentRoom.getLongDescription());
            }
        } else {
            System.out.println("There is no path to retrace, go explore.");
        }
    }

    private void search(Command command) {
        if (detected) {
            System.out.println(
                    "You cannot search until all enemies have been defeated.\nIf you are not ready, go back.");
            return;
        }
        if (command.hasSecondWord()) {
            System.out.println("Hey bozo just search.");
        } else {
            String searchresult = "";
            for (Entry<Item, Room> entry : itemLocations.entrySet()) {
                if (entry.getValue() == currentRoom) {
                    String itemName;
                    if (entry.getKey().getItemtype() != "healthPotion") {
                        itemName = entry.getKey().getName() + " ";
                    } else {
                        itemName = entry.getKey().getName()
                                + String.format("x%d ", ((HealthPotion) entry.getKey()).getCount());
                    }
                    searchresult = (searchresult == "") ? itemName
                            : searchresult + " " + itemName;
                }
            }
            System.out.println("You search the room and find\n"
                    + ((searchresult == "") ? "nothing. There is nothing but the void here." : searchresult));
        }
    }

    private void pickup(Command command) {
        if (detected) {
            System.out.println(
                    "You cannot do that until all enemies have been defeated.\nIf you are not ready, go back.");
            return;
        }
        if (!command.hasSecondWord()) {
            System.out.println("What are you picking up bozo?");
            return;
        }
        String itemname = command.getSecondWord();
        boolean pickedup = false;
        for (Entry<Item, Room> entry : itemLocations.entrySet()) {
            if (entry.getValue() == currentRoom && entry.getKey().getName().equals(itemname)) {
                pickedup = true;
                String itemType = entry.getKey().getItemtype();
                if (itemType != "healthPotion") {
                    inventory.add(entry.getKey());
                    System.out.println(
                            String.format("You picked up the %s and put it in your inventory.",
                                    entry.getKey().getName()));
                } else {
                    boolean itemfound = false;
                    for (Item item : inventory) {
                        if (item.getItemtype() == itemType) {
                            itemfound = true;
                            ((HealthPotion) item).add(((HealthPotion) entry.getKey()).getCount());
                        }
                    }
                    if (!itemfound) {
                        inventory.add(entry.getKey());
                    }
                    System.out.println(
                            String.format("You picked up the %s(s) and put it in your inventory.",
                                    entry.getKey().getName()));
                }
                itemLocations.remove(entry.getKey());
                return;
            }
        }
        if (!pickedup) {
            System.out.println("What were you trying to pick up... a rock?");
        }
    }

    private void getInventory(Command command) {
        if (!command.hasSecondWord()) {
            if (inventory.size() == 0) {
                System.out.println("You have no items in inventory.");
            } else {
                System.out.println("Your inventory contains:");
                for (Item item : inventory) {
                    if (item.getItemtype() != "healthPotion") {
                        System.out.print(item.getName() + " ");
                    } else {
                        System.out.print(item.getName() + String.format("x%d ", ((HealthPotion) item).getCount()));
                    }
                }
                System.out.println();
                return;
            }
        }
        String itemname = command.getSecondWord();
        for (Item item : inventory) {
            if (item.getName().equals(itemname)) {
                System.out.println(item.getDescription());
            }
        }
    }

    private void useItem(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Use what?");
            return;
        }
        String itemname = command.getSecondWord();
        if (itemname.equals("map")) {
            System.out.println("You pull out your map.\n" + map.getMap());
            return;
        }
        for (Item item : inventory) {
            if (item.getName().equals(itemname)) {
                switch (item.getItemtype()) {
                    case "key":
                        Key key = (Key) item;
                        if (currentRoom.getExit(key.getDirection()) == key.getUnlock()) {
                            key.getUnlock().unlock();
                            map.unlock((key).getUnlock().getName());
                            inventory.remove(item);
                            System.out.println("A room has been unlocked.");
                            System.out.println(currentRoom.getLongDescription());
                            return;
                        } else {
                            System.out.println("You can't use this here.");
                            return;
                        }
                    case "healthPotion":
                        ((HealthPotion) item).use(player);
                        return;
                    default:
                        System.out.println("You can't use this here.");
                        return;
                }
            }
        }
        System.out.println("That item doesnt exist bozo.");
        return;
    }

    private void drop(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Drop what?");
            return;
        }
        String itemname = command.getSecondWord();
        for (Item item : inventory) {
            if (item.getName().equals(itemname)) {
                itemLocations.put(item, currentRoom);
                inventory.remove(item);
                System.out.println(String.format("You dropped %s.", itemname));
                return;
            }
        }
    }

    private void equipWeapon(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Equip what?");
            return;
        }
        String itemname = command.getSecondWord();
        for (Item item : inventory) {
            if (item.getName().equals(itemname)) {
                if (item.getItemtype() != "weapon") {
                    System.out.println("You can't equip that as a weapon.");
                    return;
                }
                player.equipWeapon((Weapon) item);
                System.out.println(itemname + " has been equipped.");
            }
        }
    }

    private void attack(Command command) {
        if (!command.hasSecondWord()) {
            System.out.println("Attack what?");
            return;
        }
        String enemyName = command.getSecondWord();
        for (Entry<Enemy, Room> entry : enemyList.entrySet()) {
            if (entry.getKey().getName().equalsIgnoreCase(enemyName)) {
                currentEnemy = entry.getKey();
            }
        }
        if (!inCombat) {
            System.out.println(String.format("You enter in a fight with %s.", currentEnemy.getName()));
            inCombat = true;
            return;
        } else {
            player.getWeapon().attack(currentEnemy);
            if (currentEnemy.getHealth() <= 0) {
                System.out.println(String.format("%s has been defeated.", currentEnemy.getName()));
                inCombat = false;
                player.levelUp(currentEnemy.getLevel() + 1);
                if (!currentEnemy.hasDrop()) {
                    String dropresult = "";
                    for (Entry<Item, Double> entry : currentEnemy.getDrops().entrySet()) {
                        if (Math.random() > entry.getValue()) {
                            continue;
                        }
                        String itemType = entry.getKey().getItemtype();
                        if (itemType != "healthPotion") {
                            itemLocations.put(entry.getKey(), currentRoom);
                            String itemName;
                            itemName = entry.getKey().getName();
                            dropresult = (dropresult == "") ? itemName
                                    : dropresult + " " + itemName;
                        } else {
                            boolean itemfound = false;
                            for (Entry<Item, Room> roomEntry : itemLocations.entrySet()) {
                                if (roomEntry.getValue() == currentRoom
                                        && roomEntry.getKey().getItemtype() == itemType) {
                                    itemfound = true;
                                    ((HealthPotion) roomEntry.getKey()).add(((HealthPotion) entry.getKey()).getCount());
                                }
                            }
                            if (!itemfound) {
                                itemLocations.put(entry.getKey(), currentRoom);
                            }
                            String itemName;
                            itemName = entry.getKey().getName()
                                    + String.format("x%d ", ((HealthPotion) entry.getKey()).getCount());
                            dropresult = (dropresult == "") ? itemName
                                    : dropresult + " " + itemName;
                        }
                    }
                    System.out.println("It dropped\n" + dropresult);
                }
                enemyList.remove(currentEnemy);
                hasEnemy();
                showEnemy();
            } else {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                enemyAttack(currentEnemy);
            }
        }
    }

    private void cast(Command command) {

    }

    private void enemyAttack(Enemy currentEnemy) {
        player.takeDamage(currentEnemy.getDamage());
        System.out
                .println(String.format("%s does %d damage to you.", currentEnemy.getName(), currentEnemy.getDamage()));
        if (player.getHealth() <= 0) {
            died();
            return;
        }
        System.out.println(String.format("Your HP: %d/%d \t%s HP: %d", player.getHealth(),
                player.getMaxHealth(), currentEnemy.getName(), currentEnemy.getHealth()));
    }

    /**
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game.
     * 
     * @return true, if this command quits the game, false otherwise.
     */
    private boolean quit(Command command) {
        if (command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        } else {
            return true; // signal that we want to quit
        }
    }

    private void retry() {
        Game newGame = new Game();
    }

    private void died() {
        died = true;
        System.out.println("You got yourself killed, do better.\n\nRestart? [Y/N]");
    }

    private boolean hasEnemy() {
        detected = false;
        for (Entry<Enemy, Room> entry : enemyList.entrySet()) {
            if (entry.getValue() == currentRoom) {
                detected = true;
            }
        }
        return detected;
    }

    private void showEnemy() {
        for (Entry<Enemy, Room> entry : enemyList.entrySet()) {
            if (entry.getValue() == currentRoom) {
                detected = true;
                System.out.println(String.format("A(n) %s sizes you up.", entry.getKey().getName()));
            }
        }
    }
}