package Game.Attacks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AttackPattern{
    private String name;
    private int[][] pattern;
    private List<AttackStage> stages;
    private boolean isMultiStage;

    public AttackPattern(String name, int[][] pattern) {
        this.pattern = pattern;
        this.name = name;
        this.stages = new ArrayList<>();
        this.isMultiStage = false;
    }

    public AttackPattern(String name, AttackStage... stages) {
        this.name = name;
        this.stages = List.of(stages);
        this.isMultiStage = true;
        this.pattern = stages[0].getPattern();
    }

    public int[][] GetPattern() {
        return pattern;
    }

    public int[][] GetPatternForStage(int stageIndex) {
        if (isMultiStage && stageIndex < stages.size()) {
            return stages.get(stageIndex).pattern;
        }

        return pattern;
    }

    public String GetName() {
        return name;
    }

    public int Size() {
        return this.pattern.length;
    }

    public boolean isMultiStage() {
        return isMultiStage;
    }

    public int GetTotalStages() {
        return isMultiStage ? this.stages.size() : 1;
    }

    public AttackStage GetStage(int index) {
        if (isMultiStage && index < stages.size()) {
            return stages.get(index);
        }

        return null;
    }

    public List<AttackStage> GetAllStages() {
        return stages;
    }

    public static class AttackStage  {
        private int[][] pattern;
        private String description;
        private int delay;

        public AttackStage(String description, int delay, int[][] pattern) {
            this.pattern = pattern;
            this.delay = delay;
            this.description = description;
        }

        public int[][] getPattern() {
            return pattern;
        }

        public String getDescription() {
            return description;
        }

        public int getDelay() {
            return delay;
        }

    }

    public static final AttackPattern SINGLE = new AttackPattern(
            "Ataque único",
            new int[][] {{1}}
    );

    public static final AttackPattern LINE_H3 = new AttackPattern(
            "Linha Horizontal (3)",
            new int[][] {{1,1,1}}
    );

    public static final AttackPattern LINE_H5 = new AttackPattern(
            "Linha Horizontal (5)",
            new int[][]{{1,1,1,1,1}}
    );

    public static final AttackPattern LINE_V3 = new AttackPattern(
            "Linha Vertical (3)",
            new int[][]{{1}, {1}, {1}}
    );

    public static final AttackPattern LINE_V5 = new AttackPattern(
            "Linha Vertical (5)",
            new int[][]{{1}, {1}, {1}, {1}, {1}, {1}}
    );

    public static final AttackPattern SQUARE_2X2 = new AttackPattern(
            "Área 2x2",
            new int[][]{
                    {1, 1},
                    {1, 1}
            }
    );

    public static final AttackPattern SQUARE_3X3 = new AttackPattern(
            "Área 3x3",
            new int[][]{
                    {1,1,1},
                    {1,1,1},
                    {1,1,1}
            }
    );

    public static final AttackPattern SMALL_CROSS = new AttackPattern(
            "Cruz pequena",
            new int[][]{
                    {0, 1, 0},
                    {1, 1, 1},
                    {0, 1, 0}
            }
    );

    public static final AttackPattern BIG_CROSS = new AttackPattern(
            "Cruz grande",
            new int[][]{
                    {0, 1, 1, 0},
                    {0, 1, 1, 0},
                    {1, 1, 1, 1},
                    {1, 1, 1, 1},
                    {0, 1, 1, 0},
                    {0, 1, 1, 0}
            }
    );

    public static final AttackPattern X_SHAPE = new AttackPattern(
            "X",
            new int[][]{
                    {1, 0, 1},
                    {0, 1, 0},
                    {1, 0, 1}
            }
    );

    public static final AttackPattern T_SHAPE = new AttackPattern(
            "T",
            new int[][]{
                    {1, 1, 1},
                    {0, 1, 0},
                    {0, 1, 0}
            }
    );

    public static final AttackPattern L_SHAPE = new AttackPattern(
            "L",
            new int[][]{
                    {1, 0, 0},
                    {1, 0, 0},
                    {1, 1, 1}
            }
    );

    public static final AttackPattern BOUNCE_V = new AttackPattern(
            "Pulante vertical",
            new int[][]{{1}, {0}, {1}, {0}, {1}}
    );

    public static final AttackPattern BOUNCE_H = new AttackPattern(
            "Pulante horizontal",
            new int[][]{{1, 0, 1, 0, 1}}
    );

    public static final AttackPattern ARROW_DIAGONAL = new AttackPattern(
            "Flecha diagonal",
            new int[][]{
                    {1, 0, 0, 0},
                    {0, 1, 0, 1},
                    {0, 0, 1, 1},
                    {0, 1, 1, 1}
            }
    );

    public static final AttackPattern METEOR_STRIKE = new AttackPattern(
            "Chuva de meteoros",
            new AttackStage("Meteoros caindo...", 0, new int[][]{
                    {0, 0, 1, 0, 0},
                    {0, 1, 0, 1, 0},
                    {1, 0, 0, 0, 1}
            }),
            new AttackStage("Impacto!", 1, new int[][]{
                    {0, 0, 1, 1, 0, 0},
                    {0, 1, 1, 1, 1, 0},
                    {1, 1, 1, 1, 1, 1},
                    {1, 1, 1, 1, 1, 1},
                    {0, 1, 1, 1, 1, 0},
                    {0, 0, 1, 1, 0, 0}
            })
    );

    public static final AttackPattern ARROW_RAIN = new AttackPattern(
            "Chuva de flechas",
            new AttackStage("Onda de flechas inicial...", 0, new int[][]{
                    {1},
                    {1},
                    {1},
                    {1},
                    {1},
                    {1},
                    {1}
            }),
            new AttackStage("Onda de flechas secundárias...", 1, new int[][]{
                    {0, 1},
                    {0, 1},
                    {0, 1},
                    {0, 1},
                    {0, 1},
                    {0, 1},
                    {0, 1}
            }),
            new AttackStage("Onda de flechas final...", 2, new int[][]{
                    {0, 0, 1},
                    {0, 0, 1},
                    {0, 0, 1},
                    {0, 0, 1},
                    {0, 0, 1},
                    {0, 0, 1},
                    {0, 0, 1}
            })
    );
}

