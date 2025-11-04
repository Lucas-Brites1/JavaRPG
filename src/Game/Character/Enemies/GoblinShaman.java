package Game.Character.Enemies;

import Game.Attacks.Attack;
import Game.Attacks.AttackPattern;
import Game.Character.Entity;

public class GoblinShaman extends Entity {
    public GoblinShaman() {
        super("Goblin Xamã", 'E', 50, 40, 15, 7, 7);
        InitializeAttacks();
    }

    private void InitializeAttacks() {
        AddAttack("Bola de Fogo Menor", new Attack(
                "Bola de Fogo Menor",
                1,
                AttackPattern.SQUARE_2X2,
                15, 12, 1,
                4, 4,
                1, 15, "1d6", 12
        ));

        AddAttack("Raio Sombrio", new Attack(
                "Raio Sombrio",
                1,
                AttackPattern.LINE_H3,
                12, 10, 0,
                5, 2,
                2, 12, "1d4", 11
        ));
    }

    @Override
    public String toString() {
        return "🔮 " + super.toString();
    }
}
