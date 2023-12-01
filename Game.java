
/**
 * This class is the main class of the "Castle of Shmorgenyorg" application.
 * "Castle of Shmorgenyorg" is a very simple, text based adventure game. Users
 * can explore the vicinity of and within the castle. They will discover items
 * and face monsters. 
 * 
 * To play this game, create an instance of this class and call the "play"
 * method.
 * 
 * This main class creates and initialises all the others: it creates all
 * rooms, creates the parser and starts the game. It also evaluates and
 * executes the commands that the parser returns.
 * 
 * @author Aidan Leung Yau Hei (k23093432), Michael Kölling and David J. Barnes
 * @version 2023.11.30
 */

import java.util.HashMap;
import java.util.ArrayList;
import java.util.function.Function;

public class Game {
    private Parser parser; // Used to handle user inputs
    private Player player; // Stores the player
    private ArrayList<Enemy> enemyList = new ArrayList<>(); // holds a list of enemies that are alive
    // maps command strings to respective methods
    private HashMap<String, Function<Command, Boolean>> commandList = new HashMap<>();

    // main function for personal testing
    public static void main(String[] args) {
        Game mygame = new Game();
        mygame.play();
    }

    /**
     * Create the game and initialise its internal map.
     */
    public Game() {
        parser = new Parser();
        linkCommands();
    }

    /**
     * Create all the rooms and link their exits together.
     * Create all the items and place them on the map/enemy.
     * Create all the enemies and place them on the map.
     * Set the enemy drops.
     */
    private void createEntities() {
        // initialize the player
        player = new Player();

        // create the rooms and the teleporter
        Room spawn, eastPier, westPier, blackHall, courtyard, pantry, diningHall, library, armoury, throneRoom;
        Teleporter teleporter;
        spawn = new Room("Spawn", "You are at the spawn point.");
        eastPier = new Room("East pier", "You are on the east pier.");
        westPier = new Room("West Pier", "You are on the west pier.");
        blackHall = new Room("Black Hall", "You are in the Black Hall.", true);
        courtyard = new Room("Courtyard", "You are in the courtyard.");
        pantry = new Room("Pantry", "You are in the pantry.");
        diningHall = new Room("Dining hall", "You are in the dining hall.");
        library = new Room("Library", "You are in the library.");
        armoury = new Room("Armoury", "You are in the armoury.");
        throneRoom = new Room("Throne Room", "You are in the Throne Room.\nYou feel a sinister presence.", true);
        teleporter = new Teleporter("You go through a strange doorway and a bright flash dazzles your vision.");

        // add rooms allowed to be teleported to into the teleporter
        teleporter.addRoom(spawn);
        teleporter.addRoom(eastPier);
        teleporter.addRoom(westPier);
        teleporter.addRoom(blackHall);
        teleporter.addRoom(courtyard);
        teleporter.addRoom(pantry);
        teleporter.addRoom(diningHall);
        teleporter.addRoom(library);
        teleporter.addRoom(armoury);

        // initialise room exits
        spawn.setExit("east", eastPier);
        spawn.setExit("west", westPier);
        spawn.setExit("north", blackHall);

        eastPier.setExit("west", spawn);

        westPier.setExit("east", spawn);

        blackHall.setExit("south", spawn);
        blackHall.setExit("west", pantry);
        blackHall.setExit("north", courtyard);

        pantry.setExit("east", blackHall);
        pantry.setExit("southwest", teleporter);
        pantry.setExit("north", diningHall);

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

        // create the spells
        Spell fireball, lightning;
        fireball = new Spell("fireball", 7, 3);
        lightning = new Spell("lightning", 10, 4);

        // create the items
        Item map, blackHallKey, throneRoomKey, stick, butterKnife, rustySword, honedSword, healthPotionx2,
                healthPotionx3, anvil, sharpeningStone, backpack, fireballBook, lightningBook;
        map = new Gamemap("map", "Gives you a bird's eye view.");
        blackHallKey = new Key("blackHallKey", "Unlocks the way to Black Hall.", blackHall);
        throneRoomKey = new Key("throneRoomKey", "Unlocks the way to the Throne Room.", throneRoom);
        stick = new Weapon("stick", "Cool pointy stick you found.", 2, 2);
        butterKnife = new Weapon("butterKnife", "A common utensil with an uncommonly sharp edge.", 3, 4);
        rustySword = new Weapon("rustySword", "This must've been discarded for quite some time.", 5, 5);
        honedSword = new Weapon("honedSword", "Given new life, it thanks your benevolence.", 5, 8);
        healthPotionx2 = new HealthPotion(2);
        healthPotionx3 = new HealthPotion(3);
        anvil = new UpgradePoint("anvil", "A place to upgrade weapons", "weapon");
        sharpeningStone = new UpgradeItem("sharpeningStone", "Can be used to sharpen a dulled edge.", rustySword,
                honedSword);
        backpack = new Backpack();
        fireballBook = new Book("fireballBook", "A book containing a powerful spell.", fireball);
        lightningBook = new Book("lightningBook", "A book containing a powerful spell.", lightning);

        // create the enemies and add them to the list of enemies
        Enemy mimic, geoguy, imp, goblin, troll, ogre, boss;
        enemyList.add(mimic = new Enemy("Mimic", 1, true));
        enemyList.add(geoguy = new Enemy("Geoguy", 1, true));
        enemyList.add(imp = new Enemy("Imp", 1, false));
        enemyList.add(goblin = new Enemy("Goblin", 2, false));
        enemyList.add(troll = new Enemy("Troll", 2, false));
        enemyList.add(ogre = new Enemy("Ogre", 3, false));
        enemyList.add(boss = new Enemy("Dragon", 4, false));

        // place the enemies
        mimic.setRoom(library);
        geoguy.setRoom(library);
        imp.setRoom(blackHall);
        goblin.setRoom(courtyard);
        troll.setRoom(diningHall);
        ogre.setRoom(library);
        boss.setRoom(throneRoom);

        // place the items
        westPier.addItem(blackHallKey);
        armoury.addItem(throneRoomKey);
        eastPier.addItem(stick);
        pantry.addItem(butterKnife);
        armoury.addItem(rustySword);
        eastPier.addItem(healthPotionx2);
        westPier.addItem(healthPotionx3);
        armoury.addItem(anvil);
        library.addItem(lightningBook);
        player.pickup(map);

        // add enemy drops
        mimic.addDrop(backpack, 1);
        geoguy.addDrop(sharpeningStone, 1);
        goblin.addDrop(fireballBook, 1);

        // start game in spawn area
        player.setRoom(spawn);
    }

