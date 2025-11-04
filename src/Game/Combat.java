package Game;

import Game.Attacks.Attack;
import Game.Attacks.AttackPattern;
import Game.Character.Entity;
import Game.Map.CombatGrid;
import utils.Color;
import utils.Terminal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Combat {
    private CombatGrid grid;
    private Entity player;
    private List<Entity> mobs;
    private boolean combatActive;
    private TurnSystem turnSystem;
    private Dice fleeDice;

    public Combat(CombatGrid grid, Entity player) {
        this.grid = grid;
        this.player = player;
        this.mobs = new ArrayList<>();
        this.combatActive = false;
        this.turnSystem = new TurnSystem();
        this.fleeDice = new Dice(1, 20);
    }

    public boolean AddEnemy(Entity enemy, int posX, int posY) {
        if (!grid.ValidPosition(posX, posY)) {
            return false;
        }

        enemy.SetPosition(posX, posY);
        mobs.add(enemy);
        grid.Set(posX, posY, enemy.getSymbol());
        return true;
    }

    public boolean SetPlayerPosition(int posX, int posY) {
        if (!grid.ValidPosition(posX, posY)) {
            return false;
        }
        player.SetPosition(posX, posY);
        grid.Set(posX, posY, player.getSymbol());
        return true;
    }

    public boolean Start() {
        if (mobs.isEmpty()) {
            System.err.println(Color.RED + "❌ Não há inimigos!" + Color.RESET);
            return false;
        }

        turnSystem.Initialize(player, mobs);
        combatActive = true;

        Terminal.Clear();
        Terminal.Box("⚔️ COMBATE INICIADO! ⚔️", Color.RED);
        Terminal.emptyLines(1);
        grid.DrawMap();
        Terminal.Input.WaitConfirm();

        while (combatActive) {
            Entity currentEntity = turnSystem.GetCurrentEntity();

            if (currentEntity == null) {
                break;
            }

            if (turnSystem.IsPlayerTurn(player)) {
                playerTurn();

                if (!combatActive) break;
            } else {
                enemyTurn(currentEntity);
            }

            updateEntitiesState();

            combatActive = !isCombatEnded();

            if (!combatActive) {
                showCombatResult();
                break;
            }

            turnSystem.AdvanceTurn();
        }

        return mobs.isEmpty();
    }

    private void playerTurn() {
        Terminal.Clear();
        Terminal.Box("⚔️ SEU TURNO ⚔️", Color.CYAN);
        Terminal.emptyLines(1);

        showPlayerStatus();
        Terminal.emptyLines(1);
        grid.DrawMap();
        Terminal.emptyLines(1);

        System.out.println(Color.YELLOW + "O que fazer?" + Color.RESET);
        System.out.println("1. Atacar");
        System.out.println("2. Mover");
        System.out.println("3. Fugir");
        System.out.println("4. Pular turno");
        Terminal.emptyLines(1);

        int choice = Terminal.Input.ReadInteger("Escolha: ", "Entrada inválida!");

        switch (choice) {
            case 1 -> playerAttack();
            case 2 -> playerMove();
            case 3 -> {
                if (attemptFlee()) {
                    combatActive = false;
                }
            }
            case 4 -> {
                System.out.println(Color.YELLOW + "Turno pulado..." + Color.RESET);
                Terminal.Input.WaitConfirm();
            }
            default -> {
                System.out.println(Color.RED + "Opção inválida!" + Color.RESET);
                Terminal.Input.WaitConfirm();
                playerTurn();
            }
        }
    }

    private void playerAttack() {
        Attack selectedAttack = selectAttack();

        int[] targetPos = selectTarget(selectedAttack);
        if (targetPos == null) {
            return;
        }

        Terminal.Clear();
        Terminal.Box("🎯 PREVIEW DO ATAQUE", Color.YELLOW);
        grid.PreviewAttackOnMap(targetPos[0], targetPos[1], selectedAttack.getPattern());

        System.out.println("Confirme o ataque ou cancele-o!");
        System.out.println("0. Cancelar habilidade");
        System.out.println("1. Atacar");
        int choice = Terminal.Input.ReadInteger("Escolha: ", "Entrada inválida!");

        if (choice == 0) {
            playerTurn();
        } else if (choice == 1) {
            Terminal.Clear();
            executePlayerAttack(selectedAttack, targetPos[0], targetPos[1]);
        } else {
            System.out.println("Opção inválida, tente novamente\n");
            playerAttack();
        }
    }

    private void playerMove() {
        Terminal.Clear();
        Terminal.Box("🚶 MOVIMENTO", Color.CYAN);
        Terminal.emptyLines(1);
        grid.DrawMap();
        Terminal.emptyLines(1);

        System.out.println(Color.YELLOW + "Posição atual: (" + player.getPosX() + ", " + player.getPosY() + ")" + Color.RESET);
        Terminal.emptyLines(1);

        int newX = Terminal.Input.ReadInteger("Nova LINHA (-1 para cancelar): ", "");
        if (newX == -1) {
            playerMove();
            return;
        }

        int newY = Terminal.Input.ReadInteger("Nova COLUNA (-1 para cancelar): ", "");
        if (newY == -1) {
            playerMove();
            return;
        }

        if (!grid.ValidPosition(newX, newY)) {
            System.out.println(Color.RED + "❌ Posição inválida!" + Color.RESET);
            Terminal.Input.WaitConfirm();
            return;
        }

        if (!grid.PositionEmpty(newX, newY)) {
            System.out.println(Color.RED + "❌ Posição ocupada!" + Color.RESET);
            Terminal.Input.WaitConfirm();
            return;
        }

        int distance = Math.abs(newX - player.getPosX()) + Math.abs(newY - player.getPosY());
        if (distance > 2) {
            System.out.println(Color.RED + "❌ Muito longe! Você pode se mover até 2 casas." + Color.RESET);
            Terminal.Input.WaitConfirm();
            return;
        }

        grid.Move(player.getPosX(), player.getPosY(), newX, newY);
        player.SetPosition(newX, newY);

        System.out.println(Color.GREEN + "✓ Movido para (" + newX + ", " + newY + ")" + Color.RESET);
        Terminal.Input.WaitConfirm();
    }

    private void enemyTurn(Entity enemy) {
        Terminal.Clear();
        Terminal.Box("⚔️ TURNO DE " + enemy.getName().toUpperCase() + " ⚔️", Color.RED);
        Terminal.emptyLines(1);

        showEnemyStatus(enemy);
        Terminal.emptyLines(1);
        grid.DrawMap();
        Terminal.emptyLines(1);

        EnemyAI.Action action = EnemyAI.DecideAction(enemy, player, grid);

        switch (action.type) {
            case ATTACK -> {
                Terminal.TypeWriter(Color.RED + enemy.getName() + " está atacando!" + Color.RESET, 50);
                Terminal.emptyLines(1);
                Terminal.Input.WaitConfirm();

                Terminal.Clear();
                Terminal.Box("🎯 PREVIEW DO ATAQUE INIMIGO", Color.RED);
                grid.PreviewAttackOnMap(action.targetX, action.targetY, action.attack.getPattern());
                Terminal.Input.WaitConfirm();

                Terminal.Clear();
                action.attack.Execute(enemy, player);

                enemy.ConsumeResources(action.attack);
            }
            case MOVE -> {
                Terminal.TypeWriter(Color.YELLOW + enemy.getName() + " está se aproximando..." + Color.RESET, 50);
                Terminal.emptyLines(1);

                grid.Move(enemy.getPosX(), enemy.getPosY(), action.targetX, action.targetY);
                enemy.SetPosition(action.targetX, action.targetY);

                grid.DrawMap();
                Terminal.Input.WaitConfirm();
            }
            case FLEE -> {
                Terminal.TypeWriter(Color.YELLOW + enemy.getName() + " está tentando fugir!" + Color.RESET, 50);
                Terminal.emptyLines(1);

                if (enemyAttemptFlee(enemy)) {
                    mobs.remove(enemy);
                    turnSystem.RemoveEntity(enemy);
                    grid.ClearPosition(enemy.getPosX(), enemy.getPosY());
                    System.out.println(Color.GREEN + "✓ " + enemy.getName() + " fugiu!" + Color.RESET);
                } else {
                    System.out.println(Color.RED + "✗ " + enemy.getName() + " não conseguiu fugir!" + Color.RESET);
                }

                Terminal.Input.WaitConfirm();
            }
            case WAIT -> {
                Terminal.TypeWriter(Color.PURPLE + enemy.getName() + " está esperando..." + Color.RESET, 50);
                Terminal.emptyLines(1);
                Terminal.Input.WaitConfirm();
            }
        }
    }

    private Attack selectAttack() {
        Terminal.Box("📜 SELECIONE UMA HABILIDADE", Color.PURPLE);
        Terminal.emptyLines(1);

        Map<String, Attack> attacks = player.getAttacks();
        List<String> attackKeys = new ArrayList<>(attacks.keySet());

        for (int i = 0; i < attackKeys.size(); i++) {
            String key = attackKeys.get(i);
            Attack attack = attacks.get(key);

            boolean canUse = attack.CanUse(player);
            String statusColor = canUse ? Color.GREEN : Color.RED;
            String statusSymbol = canUse ? "✓" : "✗";

            System.out.println(statusColor + "[" + (i + 1) + "] " + statusSymbol + " " +
                    Color.BOLD + key + Color.RESET);
            System.out.println("    ├─ " + Color.YELLOW + "Dano: " + attack.getDamage() +
                    " + " + attack.getDamageDice() + Color.RESET);
            System.out.println("    ├─ " + Color.CYAN + "Stamina: " + attack.getStaminaCost() + Color.RESET);
            System.out.println("    └─ " + Color.PURPLE + "Alcance: X:" +
                    attack.getRangeX() + " Y:" + attack.getRangeY() + Color.RESET);
            Terminal.emptyLines(1);
        }

        System.out.println(Color.YELLOW + "[0] Cancelar" + Color.RESET);
        Terminal.emptyLines(1);

        int choice = Terminal.Input.ReadInteger("Escolha (0-" + attackKeys.size() + "): ", 0, attackKeys.size());

        if (choice == 0) playerTurn();
        if (choice < 1 || choice > attackKeys.size()) {
            System.out.println(Color.RED + "❌ Opção inválida!" + Color.RESET);
            Terminal.Input.WaitConfirm();
            return selectAttack();
        }

        Attack selected = attacks.get(attackKeys.get(choice - 1));

        if (!selected.CanUse(player)) {
            System.out.println(Color.RED + "❌ Você não pode usar esta habilidade!" + Color.RESET);
            Terminal.Input.WaitConfirm();
            return selectAttack();
        }

        return selected;
    }

    private int[] selectTarget(Attack attack) {
        Terminal.Clear();
        Terminal.Box("🎯 SELECIONE O ALVO", Color.YELLOW);
        Terminal.emptyLines(1);
        grid.DrawMap();
        Terminal.emptyLines(1);

        int targetX = Terminal.Input.ReadInteger("Digite a LINHA (-1 para cancelar): ", "");
        if (targetX == -1) {
            playerTurn();
            return null;
        }

        int targetY = Terminal.Input.ReadInteger("Digite a COLUNA (-1 para cancelar): ", "");
        if (targetY == -1) {
            playerTurn();
            return null;
        }

        if (!grid.ValidPosition(targetX, targetY)) {
            System.out.println(Color.RED + "❌ Posição inválida!" + Color.RESET);
            Terminal.Input.WaitConfirm();
            return selectTarget(attack);
        }

        if (!attack.IsInRange(player.getPosX(), player.getPosY(), targetX, targetY)) {
            System.out.println(Color.RED + "❌ Fora do alcance!" + Color.RESET);
            Terminal.Input.WaitConfirm();
            return selectTarget(attack);
        }

        return new int[]{targetX, targetY};
    }

    private void executePlayerAttack(Attack attack, int targetX, int targetY) {
        List<Entity> hitEnemies = getHitEnemies(attack, targetX, targetY);

        if (!hitEnemies.isEmpty()) {
            for (Entity enemy : hitEnemies) {
                attack.Execute(player, enemy);
                Terminal.emptyLines(1);
            }
        } else {
            Terminal.TypeWriter(Color.YELLOW + "💨 Nenhum inimigo foi atingido!" + Color.RESET, 50);
        }

        player.ConsumeResources(attack);
        Terminal.emptyLines(1);
        grid.DrawMap();
        Terminal.Input.WaitConfirm();
    }

    private List<Entity> getHitEnemies(Attack attack, int targetX, int targetY) {
        List<Entity> hitEnemies = new ArrayList<>();

        AttackPattern attackPattern = attack.getPattern();

        if (attackPattern.isMultiStage()) {
            for (AttackPattern.AttackStage stage : attackPattern.GetAllStages()) {
                int[][] pattern = stage.getPattern();

                for (int i = 0; i < pattern.length; i++) {
                    for (int j = 0; j < pattern[i].length; j++) {
                        if (pattern[i][j] == 1) {
                            int hitX = targetX + i;
                            int hitY = targetY + j;

                            for (Entity mob : mobs) {
                                if (mob.getPosX() == hitX && mob.getPosY() == hitY) {
                                    if (!hitEnemies.contains(mob)) {
                                        hitEnemies.add(mob);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            int[][] pattern = attackPattern.GetPattern();
            for (int i = 0; i < pattern.length; i++) {
                for (int j = 0; j < pattern[i].length; j++) {
                    if (pattern[i][j] == 1) {
                        int hitX = targetX + i;
                        int hitY = targetY + j;

                        for (Entity mob : mobs) {
                            if (mob.getPosX() == hitX && mob.getPosY() == hitY) {
                                if (!hitEnemies.contains(mob)) {
                                    hitEnemies.add(mob);
                                }
                            }
                        }
                    }
                }
            }
        }

        return hitEnemies;
    }

    private boolean attemptFlee() {
        Terminal.Clear();
        Terminal.Box("🏃 TENTANDO FUGIR", Color.YELLOW);
        Terminal.emptyLines(1);

        int playerRoll = fleeDice.rollMultipleDices().getFirst().value;
        int playerTotal = playerRoll + player.getLucky();

        Entity fastestEnemy = mobs.stream()
                .max((e1, e2) -> Integer.compare(e1.getLucky(), e2.getLucky()))
                .orElse(mobs.getFirst());

        int enemyRoll = fleeDice.rollMultipleDices().getFirst().value;
        int enemyTotal = enemyRoll + fastestEnemy.getLucky();

        System.out.println(String.format("%s🎲 %s: [%d] + %d = %d%s",
                Color.CYAN, player.getName(), playerRoll, player.getLucky(), playerTotal, Color.RESET));

        System.out.println(String.format("%s🎲 %s: [%d] + %d = %d%s",
                Color.RED, fastestEnemy.getName(), enemyRoll, fastestEnemy.getLucky(), enemyTotal, Color.RESET));

        Terminal.emptyLines(1);

        boolean escaped = playerTotal > enemyTotal;

        if (escaped) {
            System.out.println(Color.GREEN + "✓ Você conseguiu fugir!" + Color.RESET);
        } else {
            System.out.println(Color.RED + "✗ Não conseguiu fugir!" + Color.RESET);
        }

        Terminal.Input.WaitConfirm();
        return escaped;
    }

    private boolean enemyAttemptFlee(Entity enemy) {
        int enemyRoll = fleeDice.rollMultipleDices().getFirst().value;
        int enemyTotal = enemyRoll + enemy.getLucky();

        int playerRoll = fleeDice.rollMultipleDices().getFirst().value;
        int playerTotal = playerRoll + player.getLucky();

        return enemyTotal > playerTotal;
    }

    private void updateEntitiesState() {
        mobs.removeIf(mob -> {
            if (!mob.IsAlive()) {
                grid.ClearPosition(mob.getPosX(), mob.getPosY());
                turnSystem.RemoveEntity(mob);
                return true;
            }
            return false;
        });
    }

    private boolean isCombatEnded() {
        return mobs.isEmpty() || !player.IsAlive();
    }

    private void showCombatResult() {
        Terminal.emptyLines(2);
        if (mobs.isEmpty()) {
            Terminal.Box("🎉 VITÓRIA! 🎉", Color.GREEN);
        } else {
            Terminal.Box("💀 DERROTA... 💀", Color.RED);
        }
    }

    private void showPlayerStatus() {
        System.out.println(Color.BOLD + "╔════════════════════════════════════╗" + Color.RESET);
        System.out.println(Color.BOLD + "║  " + player.getName() + Color.RESET);
        System.out.println(Color.BOLD + "╠════════════════════════════════════╣" + Color.RESET);
        System.out.println(String.format("║  ❤️  HP: %.0f/%.0f",
                player.getHealth(), player.getHealthMax()));
        System.out.println(String.format("║  ⚡ Stamina: %.0f/%.0f",
                player.getStamina(), player.getStaminaMax()));
        System.out.println(String.format("║  📍 Posição: (%d, %d)",
                player.getPosX(), player.getPosY()));
        System.out.println(Color.BOLD + "╚════════════════════════════════════╝" + Color.RESET);
    }

    private void showEnemyStatus(Entity enemy) {
        System.out.println(Color.RED + "╔════════════════════════════════════╗" + Color.RESET);
        System.out.println(Color.RED + "║  " + enemy.getName() + Color.RESET);
        System.out.println(Color.RED + "╠════════════════════════════════════╣" + Color.RESET);
        System.out.println(String.format("║  ❤️  HP: %.0f/%.0f",
                enemy.getHealth(), enemy.getHealthMax()));
        System.out.println(String.format("║  ⚡ Stamina: %.0f/%.0f",
                enemy.getStamina(), enemy.getStaminaMax()));
        System.out.println(String.format("║  📍 Posição: (%d, %d)",
                enemy.getPosX(), enemy.getPosY()));
        System.out.println(Color.RED + "╚════════════════════════════════════╝" + Color.RESET);
    }

    public void Map() {
        this.grid.DrawMap();
    }
}