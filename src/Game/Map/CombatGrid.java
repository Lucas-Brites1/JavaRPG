package Game.Map;

import Game.Attacks.AttackPattern;
import utils.Color;
import utils.Terminal;

public class CombatGrid {
    private final int lines,columns;
    private MapGrid grid;

    public CombatGrid(int lines, int columns) {
        this.lines = lines;
        this.columns = columns;
        grid = new MapGrid(lines, columns);
    }

    public void Set(int line, int column, char character) {
        if (ValidPosition(line, column)) {
            grid.SetChar(line, column, character);
        }
    }

    public char Get(int line, int column) {
        if (ValidPosition(line, column)) {
            return grid.GetChar(line, column);
        }

        return '\0';
    }

    public boolean ValidPosition(int line, int column) {
        return line >= 0 && line < lines && column >= 0 && column < columns;
    }

    public boolean PositionEmpty(int line, int column) {
        return ValidPosition(line, column) && Get(line, column) == MapGrid.EMPTY_CHAR;
    }

    public void ClearMap() {
        grid.Clear();
    }

    public void ClearPosition(int line, int column) {
        if (ValidPosition(line, column)) {
            Set(line, column, MapGrid.EMPTY_CHAR);
        }
    }

    public boolean Move(int fromLine, int fromColumn, int toLine, int toColumn) {
        if (ValidPosition(fromLine, fromColumn) && PositionEmpty(toLine, toColumn)) {
            char c = Get(fromLine, fromColumn);
            Set(toLine, toColumn, c);
            ClearPosition(fromLine, fromColumn);
            return true;
        }
        return false;
    }

    private String GetColoredChar(char c) {
        return switch (c) {
            case 'G' -> Color.GREEN + c + Color.RESET; // Guerreiro
            case 'M' -> Color.PURPLE + c + Color.RESET; // Mago
            case 'A' -> Color.CYAN + c + Color.RESET; // Arqueiro
            case 'E' -> Color.RED + c + Color.RESET; // Inimigo
            case 'X' -> Color.YELLOW + c + Color.RESET; // Preview área vazia
            case '☠' -> Color.RED + c + Color.RESET; // Preview hit
            case '*' -> Color.YELLOW + c + Color.RESET; // Projétil/Ataque
            case '⊕' -> Color.BRIGHT_CYAN + c + Color.RESET; // Ponto de origem
            case ' ' -> " "; // Vazio
            default -> String.valueOf(c);
        };
    }

    public void DrawMap() {
        System.out.println();

        // Número de dígitos para índices de linha
        int lineDigits = String.valueOf(lines - 1).length();

        // Impressão dos números de coluna
        StringBuilder headerRow = new StringBuilder();
        headerRow.append(" ".repeat(lineDigits + 1));

        for (int j = 0; j < columns; j++) {
            if (j <= 10) {
                headerRow.append("  ").append(j);
            } else {
                headerRow.append(j).append("   ");
            }
        }
        System.out.println(headerRow);

        String indent = " ".repeat(lineDigits + 1);
        System.out.print(indent + "┌");
        for (int j = 0; j < columns; j++) {
            System.out.print("───");
            if (j < columns - 1) {
                System.out.print("┬");
            }
        }
        System.out.println("┐");

        for (int i = 0; i < lines; i++) {
            // Número da linha com padding à direita
            String lineNum = String.format("%-" + lineDigits + "d", i);
            System.out.print(lineNum + " │");

            for (int j = 0; j < columns; j++) {
                char cellChar = grid.GetChar(i, j);
                String coloredChar = GetColoredChar(cellChar);

                if (coloredChar.length() > 1) {
                    System.out.print(" " + coloredChar + " ");
                } else {
                    System.out.print(" " + coloredChar + " ");
                }

                if (j < columns - 1) {
                    System.out.print("│");
                }
            }
            System.out.println("│");

            if (i < lines - 1) {
                System.out.print(indent + "├");
                for (int j = 0; j < columns; j++) {
                    System.out.print("───");
                    if (j < columns - 1) {
                        System.out.print("┼");
                    }
                }
                System.out.println("┤");
            }
        }

        System.out.print(indent + "└");
        for (int j = 0; j < columns; j++) {
            System.out.print("───");
            if (j < columns - 1) {
                System.out.print("┴");
            }
        }
        System.out.println("┘");
    }

