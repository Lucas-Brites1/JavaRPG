package utils;

/**
 * Classe utilitária para adicionar cores e formatação ao texto do terminal
 * usando códigos ANSI escape.
 */
public class Color {

    // ==================== CORES DE TEXTO ====================
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    // ==================== CORES BRILHANTES ====================
    public static final String BRIGHT_BLACK = "\u001B[90m";
    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_BLUE = "\u001B[94m";
    public static final String BRIGHT_PURPLE = "\u001B[95m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_WHITE = "\u001B[97m";

    // ==================== CORES DE FUNDO ====================
    public static final String BG_BLACK = "\u001B[40m";
    public static final String BG_RED = "\u001B[41m";
    public static final String BG_GREEN = "\u001B[42m";
    public static final String BG_YELLOW = "\u001B[43m";
    public static final String BG_BLUE = "\u001B[44m";
    public static final String BG_PURPLE = "\u001B[45m";
    public static final String BG_CYAN = "\u001B[46m";
    public static final String BG_WHITE = "\u001B[47m";

    // ==================== ESTILOS ====================
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";
    public static final String ITALIC = "\u001B[3m";
    public static final String UNDERLINE = "\u001B[4m";
    public static final String BLINK = "\u001B[5m";
    public static final String REVERSE = "\u001B[7m";
    public static final String HIDDEN = "\u001B[8m";
    public static final String STRIKETHROUGH = "\u001B[9m";

    // ==================== CORES RGB (256 cores) ====================
    public static final String ORANGE = "\u001B[38;5;208m";
    public static final String PINK = "\u001B[38;5;205m";
    public static final String GOLD = "\u001B[38;5;220m";
    public static final String SILVER = "\u001B[38;5;250m";
    public static final String DARK_RED = "\u001B[38;5;124m";
    public static final String DARK_GREEN = "\u001B[38;5;22m";
    public static final String DARK_BLUE = "\u001B[38;5;18m";

    // ==================== MÉTODOS UTILITÁRIOS ====================


    public static String red(String text) {
        return RED + text + RESET;
    }

    public static String green(String text) {
        return GREEN + text + RESET;
    }

    public static String yellow(String text) {
        return YELLOW + text + RESET;
    }

    public static String blue(String text) {
        return BLUE + text + RESET;
    }

    public static String purple(String text) {
        return PURPLE + text + RESET;
    }

    public static String cyan(String text) {
        return CYAN + text + RESET;
    }

    public static String white(String text) {
        return WHITE + text + RESET;
    }

    public static String orange(String text) {
        return ORANGE + text + RESET;
    }

    public static String gold(String text) {
        return GOLD + text + RESET;
    }

    public static String bold(String text) {
        return BOLD + text + RESET;
    }

    public static String underline(String text) {
        return UNDERLINE + text + RESET;
    }

    public static String italic(String text) {
        return ITALIC + text + RESET;
    }

    public static String style(String text, String... styles) {
        StringBuilder sb = new StringBuilder();
        for (String style : styles) {
            sb.append(style);
        }
        sb.append(text);
        sb.append(RESET);
        return sb.toString();
    }

    public static String HPBar(int atual, int maximo, int largura) {
        double porcentagem = (double) atual / maximo;
        int preenchido = (int) (porcentagem * largura);

        String cor;
        if (porcentagem > 0.6) {
            cor = GREEN;
        } else if (porcentagem > 0.3) {
            cor = YELLOW;
        } else {
            cor = RED;
        }

        StringBuilder barra = new StringBuilder("[");
        barra.append(cor);
        for (int i = 0; i < largura; i++) {
            if (i < preenchido) {
                barra.append("█");
            } else {
                barra.append("░");
            }
        }
        barra.append(RESET);
        barra.append("] ");
        barra.append(atual).append("/").append(maximo);

        return barra.toString();
    }

    public static String linha(int tamanho, String cor) {
        StringBuilder sb = new StringBuilder(cor);
        for (int i = 0; i < tamanho; i++) {
            sb.append("═");
        }
        sb.append(RESET);
        return sb.toString();
    }

    public static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void moverCursor(int linha, int coluna) {
        System.out.print("\033[" + linha + ";" + coluna + "H");
    }

    public static void esconderCursor() {
        System.out.print("\033[?25l");
    }

    public static void mostrarCursor() {
        System.out.print("\033[?25h");
    }
}
