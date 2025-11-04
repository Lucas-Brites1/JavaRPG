package Game.Character.Enemies;

import Game.Attacks.Attack;
import Game.Attacks.AttackPattern;
import Game.Character.Entity;

public class Dragon extends Entity {
    public Dragon() {
        super("Dragão Jovem", 'E', 200, 100, 50, 20, 8);
        InitializeAttacks();
    }

    private void InitializeAttacks() {
        AddAttack("Mordida", new Attack(
                "Mordida - Ataque corpo a corpo",
                1,
                AttackPattern.SINGLE,
                30, 15, 0,
                2, 2,
                4, 30, "3d8", 15
        ));

        AddAttack("Sopro de Fogo", new Attack(
                "Sopro de Fogo - Cone de chamas",
                1,
                AttackPattern.LINE_H5,
                45, 40, 2,
                6, 3,
                2, 25, "4d8", 17
        ));

        AddAttack("Cauda Chicoteante", new Attack(
                "Cauda Chicoteante - Ataque em área",
                1,
                AttackPattern.SMALL_CROSS,
                35, 25, 1,
                3, 3,
                1, 20, "3d6", 16
        ));
    }

    @Override
    public String toString() {
        return "🐉 " + super.toString();
    }
}