    public MapGrid SaveState() {
        return grid.clone();
    }

    public void PreviewAttackOnMap(int startLine, int startColumn, AttackPattern pattern) {
        MapGrid backup = SaveState();

        if (pattern.isMultiStage()) {
            int stageIndex = 0;
            MapGrid previousStageGrid = null;

            for (AttackPattern.AttackStage stage : pattern.GetAllStages()) {
                System.out.println("\n" + Color.CYAN + "═══════════════════════════════════" + Color.RESET);
                System.out.println(Color.BOLD + "Estágio " + (stageIndex + 1) + "/" + pattern.GetTotalStages() +
                        ": " + Color.RESET + stage.getDescription());
                if (stage.getDelay() > 0) {
                    System.out.println(Color.YELLOW + "⏱ Delay: " + stage.getDelay() + " turno(s)" + Color.RESET);
                }
                System.out.println(Color.CYAN + "═══════════════════════════════════" + Color.RESET);

                int[][] currentPattern = stage.getPattern();

                if (previousStageGrid != null && stageIndex > 0) {
                    AttackPattern.AttackStage prevStage = pattern.GetStage(stageIndex - 1);
                    int[][] prevPattern = prevStage.getPattern();

                    for (int i = 0; i < prevPattern.length; i++) {
                        for (int j = 0; j < prevPattern[i].length; j++) {
                            if (prevPattern[i][j] == 1) {
                                int targetLine = startLine + i;
                                int targetColumn = startColumn + j;

                                if (ValidPosition(targetLine, targetColumn)) {
                                    char originalChar = backup.GetChar(targetLine, targetColumn);
                                    char currentGridChar = grid.GetChar(targetLine, targetColumn);

                                    if (currentGridChar == 'X' || currentGridChar == '☠') {
                                        Set(targetLine, targetColumn, originalChar);
                                    }
                                }
                            }
                        }
                    }
                }

                for (int i = 0; i < currentPattern.length; i++) {
                    for (int j = 0; j < currentPattern[i].length; j++) {
                        if (currentPattern[i][j] == 1) {
                            int targetLine = startLine + i;
                            int targetColumn = startColumn + j;

                            if (ValidPosition(targetLine, targetColumn)) {
                                char currentChar = backup.GetChar(targetLine, targetColumn);

                                if (currentChar == MapGrid.EMPTY_CHAR) {
                                    Set(targetLine, targetColumn, 'X');
                                } else if (currentChar == 'E') {
                                    Set(targetLine, targetColumn, '☠');
                                }
                            }
                        }
                    }
                }

                previousStageGrid = grid.clone();
                DrawMap();

                // Pausa entre estágios (exceto no último)
                if (stageIndex < pattern.GetTotalStages() - 1) {
                    Terminal.Input.WaitConfirm("\n" + Color.YELLOW + "Pressione ENTER para ver o próximo estágio..." + Color.RESET);
                }

                stageIndex++;
            }

            this.grid = backup;

        } else {
            for (int i = 0; i < pattern.Size(); i++) {
                for (int j = 0; j < pattern.GetPattern()[i].length; j++) {
                    if (pattern.GetPattern()[i][j] == 1) {
                        int targetLine = startLine + i;
                        int targetColumn = startColumn + j;

                        if (ValidPosition(targetLine, targetColumn)) {
                            char currentChar = grid.GetChar(targetLine, targetColumn);

                            if (currentChar == MapGrid.EMPTY_CHAR) {
                                Set(targetLine, targetColumn, 'X');
                            } else if (currentChar == 'E') {
                                Set(targetLine, targetColumn, '☠');
                            }
                        }
                    }
                }
            }

            DrawMap();
            this.grid = backup;
        }
    }
}