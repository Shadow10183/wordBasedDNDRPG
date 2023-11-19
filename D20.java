/**
 * Class D20 - a dice in an adventure game.
 *
 * This class is part of the "World of Zuul" application.
 * "World of Zuul" is a very simple, text based adventure game.
 *
 * A "D20" simulates a dice with 20 sides, the outcome of the diceroll affects
 * the effectiveness of actions.
 * 
 * e.g. Dice roll on attack, a roll of 20 will multiply damage by 1.5 while a
 * roll of 1 will half the damage.
 * 
 * @author Aidan Leung Yau Hei
 * @version 2016.02.29
 */

public class D20 {
    public D20() {
        // do nothing
    }

    public int roll() {
        int result = 0;
        int rolls = (int) (Math.random() * 30) + 21;
        for (int i = 0; i < rolls; i++) {
            result = (int) (Math.random() * 20) + 1;
            System.out.print("\r  \r");
            System.out.print(result);
            try {
                Thread.sleep(Math.max(100, (i - rolls + 8) * 100));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println();
        return result;
    }
}
