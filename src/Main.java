import Game.Story.StoryManager;
import utils.Color;
import utils.Terminal;

public class Main {
    public static void main(String[] args) {
        ShowGameLogo();

        StoryManager story = new StoryManager();
        story.Start();

        Terminal.emptyLines(2);
        Terminal.Success("Obrigado por jogar!");
        Terminal.Close();
    }

    private static void ShowGameLogo() {
        Terminal.Clear();

        Terminal.emptyLines(1);
        Terminal.printlnCentered(Color.yellow("Um RPG Tático de Exploração de Masmorras"));
        Terminal.emptyLines(1);
        Terminal.printlnCentered(Color.style("⚔️ Sistema de Combate por Turnos", Color.BOLD));
        Terminal.printlnCentered(Color.style("🎲 Rolagem de Dados D20", Color.BOLD));
        Terminal.printlnCentered(Color.style("🤖 IA de Inimigos Inteligente", Color.BOLD));
        Terminal.printlnCentered(Color.style("📖 História Interativa", Color.BOLD));

        Terminal.emptyLines(2);
        Terminal.Input.WaitConfirm();
    }
}