package Game.Character.Enemies;

import Game.Attacks.Attack;
import Game.Attacks.AttackPattern;
import Game.Character.Entity;

public class Orc extends Entity {
    public Orc() {
        super("Orc Guerreiro", 'E', 80, 35, 25, 10, 5);
        InitializeAttacks();
    }

    private void InitializeAttacks() {
        AddAttack("Machadada", new Attack(
                "Machadada - Golpe poderoso",
                1,
                AttackPattern.SINGLE,
                18, 10, 0,
                1, 1,
                1, 20, "2d6", 12
        ));

        AddAttack("Investida Selvagem", new Attack(
                "Investida Selvagem",
                1,
                AttackPattern.LINE_H3,
                22, 15, 1,
                3, 1,
                0, 15, "2d4", 13
        ));
    }

    @Override
    public String toString() {
        return "⚔️ " + super.toString();
    }
}