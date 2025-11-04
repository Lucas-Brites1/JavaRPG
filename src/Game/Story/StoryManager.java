package Game.Story;
import Game.Character.Entity;
import Game.Character.Enemies.*;
import Game.Character.Heroes.*;
import Game.Combat;
import Game.Dice;
import Game.Map.CombatGrid;
import utils.Color;
import utils.Terminal;

import java.util.Random;

public class StoryManager {
    private Entity player;
    private Dice eventDice;
    private Random random;
    private boolean darkKnightDefeated;

    public StoryManager() {
        this.eventDice = new Dice(1, 100);
        this.random = new Random();
        this.darkKnightDefeated = false;
    }

    public void Start() {
        Terminal.Clear();
        Terminal.HideCursor();
        ShowIntro();
        player = CharacterCreation();

        if (player == null) {
            System.out.println(Color.RED + "Você desistiu da aventura..." + Color.RESET);
            return;
        }

        Terminal.Clear();
        ShowMissionBriefing();

        if (!AcceptMission()) {
            System.out.println(Color.YELLOW + "\nVocê decide não aceitar a missão." + Color.RESET);
            System.out.println("Talvez outro aventureiro tenha mais coragem...");
            return;
        }

        Terminal.Clear();
        EnterDungeon();
    }

    private void ShowIntro() {
        Terminal.Box("🗡️  RPG TÁTICO - A MASMORRA  🗡️", Color.CYAN);
        Terminal.emptyLines(2);

        Terminal.TypeWriter("Bem-vindo, aventureiro!", 50);
        Terminal.emptyLines(1);
        Terminal.TypeWriter("Uma masmorra misteriosa surgiu no caminho da Grande Capital...", 30);
        Terminal.TypeWriter("Comerciantes desapareceram. Viajantes falam de monstros.", 30);
        Terminal.TypeWriter("O reino precisa de heróis corajosos!", 30);
        Terminal.emptyLines(2);

        Terminal.Input.WaitConfirm();
    }

    private Entity CharacterCreation() {
        Terminal.Clear();
        Terminal.Box("⚔️ CRIAÇÃO DE PERSONAGEM ⚔️", Color.PURPLE);
        Terminal.emptyLines(1);

        System.out.println("Escolha sua classe:");
        Terminal.emptyLines(1);

        System.out.println(Color.PURPLE + "[1] ⚡ MAGO" + Color.RESET);
        System.out.println("    ├─ HP: 80  | Stamina: 120");
        System.out.println("    ├─ ATK: 30 | DEF: 5");
        System.out.println("    └─ Especialidade: Magias poderosas de longo alcance");
        Terminal.emptyLines(1);

        System.out.println(Color.GREEN + "[2] ⚔️ GUERREIRO" + Color.RESET);
        System.out.println("    ├─ HP: 150 | Stamina: 80");
        System.out.println("    ├─ ATK: 50 | DEF: 15");
        System.out.println("    └─ Especialidade: Combate corpo a corpo devastador");
        Terminal.emptyLines(1);

        System.out.println(Color.CYAN + "[3] 🏹 ARQUEIRO" + Color.RESET);
        System.out.println("    ├─ HP: 100 | Stamina: 100");
        System.out.println("    ├─ ATK: 40 | DEF: 8");
        System.out.println("    └─ Especialidade: Ataques precisos de longa distância");
        Terminal.emptyLines(1);

        System.out.println(Color.YELLOW + "[0] Desistir" + Color.RESET);
        Terminal.emptyLines(1);

        int choice = Terminal.Input.ReadInteger("Escolha sua classe (0-3): ", "Entrada inválida!");

        if (choice == 0) {
            return null;
        }

        String nome = Terminal.Input.ReadString("\nDigite o nome do seu herói: ");
        while (nome.isEmpty() || nome.length() < 3) {
            Terminal.println("Nome de herói inválido, deve conter ao menos 4 letras!", Color.RED);
            nome = Terminal.Input.ReadString("\nDigite o nome do seu herói: ");
        }

        return switch (choice) {
            case 1 -> new Mage(nome);
            case 2 -> new Warrior(nome);
            case 3 -> new Archer(nome);
            default -> {
                Terminal.Error("Opção inválida!");
                yield CharacterCreation();
            }
        };
    }

