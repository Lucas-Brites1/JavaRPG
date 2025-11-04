package utils;

import java.util.Scanner;

public class Terminal {
    public static class Input {
        public static final Scanner scanner = new Scanner(System.in);

        public static String ReadString(String prompt) {
            System.out.print(prompt);
            return scanner.nextLine();
        }

        public static String SReadString(String prompt, String color) {
            System.out.print(color + prompt + Color.RESET);
            return scanner.nextLine();
        }

        public static int ReadInteger(String prompt, String error) {
            System.out.print(prompt);
            while (!scanner.hasNextInt()) {
                scanner.next();
                System.out.println(Color.red(error));
                System.out.print(prompt);
            }
            int opt = scanner.nextInt();
            scanner.nextLine();
            return opt;
        }

        public static int SReadInteger(String prompt, String color, String error) {
            System.out.print(color + prompt + Color.RESET);
            while (!scanner.hasNextInt()) {
                scanner.next();
                System.out.println(Color.red(error));
                System.out.print(color + prompt + Color.RESET);
            }
            int opt = scanner.nextInt();
            scanner.nextLine();
            return opt;
        }

        public static int ReadInteger(String prompt, int min, int max) {
            int value;
            boolean valid = false;

            do {
                System.out.print(prompt);
                while (!scanner.hasNextInt()) {
                    scanner.next();
                    System.out.println(Color.red("Entrada inválida! Digite um número."));
                    System.out.print(prompt);
                }
                value = scanner.nextInt();
                scanner.nextLine();

                if (value < min || value > max) {
                    System.out.println(Color.red("Valor deve estar entre " + min + " e " + max));
                } else {
                    valid = true;
                }
            } while (!valid);

            return value;
        }

        public static void WaitConfirm() {
            System.out.println(Color.yellow("\nPressione ENTER para continuar..."));
            scanner.nextLine();
        }

        public static void WaitConfirm(String text) {
            System.out.println(Color.yellow(text));
            scanner.nextLine();
        }

        public static boolean Confirm(String question) {
            System.out.print(question + " (S/N): ");
            String r = scanner.nextLine().trim().toUpperCase();
            return r.equals("S") || r.equals("SI") || r.equals("SIM") ||
                    r.equals("Y") || r.equals("YE") || r.equals("YES");
        }
    }

    public static String Centralize(String text, int width) {
        if (text.length() >= width) {
            return text;
        }

        int padding = (width - text.length()) / 2;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < padding; i++) {
            sb.append(" ");
        }
        sb.append(text);

        return sb.toString();
    }

    public static void printlnCentered(String text, int width, String color) {
        System.out.println(color + Centralize(text, width) + Color.RESET);
    }

    public static void printlnCentered(String text, int width) {
        System.out.println(Centralize(text, width));
    }

    public static void printlnCentered(String text) {
        printlnCentered(text, 80);
    }

    public static void printlnCentered(String text, String color) {
        printlnCentered(text, 80, color);
    }

    public static void println(String text, String color) {
        System.out.println(color + text + Color.RESET);
    }

    public static void print(String text, String color) {
        System.out.print(color + text + Color.RESET);
    }

    public static void println(String text) {
        System.out.println(text);
    }

    public static void print(String text) {
        System.out.print(text);
    }

    public static void emptyLines(int quantity) {
        for (int i=0; i<quantity; i++) {
            System.out.println();
        }
    }

    public static void line(int len, String color) {
        System.out.println(Color.linha(len, color));
    }

    public static void line() {
        line(50, Color.CYAN);
    }

    public static void Clear() {
        Color.limparTela();
    }

    public static void HideCursor() {
        Color.esconderCursor();
    }

    public static void ShowCursor() {
        Color.mostrarCursor();
    }

    public static void MoveCursorTo(int line, int column) {
        Color.moverCursor(line, column);
    }

    public static void Close() {
        Input.scanner.close();
    }

    public static void Pause(int milliseconds) {
        try  {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void Box(String title) {
        int width = title.length() + 4;
        System.out.println(Color.cyan("╔" + "═".repeat(width) + "╗"));
        System.out.println(Color.cyan("║  ") + Color.bold(title) + Color.cyan("  ║"));
        System.out.println(Color.cyan("╚" + "═".repeat(width) + "╝"));
    }


    public static void Box(String title, String color) {
        int width = title.length() + 4;
        System.out.println(color + "╔" + "═".repeat(width) + "╗" + Color.RESET);
        System.out.println(color + "║  " + Color.RESET + Color.bold(title) + color + "  ║" + Color.RESET);
        System.out.println(color + "╚" + "═".repeat(width) + "╝" + Color.RESET);
    }

    public static int Menu(String title, String... options) {
        Box(title);
        emptyLines(1);

        for (int i=0; i<options.length; i++) {
            System.out.println(Color.cyan((i + 1) + ". ") + options[i]);
        }

        emptyLines(1);
        return Input.ReadInteger("Sua escolha: ", 1, options.length);
    }

    public static void Error(String message) {
        System.out.println(Color.style("✗ ERRO: " + message, Color.RED, Color.BOLD));
    }

    public static void Success(String message) {
        System.out.println(Color.style("✓ " + message, Color.GREEN, Color.BOLD));
    }

    public static void Warning(String message) {
        System.out.println(Color.style("⚠ " + message, Color.YELLOW, Color.BOLD));
    }

    public static void Info(String message) {
        System.out.println(Color.style("ℹ " + message, Color.CYAN, Color.BOLD));
    }

    public static void TypeWriter(String message, int velocity) {
        for (char c: message.toCharArray()) {
            System.out.print(c);
            Pause(velocity);
        }
        emptyLines(1);
    }
}
