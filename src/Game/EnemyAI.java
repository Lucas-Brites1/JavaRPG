package Game;

import Game.Attacks.Attack;
import Game.Character.Entity;
import Game.Map.CombatGrid;

import java.util.ArrayList;
import java.util.List;

public class EnemyAI {

    public enum ActionType {
        ATTACK,
        MOVE,
        FLEE,
        WAIT
    }

    public static class Action {
        public ActionType type;
        public Attack attack;
        public int targetX;
        public int targetY;

        public Action(ActionType type) {
            this.type = type;
        }

        public Action(ActionType type, int targetX, int targetY) {
            this.type = type;
            this.targetX = targetX;
            this.targetY = targetY;
        }

        public Action(ActionType type, Attack attack, int targetX, int targetY) {
            this.type = type;
            this.attack = attack;
            this.targetX = targetX;
            this.targetY = targetY;
        }
    }

    public static Action DecideAction(Entity enemy, Entity player, CombatGrid grid) {
        double healthPercentage = enemy.getHealth() / enemy.getHealthMax();
        int distance = calculateDistance(enemy, player);

        if (healthPercentage < 0.25) {
            return new Action(ActionType.FLEE);
        }

        Attack bestAttack = findBestUsableAttack(enemy, player);

        if (bestAttack != null) {
            int[] attackPos = findBestAttackPosition(enemy, player, bestAttack);
            return new Action(ActionType.ATTACK, bestAttack, attackPos[0], attackPos[1]);
        }

        if (distance > 1) {
            int[] movePos = findMoveTowardsPlayer(enemy, player, grid);
            if (movePos != null) {
                return new Action(ActionType.MOVE, movePos[0], movePos[1]);
            }
        }

        return new Action(ActionType.WAIT);
    }

    private static Attack findBestUsableAttack(Entity enemy, Entity player) {
        List<Attack> usableAttacks = new ArrayList<>();

        for (Attack attack : enemy.GetAllAttacks()) {
            if (attack.CanUse(enemy) && isPlayerInRange(enemy, player, attack)) {
                usableAttacks.add(attack);
            }
        }

        if (usableAttacks.isEmpty()) {
            return null;
        }

        Attack bestAttack = null;
        int highestPriority = -1;

        for (Attack attack : usableAttacks) {
            int priority = calculateAttackPriority(enemy, attack);

            if (priority > highestPriority) {
                highestPriority = priority;
                bestAttack = attack;
            }
        }

        return bestAttack;
    }

    private static int calculateAttackPriority(Entity enemy, Attack attack) {
        int priority = 0;

        priority += attack.getDamage();

        if (enemy.getStamina() > enemy.getStaminaMax() * 0.7) {
            priority += attack.getStaminaCost() / 2;
        }

        priority -= attack.getStaminaCost() / 5;

        if (attack.getCurrentCooldown() == 0) {
            priority += 10;
        }

        return priority;
    }

    private static boolean isPlayerInRange(Entity enemy, Entity player, Attack attack) {
        return attack.IsInRange(
                enemy.getPosX(), enemy.getPosY(),
                player.getPosX(), player.getPosY()
        );
    }

    private static int[] findBestAttackPosition(Entity enemy, Entity player, Attack attack) {
        return new int[]{player.getPosX(), player.getPosY()};
    }

    private static int[] findMoveTowardsPlayer(Entity enemy, Entity player, CombatGrid grid) {
        int enemyX = enemy.getPosX();
        int enemyY = enemy.getPosY();
        int playerX = player.getPosX();
        int playerY = player.getPosY();

        List<int[]> possibleMoves = new ArrayList<>();

        int[][] directions = {
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1},
                {-1, -1},
                {-1, 1},
                {1, -1},
                {1, 1}
        };

        for (int[] dir : directions) {
            int newX = enemyX + dir[0];
            int newY = enemyY + dir[1];

            if (grid.ValidPosition(newX, newY) && grid.PositionEmpty(newX, newY)) {
                if (!(newX == playerX && newY == playerY)) {
                    possibleMoves.add(new int[]{newX, newY});
                }
            }
        }

        if (possibleMoves.isEmpty()) {
            return null;
        }

        int[] bestMove = null;
        int shortestDistance = Integer.MAX_VALUE;

        for (int[] move : possibleMoves) {
            int dist = Math.abs(move[0] - playerX) + Math.abs(move[1] - playerY);

            if (dist < shortestDistance) {
                shortestDistance = dist;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private static int calculateDistance(Entity entity1, Entity entity2) {
        return Math.abs(entity1.getPosX() - entity2.getPosX()) +
                Math.abs(entity1.getPosY() - entity2.getPosY());
    }
}