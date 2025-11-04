package Game.Character.Heroes;

import Game.Attacks.Attack;
import Game.Attacks.AttackPattern;
import Game.Character.Entity;

public class Mage extends Entity {
    public Mage(String name) {
        super(name, 'M', 80, 120, 30, 5, 8);
        InitializeAttacks();
    }

    public Mage(Mage other) {
        super(other);
    }

    private void InitializeAttacks() {
        AddAttack("Chamuscar", new Attack(
                "Chamuscar - Rajada de chamas rápida",
                1,
                AttackPattern.SINGLE,
                12, 8, 0,
                4, 4,
                4, 18, "1d6", 12
        ));

        AddAttack("Bola de Fogo", new Attack(
                "Bola de Fogo - Explosão flamejante",
                1,
                AttackPattern.SQUARE_2X2,
                30, 20, 1,
                5, 4,
                3, 22, "2d6", 14
        ));

        AddAttack("Raio Arcano", new Attack(
                "Raio Arcano - Descarga elétrica em linha",
                1,
                AttackPattern.LINE_H5,
                38, 28, 2,
                7, 2,
                5, 25, "3d4", 15
        ));

        AddAttack("Meteoro", new Attack(
                "Meteoro - Chuva de meteoros devastadora",
                1,
                AttackPattern.METEOR_STRIKE,
                55, 70, 4,
                6, 6,
                1, 28, "4d6", 17
        ));
    }

    @Override
    public String toString() {
        return "⚡ MAGO " + super.toString();
    }
}