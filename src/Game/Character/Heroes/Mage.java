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
        AddAttack("Bola de Fogo", new Attack(
                "Bola de Fogo - Explosão flamejante",
                1,
                AttackPattern.SQUARE_2X2,
                10, 12, 1,
                3, 8,
                3, 19, "1d6", 6
        ));

        AddAttack("Chamuscar", new Attack(
                "Chamuscar - Rajada de chamas rápida",
                1,
                AttackPattern.SINGLE,
                12, 16, 2,
                4, 6,
                6, 22, "2d6", 12
        ));

        AddAttack("Raio Arcano", new Attack(
                "Raio Arcano - Descarga elétrica em linha",
                1,
                AttackPattern.LINE_H5,
                38, 28, 2,
                5, 7,
                5, 25, "3d4", 15
        ));

        AddAttack("Meteoro", new Attack(
                "Meteoro - Chuva de meteoros devastadora",
                1,
                AttackPattern.METEOR_STRIKE,
                55, 70, 4,
                6, 8,
                3, 30, "4d6", 17
        ));
    }

    @Override
    public String toString() {
        return "⚡ MAGO " + super.toString();
    }
}