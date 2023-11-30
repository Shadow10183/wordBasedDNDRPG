/**
 * This class is part of the "Castle of Shmorgenyorg" application.
 * "Castle of Shmorgenyorg" is a very simple, text based adventure game.
 *
 * The spell can be cast by player in order to attack an enemy.
 * Spells have different damage and thus different mana cost.
 * 
 * @author Aidan Leung Yau Hei (k23093432)
 * @version 2023.11.30
 */

class Spell {
    private String name;
    private int damage;
    private int manaCost;

    /**
     * Creates a new spell
     * 
     * @param name
     * @param damage
     * @param manaCost
     */
    public Spell(String name, int damage, int manaCost) {
        this.name = name;
        this.damage = damage;
        this.manaCost = manaCost;
    }

    /**
     * Cast by player to deal damage to an enemy
     * The spell rolls a D20 and the final damage is determined by the dice roll.
     * Prints a message showing the final damage dealt to the enemy.
     * 
     * @param enemy to be dealt damage to
     */
    public void attack(Enemy enemy) {
        D20 dice = new D20();
        int turnDamage = (int) Math.round((damage * (0.5 + ((double) dice.roll() / 20))));
        enemy.takeDamage(turnDamage);
        System.out.println(String.format(
                "You cast %s at %s, it did %d damage.", name, enemy.getName(), turnDamage));
    }

    /**
     * @return name of the spell
     */
    public String getName() {
        return name;
    }

    /**
     * @return manacost of the spell
     */
    public int getManaCost() {
        return manaCost;
    }
}
