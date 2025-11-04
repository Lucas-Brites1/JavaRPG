package Game.Character;

import Game.Attacks.Attack;
import Game.Map.MapGrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Entity implements Comparable<Entity> {
    protected String name;
    protected HashMap<String, Attack> attacks;
    protected int posX, posY;
    protected char symbol;

    protected double health;
    protected double healthMax;
    protected double stamina;
    protected double staminaMax;
    protected double damage;
    protected double defense;
    protected int lucky;
    protected int level;

    public Entity(String name, char symbol, double health, double stamina, double damage, double defense, int lucky) {
        this.name = name;
        this.symbol = symbol;
        this.health = health;
        healthMax = health;
        this.stamina = stamina;
        staminaMax = stamina;
        this.damage = damage;
        this.defense = defense;
        this.lucky = lucky;
        this.attacks = new HashMap<>();
        this.level = 1;
        posX = 0;
        posY = 0;
    }

    public Entity(Entity other) {
        this.name = other.name;
        this.symbol = other.symbol;
        this.health = other.health;
        this.healthMax = other.healthMax;
        this.stamina = other.stamina;
        this.staminaMax = other.staminaMax;
        this.level = other.level;
        this.damage = other.damage;
        this.defense = other.defense;
        this.lucky = other.lucky;
        this.attacks = new HashMap<>(other.attacks);
        this.posX = other.posX;
        this.posY = other.posY;
    }

    public boolean CanUseAttack(Attack attack, int targetX, int targetY) {
        return attack.CanUse(this) && attack.IsInRange(posX, posY, targetX, targetY) && IsAlive() && !(targetX == this.posX && targetY == this.posY);
    }

    public void ConsumeResources(Attack attack) {
        stamina -= attack.getStaminaCost();
        attack.Use();
    }

    public void AddAttack(String key, Attack attack) {
        attacks.put(key, attack);
    }

    public Attack GetAttack(String key) {
        return attacks.get(key);
    }

    public List<Attack> GetAllAttacks() {
        return new ArrayList<>(attacks.values());
    }

    public boolean IsAlive() {
     return health > 0;
    }

    public void SetPosition(int x, int y) {
        posX = x;
        posY = y;
    }

    public String getName() {
        return name;
    }

    public double getDamage() {
        return damage;
    }

    public double getDefense() {
        return defense;
    }

    public double getHealth() {
        return health;
    }

    public double getHealthMax() {
        return healthMax;
    }

    public double getStamina() {
        return stamina;
    }

    public double getStaminaMax() {
        return staminaMax;
    }

    public char getSymbol() {
        return symbol;
    }

    public void MarkAsDead() {
        this.symbol = MapGrid.EMPTY_CHAR;
    }

    public HashMap<String, Attack> getAttacks() {
        return attacks;
    }

    public int getLucky() {
        return lucky;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return String.format("%s | HP: %f/%f | Stamina: %f/%f | ATK: %f | DEF: %f",
                name, health, healthMax, stamina, staminaMax, damage, defense);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Entity other = (Entity) obj;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode() + posX + posY;
    }


    @Override
    public int compareTo(Entity other) {
        int nivelComparison = Integer.compare(this.level, other.level);
        if (nivelComparison != 0) {
            return nivelComparison;
        }

        int healthComparison = Double.compare(this.healthMax, other.healthMax);
        if (healthComparison != 0) {
            return healthComparison;
        }

        return this.name.compareTo(other.name);
    }

    public void TakeDamage(double amount) {
        health = Math.max(0, health - amount);
    }

    public void Heal(double amount) {
        health = Math.min(healthMax, health + amount);
    }

    public void RecoverStamina(double amount) {
        stamina = Math.min(staminaMax, stamina + amount);
    }

    public void RestoreToFull() {
        health = healthMax;
        stamina = staminaMax;
    }
}

