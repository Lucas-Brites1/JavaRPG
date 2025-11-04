package Game.Character.Enemies;

import Game.Attacks.Attack;
import Game.Attacks.AttackPattern;
import Game.Character.Entity;

public class Goblin extends Entity {
    public Goblin() {
        super("Goblin", 'E', 40, 25, 12, 5, 6);
        InitializeAttacks();
    }

    private void InitializeAttacks() {
        AddAttack("Facada", new Attack(
                "Facada - Ataque rápido",
                1,
                AttackPattern.SINGLE,
                8, 5, 0,
                1, 1,
                2, 10, "1d4", 10
        ));
    }

    @Override
    public String toString() {
        return "👺 " + super.toString();
    }
}