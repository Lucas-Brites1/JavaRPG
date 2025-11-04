package Game.Attacks;

import Game.Character.Entity;
import Game.Dice;
import utils.Terminal;
import utils.Color;

public class Attack {
    private AttackPattern pattern;
    private String name;
    private int damage;
    private int staminaCost;
    private int cooldown;
    private int currentCooldown;
    private int rangeX;
    private int rangeY;
    private int minLevel;
    private boolean selfTarget = false;
    private boolean available = true;

    private int accuracyBonus;
    private int criticalChance;
    private String damageDice;
    private int difficultyClass;

    private static final Dice hitDice = new Dice(1, 20);
    private static final Dice critDice = new Dice(1, 100);

    public Attack(String name, int levelRequired, AttackPattern pattern,
                  int damage, int staminaCost, int cooldown,
                  int rangeX, int rangeY) {
        this(name, levelRequired, pattern, damage, staminaCost, cooldown,
                rangeX, rangeY, 0, 5, "1d6", 10);
    }

    public Attack(String name, int levelRequired, AttackPattern pattern,
                  int damage, int staminaCost, int cooldown,
                  int rangeX, int rangeY, boolean selfTarget) {
        this(name, levelRequired, pattern, damage, staminaCost, cooldown,
                rangeX, rangeY, 0, 5, "1d6", 10);
        this.selfTarget = selfTarget;
    }

    public Attack(String name, int levelRequired, AttackPattern pattern,
                  int baseDamage, int staminaCost, int cooldown,
                  int rangeX, int rangeY,
                  int accuracyBonus, int criticalChance,
                  String damageDice, int difficultyClass) {
        this.pattern = pattern;
        this.name = name;
        this.damage = baseDamage;
        this.staminaCost = staminaCost;
        this.cooldown = cooldown;
        this.currentCooldown = 0;
        this.rangeX = rangeX;
        this.rangeY = rangeY;
        this.minLevel = levelRequired;
        this.accuracyBonus = accuracyBonus;
        this.criticalChance = criticalChance;
        this.damageDice = damageDice;
        this.difficultyClass = difficultyClass;
    }

    public boolean CanUse(Entity entity) {
        return available &&
                entity.getStamina() >= staminaCost &&
                entity.getLevel() >= this.minLevel &&
                currentCooldown == 0;
    }

    public void ReduceCooldown() {
        if (currentCooldown > 0) {
            currentCooldown--;
            if (currentCooldown == 0) {
                available = true;
            }
        }
    }

    public boolean AttackIsSelfTarget() {
        return selfTarget;
    }

    public AttackResult Execute(Entity attacker, Entity target) {
        AttackResult result = new AttackResult();

        Terminal.Box("⚔️ " + attacker.getName() + " VS " + target.getName(), Color.YELLOW);
        Terminal.emptyLines(1);

        System.out.println(Color.CYAN + "🎲 Rolando para acertar..." + Color.RESET);

        int hitRoll = rollD20();
        int attackModifier = attacker.getLucky() + accuracyBonus;
        int totalAttackRoll = hitRoll + attackModifier;
        int targetAC = calculateArmorClass(target);

        System.out.println(String.format(
                "   Dado: %s[%d]%s + Modificador: %d (sorte: %d + precisão: %d) = %s%d%s",
                Color.YELLOW, hitRoll, Color.RESET,
                attackModifier, attacker.getLucky(), accuracyBonus,
                Color.BOLD, totalAttackRoll, Color.RESET
        ));

        System.out.println(String.format(
                "   Classe de Armadura do alvo: %s%d%s",
                Color.CYAN, targetAC, Color.RESET
        ));

        result.hitRoll = hitRoll;
        result.totalAttackRoll = totalAttackRoll;
        result.targetAC = targetAC;

        if (hitRoll == 1) {
            System.out.println(Color.RED + "\n💥 FALHA CRÍTICA!" + Color.RESET);
            result.hit = false;
            result.criticalFailure = true;
            Terminal.Input.WaitConfirm();
            return result;
        }

        boolean naturalCrit = (hitRoll == 20);
        if (naturalCrit) {
            System.out.println(Color.YELLOW + "\n⭐ 20 NATURAL - CRÍTICO AUTOMÁTICO!" + Color.RESET);
        }

        if (totalAttackRoll < targetAC && !naturalCrit) {
            System.out.println(Color.RED + "\n✗ O ataque errou!" + Color.RESET);
            result.hit = false;
            Terminal.Input.WaitConfirm();
            return result;
        }

        System.out.println(Color.GREEN + "✓ Acertou!" + Color.RESET);
        result.hit = true;
        Terminal.emptyLines(1);

        boolean isCritical = naturalCrit;

        if (!naturalCrit) {
            System.out.println(Color.PURPLE + "🎲 Verificando crítico..." + Color.RESET);

            int critRoll = rollD100();
            int totalCritChance = criticalChance + (attacker.getLucky() / 2);

            System.out.printf(
                    "   Dado: %s[%d]%s vs Chance: %d%% (base: %d%% + sorte/2: %d%%)%n",
                    Color.YELLOW, critRoll, Color.RESET,
                    totalCritChance, criticalChance, attacker.getLucky() / 2
            );

            isCritical = critRoll <= totalCritChance;

            if (isCritical) {
                System.out.println(Color.YELLOW + "💥 ACERTO CRÍTICO!" + Color.RESET);
            }
            Terminal.emptyLines(1);
        }

        result.critical = isCritical;

        System.out.println(Color.RED + "💥 Calculando dano..." + Color.RESET);

        int baseDamage = this.damage;
        DiceRollResult damageRoll = rollDamageDice();
        int diceTotal = damageRoll.total;
        int attributeBonus = (int) (attacker.getDamage() * 0.5);
        int totalDamage = baseDamage + diceTotal + attributeBonus;

        if (isCritical) {
            totalDamage *= 2;
        }

        System.out.println(String.format(
                "   Base: %s%d%s + Dados: %s%s = %d%s + Atributo: %s%d%s = %s%d%s%s",
                Color.BOLD, baseDamage, Color.RESET,
                Color.YELLOW, damageRoll.rolls, diceTotal, Color.RESET,
                Color.CYAN, attributeBonus, Color.RESET,
                Color.RED, totalDamage, Color.RESET,
                isCritical ? " " + Color.YELLOW + "(x2 CRÍTICO!)" + Color.RESET : ""
        ));

        double finalDamage = Math.max(1, totalDamage - target.getDefense());

        System.out.println(String.format(
                "   Após defesa (%.0f): %s%.0f de dano%s",
                target.getDefense(),
                Color.RED + Color.BOLD, finalDamage, Color.RESET
        ));

        result.totalDamage = totalDamage;
        result.finalDamage = finalDamage;

        Terminal.emptyLines(1);

        target.TakeDamage(finalDamage);

        System.out.println(String.format(
                "🎯 %s%s%s recebeu %s%.0f%s de dano!",
                Color.BOLD, target.getName(), Color.RESET,
                Color.RED, finalDamage, Color.RESET
        ));

        System.out.println(String.format(
                "   HP: %s%.0f%s/%.0f %s",
                target.getHealth() > target.getHealthMax() * 0.3 ? Color.GREEN : Color.RED,
                target.getHealth(), Color.RESET,
                target.getHealthMax(),
                getHealthBar(target)
        ));

        if (!target.IsAlive()) {
            Terminal.emptyLines(1);
            System.out.println(Color.RED + "💀 " + target.getName() +
                    " foi derrotado!" + Color.RESET);
            result.killed = true;
        }

        Terminal.Input.WaitConfirm();
        return result;
    }

