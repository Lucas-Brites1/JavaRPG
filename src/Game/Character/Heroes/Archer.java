package Game.Character.Heroes;

import Game.Attacks.Attack;
import Game.Attacks.AttackPattern;
import Game.Character.Entity;

public class Archer extends Entity {
    public Archer(String name) {
        super(name, 'A', 100, 100, 40, 8, 10);
        InitializeAttacks();
    }

    public Archer(Archer other) {
        super(other);
    }

    private void InitializeAttacks() {
        AddAttack("Tiro Rápido", new Attack(
                "Tiro Rápido - Disparo básico preciso",
                1,
                AttackPattern.SINGLE,
                15, 8, 0,
                5, 5,
                5, 20, "1d6", 12
        ));

        AddAttack("Tiro Duplo", new Attack(
                "Tiro Duplo - Dois alvos em linha",
                1,
                AttackPattern.LINE_H3,
                25, 15, 1,
                6, 3,
                4, 25, "2d4", 13
        ));

        AddAttack("Flecha Perfurante", new Attack(
                "Flecha Perfurante - Atravessa inimigos",
                1,
                AttackPattern.LINE_V5,
                35, 25, 2,
                8, 2,
                6, 30, "2d6", 15
        ));

        AddAttack("Chuva de Flechas", new Attack(
                "Chuva de Flechas - Ataque em área devastador",
                1,
                AttackPattern.ARROW_RAIN,
                50, 60, 4,
                7, 7,
                2, 35, "3d6", 16
        ));
    }

    @Override
    public String toString() {
        return "🏹 ARQUEIRO " + super.toString();
    }
}