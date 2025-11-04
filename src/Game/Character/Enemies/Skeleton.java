package Game.Character.Enemies;

import Game.Attacks.Attack;
import Game.Attacks.AttackPattern;
import Game.Character.Entity;

public class Skeleton extends Entity {
    public Skeleton() {
        super("Esqueleto", 'E', 35, 20, 10, 8, 4);
        InitializeAttacks();
    }

    private void InitializeAttacks() {
        AddAttack("Espadada", new Attack(
                "Espadada - Ataque básico",
                1,
                AttackPattern.SINGLE,
                10, 5, 0,
                1, 1,
                1, 8, "1d6", 10
        ));
    }

    @Override
    public String toString() {
        return "💀 " + super.toString();
    }
}