    private void ShowMissionBriefing() {
        Terminal.Box("📜 MISSÃO: EXPLORAR A MASMORRA", Color.YELLOW);
        Terminal.emptyLines(1);

        Terminal.TypeWriter("O capitão da guarda te convoca:", 30);
        Terminal.emptyLines(1);

        System.out.println(Color.BOLD + "\"" + player.getName() + ", precisamos de você!\"" + Color.RESET);
        Terminal.emptyLines(1);

        Terminal.TypeWriter("\"Uma masmorra surgiu do nada no caminho para a capital.\"", 30);
        Terminal.TypeWriter("\"Comerciantes desapareceram. Dizem que há monstros lá dentro.\"", 30);
        Terminal.TypeWriter("\"Precisamos que alguém investigue e elimine a ameaça.\"", 30);
        Terminal.emptyLines(2);

        System.out.println(Color.YELLOW + "\"Você aceita esta missão?\"" + Color.RESET);
        Terminal.emptyLines(1);
    }

    private boolean AcceptMission() {
        System.out.println("[1] Sim, vou investigar a masmorra!");
        System.out.println("[2] Não, isso é muito perigoso...");
        Terminal.emptyLines(1);

        int choice = Terminal.Input.ReadInteger("Escolha: ", "Entrada inválida!");
        return choice == 1;
    }

    private void EnterDungeon() {
        Terminal.Box("🏰 ENTRADA DA MASMORRA 🏰", Color.RED);
        Terminal.emptyLines(1);

        Terminal.TypeWriter("Você se aproxima da entrada da masmorra...", 30);
        Terminal.TypeWriter("Uma aura sinistra emana do interior.", 30);
        Terminal.TypeWriter("Tudo está escuro, mas você consegue distinguir o grande corredor.", 30);
        Terminal.emptyLines(2);

        Terminal.TypeWriter("À sua frente, você vê:", 30);
        Terminal.emptyLines(1);
        System.out.println(Color.CYAN + "→ DIREITA: Uma sala estreita com uma luz fraca" + Color.RESET);
        System.out.println(Color.PURPLE + "→ ESQUERDA: Uma sala grande completamente escura" + Color.RESET);
        Terminal.emptyLines(1);

        FirstChoice();
    }

    private void FirstChoice() {
        System.out.println("Para onde você quer ir?");
        System.out.println("[1] Sala estreita à direita");
        System.out.println("[2] Sala grande à esquerda");
        System.out.println("[3] Voltar (desistir)");
        Terminal.emptyLines(1);

        int choice = Terminal.Input.ReadInteger("Escolha: ", "Entrada inválida!");

        switch (choice) {
            case 1 -> SmallRoom();
            case 2 -> LargeRoom();
            case 3 -> {
                Terminal.Warning("\nVocê decide que é melhor voltar...");
                System.out.println("A masmorra permanece inexplorada.");
            }
            default -> {
                Terminal.Error("Escolha inválida!");
                FirstChoice();
            }
        }
    }

    private void SmallRoom() {
        Terminal.Clear();
        Terminal.Box("🔦 SALA ESTREITA", Color.CYAN);
        Terminal.emptyLines(1);

        Terminal.TypeWriter("Você entra cautelosamente na sala estreita...", 30);
        Terminal.TypeWriter("A luz vem de tochas nas paredes.", 30);
        Terminal.emptyLines(1);

        int roll = eventDice.rollMultipleDices().getFirst().value;

        if (roll <= 40) {
            player.RecoverStamina(player.getStaminaMax());
            Terminal.Input.WaitConfirm();
            ContinueExploring();
        } else if (roll <= 80) {
            Terminal.TypeWriter("CUIDADO!", 10);
            Terminal.emptyLines(1);
            System.out.println(Color.RED + "👺 2 Goblins te atacam!" + Color.RESET);
            Terminal.Input.WaitConfirm();

            int lines = random.nextInt(3) + 5;
            int cols = random.nextInt(5) + 8;
            StartCombat(lines, cols, new Goblin(), new Goblin());
        } else {
            Terminal.Input.WaitConfirm();
            ContinueExploring();
        }
    }

