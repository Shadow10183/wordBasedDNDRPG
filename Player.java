public class Player {
    private Weapon equippedweapon;
    private int health;
    private int level;
    private int maxHealth;

    public Player() {
        health = 7;
        maxHealth = 7;
        level = 1;
        equippedweapon = new Weapon("Fists", "Your bare fists.", 1);
    }

    public void takeDamage(int damage) {
        health -= damage;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void equipWeapon(Weapon weapon) {
        equippedweapon = weapon;
    }

    public Weapon getWeapon() {
        return equippedweapon;
    }

    public void levelUp(int level) {
        if (level > this.level) {
            switch (level) {
                case 1:
                    health = 7;
                    maxHealth = 7;
                    break;
                case 2:
                    health = 14;
                    maxHealth = 14;
                    break;
                case 3:
                    health = 22;
                    maxHealth = 22;
                    break;
                case 4:
                    health = 30;
                    maxHealth = 30;
                    break;
            }
            System.out.println(String.format("You got stronger!\nLevel increased to %d.\nMax HP increased to %d.",
                    this.level, maxHealth));
        } else {
            health = maxHealth;
            System.out.println("You have recovered your health.");
        }
    }
}
