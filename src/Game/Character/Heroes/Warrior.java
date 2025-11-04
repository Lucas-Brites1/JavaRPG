package Game.Character.Heroes;

import Game.Attacks.Attack;
import Game.Attacks.AttackPattern;
import Game.Character.Entity;

public class Warrior extends Entity {
    public Warrior(String name) {
        super(name, 'G', 150, 80, 50, 15, 5);
        InitializeAttacks();
    }

    public Warrior(Warrior other) {
        super(other);
    }

    private void InitializeAttacks() {
        AddAttack("Golpe Pesado", new Attack(
                "Golpe Pesado - Ataque corpo a corpo devastador",
                1,
                AttackPattern.SINGLE,
                20, 10, 0,
                1, 1,
                3, 15, "2d6", 11
        ));

        AddAttack("Investida", new Attack(
                "Investida - Carga em linha reta",
                1,
                AttackPattern.LINE_H3,
                35, 20, 1,
                3, 1,
                4, 20, "2d8", 13
        ));

        AddAttack("Golpe Circular", new Attack(
                "Golpe Circular - Atinge todos ao redor",
                1,
                AttackPattern.SMALL_CROSS,
                40, 30, 2,
                1, 1,
                2, 25, "3d6", 14
        ));

        AddAttack("Fúria Bárbara", new Attack(
                "Fúria Bárbara - Devastação em área",
                1,
                AttackPattern.SQUARE_3X3,
                55, 50, 3,
                2, 2,
                1, 30, "4d6", 16
        ));
    }

    @Override
    public String toString() {
        return "⚔️ GUERREIRO " + super.toString();
    }
}