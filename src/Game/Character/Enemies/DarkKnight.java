package Game.Character.Enemies;

import Game.Attacks.Attack;
import Game.Attacks.AttackPattern;
import Game.Character.Entity;

public class DarkKnight extends Entity {
    public DarkKnight() {
        super("Cavaleiro das Trevas", 'E', 100, 50, 32, 14.5, 4);
        InitializeAttacks();
    }

    private void InitializeAttacks() {
        AddAttack("Corte Sombrio", new Attack(
                "Corte Sombrio - Ataque poderoso",
                1,
                AttackPattern.SINGLE,
                25, 0, 1,
                1, 1,
                3, 18, "3d6", 14
        ));

        AddAttack("Onda Negra", new Attack(
                "Onda Negra - Ataque em cruz",
                1,
                AttackPattern.SMALL_CROSS,
                30, 20, 2,
                2, 2,
                2, 20, "2d8", 15
        ));

        AddAttack("Devastação", new Attack(
                "Devastação - Ultimate",
                1,
                AttackPattern.SQUARE_3X3,
                40, 35, 3,
                2, 2,
                1, 30, "4d6", 16
        ));
    }

    @Override
    public String toString() {
        return "🗡️ " + super.toString();
    }
}