    private void LargeRoom() {
        Terminal.Clear();
        Terminal.Box("🌑 SALA GRANDE", Color.PURPLE);
        Terminal.emptyLines(1);

        Terminal.TypeWriter("Você entra na sala escura...", 30);
        Terminal.TypeWriter("Seus olhos se adaptam à escuridão.", 30);
        Terminal.TypeWriter("Você percebe formas se movendo!", 30);
        Terminal.emptyLines(2);

        System.out.println(Color.RED + "⚔️ Um Orc Guerreiro e um Goblin Xamã aparecem!" + Color.RESET);
        Terminal.Input.WaitConfirm();

        int lines = random.nextInt(5) + 8;
        int cols = random.nextInt(6) + 12;
        StartCombat(lines, cols, new Orc(), new GoblinShaman());
    }

    private void ContinueExploring() {
        if (!player.IsAlive()) {
            return;
        }

        Terminal.Clear();
        Terminal.Box("🗺️ CORREDOR", Color.YELLOW);
        Terminal.emptyLines(1);

        Terminal.TypeWriter("Você continua explorando a masmorra...", 30);
        Terminal.emptyLines(1);

        System.out.println("O que fazer?");
        if (darkKnightDefeated) {
            System.out.println(Color.RED + "[1] Seguir para o Coração da Masmorra" + Color.RESET);
        } else {
            System.out.println("[1] Seguir em frente");
        }
        System.out.println("[2] Procurar por itens");
        System.out.println("[3] Descansar (recuperar HP/Stamina)");
        System.out.println("[4] Voltar para a entrada");
        Terminal.emptyLines(1);

        int choice = Terminal.Input.ReadInteger("Escolha: ", "Entrada inválida!");

        switch (choice) {
            case 1:
                if (darkKnightDefeated) {
                    DragonsLair();
                } else {
                    DeepDungeon();
                }
                break;
            case 2:
                SearchForItems();
                break;
            case 3:
                Rest();
                break;
            case 4:
                Terminal.Warning("Você retorna à superfície...");
                Terminal.Success("Missão concluída! Você sobreviveu!");
                break;
            default:
                ContinueExploring();
        }
    }

    private void DeepDungeon() {
        Terminal.Clear();
        Terminal.Box("⚔️ PROFUNDEZAS DA MASMORRA", Color.RED);
        Terminal.emptyLines(1);

        Terminal.TypeWriter("Você desce ainda mais fundo...", 30);
        Terminal.TypeWriter("O ar fica mais pesado.", 30);
        Terminal.TypeWriter("Uma presença poderosa está próxima...", 30);
        Terminal.emptyLines(2);

        System.out.println(Color.RED + Color.BOLD + "🗡️ UM CAVALEIRO DAS TREVAS BLOQUEIA SEU CAMINHO!" + Color.RESET);
        Terminal.Input.WaitConfirm();

        int lines = random.nextInt(4) + 10;
        int cols = random.nextInt(5) + 18;
        StartCombat(lines, cols, "DARK_KNIGHT", new DarkKnight());
    }

    private void DragonsLair() {
        Terminal.Clear();
        Terminal.Box("🔥 O COVIL DO DRAGÃO 🔥", Color.ORANGE);
        Terminal.emptyLines(1);

        Terminal.TypeWriter("Você segue por um túnel que se alarga...", 30);
        Terminal.TypeWriter("O calor é intenso. Cinzas caem do teto.", 30);
        Terminal.TypeWriter("Você entra em uma caverna colossal.", 30);
        Terminal.TypeWriter("No centro, dormindo sobre uma montanha de ouro, está ele...", 30);
        Terminal.emptyLines(2);
        Terminal.TypeWriter(Color.RED + Color.BOLD + "UM GIGANTESCO DRAGÃO VERMELHO ACORDA!" + Color.RESET, 50);
        Terminal.emptyLines(1);
        Terminal.Input.WaitConfirm();

        int lines = 20;
        int cols = 30;
        StartCombat(lines, cols, "DRAGON", new Dragon());
    }


    private void SearchForItems() {
        Terminal.Clear();
        Terminal.TypeWriter("Você procura cuidadosamente pela sala...", 30);
        Terminal.emptyLines(1);
        int roll = eventDice.rollMultipleDices().getFirst().value;
        if (roll <= 50) {
            Terminal.Success("Você encontrou uma Poção de Vida!");
            player.Heal(50);
            Terminal.Info("❤️ Recuperou 50 HP!");
        } else {
            System.out.println(Color.style("Não há nada aqui...", Color.DIM));
        }
        Terminal.emptyLines(1);
        Terminal.Input.WaitConfirm();
        ContinueExploring();
    }