    /**
     * Main play routine. Loops until end of play.
     */
    public void play() {
        printWelcome();
        createEntities();

        // Enter the main command loop. Here we repeatedly read commands and
        // execute them until the game is over.

        boolean finished = false;
        while (!finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing. Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome() {
        System.out.println();
        System.out.println("Welcome to the Castle of Shmorgenyorg!");
        System.out.println("You are an adventurer tasked with cleansing it of its evils.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
    }

    /**
     * Links the command string into methods that execute corresponding commands.
     * A slightly more efficient way to do so than by using switch statement.
     * 
     * e.g. (foo) -> bar(foo) means an argument 'foo' is passed into method 'bar'.
     */
    private void linkCommands() {
        commandList.put("quit", (command) -> quit(command));
        commandList.put("help", (command) -> printHelp());
        commandList.put("go", (command) -> goRoom(command));
        commandList.put("back", (command) -> goBack());
        commandList.put("search", (command) -> player.search());
        commandList.put("pickup", (command) -> player.pickup(command));
        commandList.put("drop", (command) -> player.drop(command));
        commandList.put("inventory", (command) -> player.showInventory(command));
        commandList.put("use", (command) -> player.use(command));
        commandList.put("equip", (command) -> player.equip(command));
        commandList.put("attack", (command) -> attack(command));
        commandList.put("cast", (command) -> cast(command));
    }

    /**
     * Given a command, process (that is: execute) the command.
     * 
     * @param command The command to be processed.
     * @return true If the command ends the game, false otherwise.
     */
    private boolean processCommand(Command command) {
        if (command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord().toLowerCase();

        // gets the method from commandList and passes command as arg
        return commandList.get(commandWord).apply(command);
    }

    // implementations of user commands:

    /**
     * Print out some help information, alternate info when in combat.
     * Here we print some stupid, cryptic message and a list of the
     * command words.
     * 
     * @return false as this will not lead to quitting or death of player.
     */
    private boolean printHelp() {
        if (player.isInCombat()) {
            System.out.println("You are in the middle of a battle.");
            System.out.println("Stand strong adventurer, and prevail.");
            System.out.println();
            System.out.println("Your command words are:");
            parser.showCombatCommands();
        } else {
            System.out.println("You are lost. You are alone. You wander");
            System.out.println("around the Castle of Shmorgenyorg.");
            System.out.println();
            System.out.println("Your command words are:");
            parser.showExplorationCommands();
        }
        return false;
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

    /**
     * Try to in to one direction. If there is an exit, enter the new room.
     * If sucessfully entered a new room, enemies will also move.
     * 
     * @param command The command to be processed.
     * @return false as this will not lead to quitting or death of player.
     */
    private boolean goRoom(Command command) {
        if (player.goRoom(command)) {
            enemyMove();
        }
        return false;
    }

    /**
     * Tells the player to go back to the previous room.
     * Enemies will also be told to move.
     * 
     * @return false as this will not lead to quitting or death of player.
     */
    private boolean goBack() {
        if (player.goBack()) {
            enemyMove();
        }
        return false;
    }

    /**
     * Tells the player to attack with their weapon.
     * If the player successfully kills an enemy, remove it from the enemy list
     * 
     * @param command The command to be processed.
     * @return true if the player died from enemy attack, otherwise false
     */
    private boolean attack(Command command) {
        Enemy slainEnemy = player.attack(command);
        if (slainEnemy != null) {
            enemyList.remove(slainEnemy);
        }
        return player.isDead();
    }

    /**
     * Tells the player to cast a spell.
     * If the player successfully kills an enemy, remove it from the enemy list
     * 
     * @param command The command to be processed.
     * @return true if the player died from enemy attack, otherwise false
     */
    private boolean cast(Command command) {
        Enemy slainEnemy = player.cast(command);
        if (slainEnemy != null) {
            enemyList.remove(slainEnemy);
        }
        return player.isDead();
    }

    /*
     * Loops through all the enemies in the enemy list
     * and tells them to move.
     */
    private void enemyMove() {
        for (Enemy enemy : enemyList) {
            enemy.move();
        }
    }
}