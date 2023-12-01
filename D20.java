/**
 * This class is part of the "Castle of Shmorgenyorg" application.
 * "Castle of Shmorgenyorg" is a very simple, text based adventure game.
 *
 * This class simulates a dice with 20 sides, the outcome of the diceroll
 * affects the effectiveness of actions.
 * 
 * e.g. Dice roll on attack, a roll of 20 will multiply damage by 1.5 while a
 * roll of 1 will multiply damage by 0.5.
 * i.e. weapon of base damage 10 will do 15 or 5 respectively.
 * 
 * @author Aidan Leung Yau Hei (k23093432)
 * @version 2023.11.30
 */

public class D20 {
    /**
     * Constructor - initialise the D20 dice.
     */
    public D20() {
        // do nothing
    }

    /**
     * Simulates the rolling of a dice, displays numbers as it 'rolls'
     * with a small delay in between, increasing towards the final rolls.
     * Used to introduce chaos to combat gameplay.
     * 
     * @return the value of the final dice roll.
     */
    public int roll() {
        System.out.println("Rolling the dice!");
        int result = 0;
        int rolls = (int) (Math.random() * 3) + 6;
        for (int i = 0; i < rolls; i++) {
            try {
                Thread.sleep(Math.max(200, (i - rolls + 5) * 200));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            result = (int) (Math.random() * 20) + 1;
            if (i != 0) {
                System.out.print("->");
            }
            System.out.print(result);
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("\nResult: " + result);
        return result;
    }
}