    private void Rest() {
        Terminal.Clear();
        Terminal.TypeWriter("Você faz uma pausa para descansar...", 30);
        Terminal.emptyLines(1);
        int roll = eventDice.rollMultipleDices().getFirst().value;

        if (roll <= 70) {
            Terminal.Success("Você descansou em segurança!");
            Terminal.Info("⚡ HP e Stamina recuperados!");
            player.RecoverStamina(player.getStaminaMax());
            player.Heal(player.getHealthMax() * 0.5);
            Terminal.emptyLines(1);
            Terminal.Input.WaitConfirm();
            ContinueExploring();
        } else {
            Terminal.Warning("Você foi atacado durante o descanso!");
            Terminal.Input.WaitConfirm();
            int lines = random.nextInt(3) + 6; // 6-8 linhas
            int cols = random.nextInt(5) + 10; // 10-14 colunas
            StartCombat(lines, cols, new Skeleton(), new Skeleton());
        }
    }

    private void ShowGameVictory() {
        Terminal.Clear();
        Terminal.Box("🎉 VOCÊ VENCEU! 🎉", Color.GOLD);
        Terminal.emptyLines(2);

        Terminal.TypeWriter("Com o último golpe, o grande dragão tomba...", 40);
        Terminal.TypeWriter("O silêncio ecoa pela caverna, quebrado apenas pelo tilintar do tesouro.", 30);
        Terminal.TypeWriter("A ameaça da masmorra foi eliminada.", 30);
        Terminal.TypeWriter("Seu nome, " + player.getName() + ", será cantado por bardos por gerações!", 30);
        Terminal.emptyLines(2);
        Terminal.TypeWriter("Obrigado por jogar!", 50);
        Terminal.emptyLines(1);
        Terminal.Input.WaitConfirm();
    }

    private void StartCombat(int gridLines, int gridColumns, String bossType, Entity... enemies) {
        CombatGrid grid = new CombatGrid(gridLines, gridColumns);
        Combat combat = new Combat(grid, player);

        int playerX = random.nextInt(gridLines);
        int playerY = random.nextInt(gridColumns / 4);
        combat.SetPlayerPosition(playerX, playerY);

        for (int i = 0; i < enemies.length; i++) {
            int enemyX, enemyY;
            int attempts = 0;

            do {
                enemyX = random.nextInt(gridLines);
                enemyY = (gridColumns / 2) + random.nextInt(gridColumns / 2);
                attempts++;

            } while (!grid.PositionEmpty(enemyX, enemyY) && attempts < 50);
            combat.AddEnemy(enemies[i], enemyX, enemyY);
        }

        boolean victory = combat.Start();

        if (victory) {
            if ("DARK_KNIGHT".equals(bossType)) {
                Terminal.Info("A presença sombria se dissipa...");
                darkKnightDefeated = true;
            } else if ("DRAGON".equals(bossType)) {
                ShowGameVictory();
                return;
            }

            Terminal.emptyLines(1);
            System.out.println(Color.GREEN + "🎉 Você venceu o combate!" + Color.RESET);
            System.out.println(Color.YELLOW + "💰 +100 Ouro" + Color.RESET);
            System.out.println(Color.CYAN + "⭐ +50 XP" + Color.RESET);
            Terminal.emptyLines(1);
            Terminal.Input.WaitConfirm();
            ContinueExploring();
        }
        else if (player.IsAlive()) {
            Terminal.emptyLines(1);
            System.out.println(Color.YELLOW + "🏃 Você fugiu da batalha e retornou ao corredor..." + Color.RESET);
            Terminal.emptyLines(1);
            Terminal.Input.WaitConfirm();
            ContinueExploring();
        }
        else {
            Terminal.emptyLines(1);
            System.out.println(Color.RED + "💀 Você foi derrotado..." + Color.RESET);
            System.out.println(Color.PURPLE + "GAME OVER" + Color.RESET);
            Terminal.emptyLines(1);
            Terminal.Input.WaitConfirm();
        }
    }

    private void StartCombat(int gridLines, int gridColumns, Entity... enemies) {
        StartCombat(gridLines, gridColumns, null, enemies);
    }

    private void StartCombat(Entity... enemies) {
        StartCombat(8, 20, null, enemies);
    }
}