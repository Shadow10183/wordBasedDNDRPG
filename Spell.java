class Spell {
    private String name;
    private int damage;
    private int manaCost;

    public Spell(String name, int damage, int manaCost) {
        this.name = name;
        this.damage = damage;
        this.manaCost = manaCost;
    }

    public void attack(Enemy enemy) {
        D20 dice = new D20();
        int turnDamage = (int) Math.round((damage * (0.5 + ((double) dice.roll() / 20))));
        enemy.takeDamage(turnDamage);
        System.out.println(String.format(
                "You cast %s at %s, it did %d damage.", name, enemy.getName(), turnDamage));
    }

    public String getName() {
        return name;
    }

    public int getManaCost() {
        return manaCost;
    }
}