    private int rollD20() {
        return hitDice.rollMultipleDices().getFirst().value;
    }

    private int rollD100() {
        return critDice.rollMultipleDices().getFirst().value;
    }

    private DiceRollResult rollDamageDice() {
        String[] parts = damageDice.split("d");
        int numDice = Integer.parseInt(parts[0]);
        int sides = Integer.parseInt(parts[1]);

        Dice dice = new Dice(numDice, sides);
        var rolls = dice.rollMultipleDices();
        int total = rolls.stream().mapToInt(r -> r.value).sum();

        return new DiceRollResult(rolls.toString(), total);
    }

    private int calculateArmorClass(Entity target) {
        return 10 + (int) target.getDefense() + target.getLevel();
    }

    private String getHealthBar(Entity entity) {
        double percentage = entity.getHealth() / entity.getHealthMax();
        int bars = (int) (percentage * 10);

        String color = percentage > 0.5 ? Color.GREEN :
                percentage > 0.3 ? Color.YELLOW : Color.RED;

        return color + "█".repeat(bars) + "░".repeat(10 - bars) + Color.RESET;
    }

    public void Use() {
        currentCooldown = cooldown;
        if (cooldown > 0) {
            available = false;
        }
    }

    public boolean IsInRange(int originX, int originY, int targetX, int targetY) {
        int distanceX = Math.abs(targetX - originX);
        int distanceY = Math.abs(targetY - originY);
        return distanceX <= rangeX && distanceY <= rangeY;
    }

    public int[][] GetAttackPattern() {
        return pattern.GetPattern();
    }

    public boolean IsMultiStage() {
        return pattern.isMultiStage();
    }

    public String getName() { return name; }
    public AttackPattern getPattern() { return pattern; }
    public int getCooldown() { return cooldown; }
    public int getCurrentCooldown() { return currentCooldown; }
    public int getDamage() { return damage; }
    public int getStaminaCost() { return staminaCost; }
    public int getRangeX() { return rangeX; }
    public int getRangeY() { return rangeY; }
    public int getAccuracyBonus() { return accuracyBonus; }
    public int getCriticalChance() { return criticalChance; }
    public String getDamageDice() { return damageDice; }
    public int getDifficultyClass() { return difficultyClass; }

    @Override
    public int hashCode() {
        return this.name.hashCode() + this.pattern.GetName().hashCode();
    }

    @Override
    public String toString() {
        String cooldownStr = currentCooldown > 0 ?
                currentCooldown + "/" + cooldown :
                String.valueOf(cooldown);
        String statusStr = available ? "✓" : "✗";

        return String.format(
                "%s %s\n" +
                        "├ Dano: %d + %s\n" +
                        "├ Stamina: %d\n" +
                        "├ Precisão: +%d\n" +
                        "├ Crítico: %d%%\n" +
                        "├ Cooldown: %s\n" +
                        "└ Alcance: X:%d Y:%d",
                name, statusStr, damage, damageDice, staminaCost,
                accuracyBonus, criticalChance, cooldownStr, rangeX, rangeY
        );
    }

    public static class AttackResult {
        public boolean hit = false;
        public boolean critical = false;
        public boolean criticalFailure = false;
        public boolean killed = false;
        public int hitRoll = 0;
        public int totalAttackRoll = 0;
        public int targetAC = 0;
        public int totalDamage = 0;
        public double finalDamage = 0;
    }

    private static class DiceRollResult {
        String rolls;
        int total;

        DiceRollResult(String rolls, int total) {
            this.rolls = rolls;
            this.total = total;
        }
    }
}