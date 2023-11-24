import java.util.ArrayList;

public class Player {
    private Weapon equippedweapon;
    private int level;
    private int health;
    private int maxHealth;
    private int mana;
    private int maxMana;
    private int storage;
    private int maxStorage;
    private ArrayList<Spell> spells = new ArrayList<>();

    public Player() {
        health = 7;
        maxHealth = 7;
        mana = 5;
        maxMana = 5;
        level = 1;
        storage = 0;
        maxStorage = 10;
        equippedweapon = new Weapon("Fists", "Your bare fists.", 0, 1);
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

    public int getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public boolean useMana(int manaCost) {
        if (mana >= manaCost) {
            mana -= manaCost;
            return true;
        }
        return false;
    }

    public void recoverMana() {
        if (mana < maxMana) {
            mana += 1;
            System.out.println(String.format("Current mana: %d/%d", mana, maxMana));
        }
    }

    public void equipWeapon(Weapon weapon) {
        equippedweapon = weapon;
    }

    public Weapon getWeapon() {
        return equippedweapon;
    }

    public boolean willPickup(int weight) {
        return ((storage + weight) <= maxStorage);
    }

    public void pickup(int weight) {
        storage += weight;
    }

    public void drop(int weight) {
        storage -= weight;
    }

    public int getStorage() {
        return storage;
    }

    public void getBackpack() {
        maxStorage += 5;
    }

    public int getMaxStorage() {
        return maxStorage;
    }

    public void addSpell(Spell spell) {
        spells.add(spell);
    }

    public Spell getSpell(String spellName) {
        if (spells.size() == 0) {
            System.out.println("You haven't learnt any spells.");
            return null;
        }
        for (Spell spell : spells) {
            if (spell.getName().equals(spellName)) {
                return spell;
            }
        }
        System.out.println("You haven't learnt this spell.");
        return null;
    }

    public void showSpells() {
        String allSpells = "";
        for (Spell spell : spells) {
            allSpells += spell.getName() + " ";
        }
        if (allSpells == "") {
            System.out.println("You haven't learnt any spells.");
            return;
        }
        System.out.println("The spells you have learnt are:\n" + allSpells);
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
            mana = maxMana;
            health = maxHealth;
            System.out.println("You have recovered your health and mana.");
        }
    }
}
