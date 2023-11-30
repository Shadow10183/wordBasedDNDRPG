/**
 * This class is part of the "Castle of Shmorgenyorg" application.
 * "Castle of Shmorgenyorg" is a very simple, text based adventure game.
 * 
 * This class holds an enumeration of all command words known to the game.
 * It is used to recognise commands as they are typed in.
 *
 * @author Aidan Leung Yau Hei (k23093432), Michael Kölling and David J. Barnes
 * @version 2023.11.30
 */

public class CommandWords {
    // a constant array that holds all valid command words
    private static final String[] validCommands = {
            "quit", "help", "go", "back", "search", "pickup", "drop", "inventory", "use", "equip", "attack", "cast"
    };
    // a constant array that holds all valid command words during exploration
    private static final String[] validExplorationCommands = {
            "quit", "help", "go", "back", "search", "pickup", "drop", "inventory", "use", "equip", "attack"
    };
    // a constant array that holds all valid command words during combat
    private static final String[] validCombatCommands = {
            "quit", "help", "attack", "cast", "use", "inventory"
    };

    /**
     * Constructor - initialise the command words.
     */
    public CommandWords() {
        // nothing to do at the moment...
    }

    /**
     * Check whether a given String is a valid command word.
     * 
     * @return true if it is, false if it isn't.
     */
    public boolean isCommand(String aString) {
        for (int i = 0; i < validCommands.length; i++) {
            if (validCommands[i].equals(aString))
                return true;
        }
        // if we get here, the string was not found in the commands
        return false;
    }

    /**
     * Print all valid exploration commands to System.out.
     */
    public void showExploration() {
        for (String command : validExplorationCommands) {
            System.out.print(command + "  ");
        }
        System.out.println();
    }

    /**
     * Print all valid combat commands to System.out.
     */
    public void showCombat() {
        for (String command : validCombatCommands) {
            System.out.print(command + "  ");
        }
        System.out.println();
    }
}
