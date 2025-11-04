package Game.Map;

import java.util.ArrayList;

public class MapGrid extends ArrayList<ArrayList<Character>> implements Cloneable {
    public static final char EMPTY_CHAR = ' ';

    public MapGrid(int lines, int columns) {
        for (int i = 0; i < lines; i++) {
            ArrayList<Character> row = new ArrayList<>();
            for (int j = 0; j < columns; j++) {
                row.add(EMPTY_CHAR);
            }
            this.add(row);
        }
    }

    public MapGrid(MapGrid other) {
        for (ArrayList<Character> row : other) {
            this.add(new ArrayList<>(row));
        }
    }

    @Override
    public MapGrid clone() {
        return new MapGrid(this);
    }

    public void SetChar(int line, int column, char c) {
        this.get(line).set(column, c);
    }

    public char GetChar(int line, int column) {
        return this.get(line).get(column);
    }

    public void Clear() {
        for (ArrayList<Character> row : this) {
            row.replaceAll(ignored -> EMPTY_CHAR);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ArrayList<Character> row : this) {
            for (Character c : row) {
                sb.append(c).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}