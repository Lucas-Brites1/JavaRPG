package Game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Dice {
    public static class SingleDiceRoll {
        public final int value;

        public SingleDiceRoll(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    public static class DiceResult {
        private final String winner;
        private final String loser;
        private final List<SingleDiceRoll> winnerRolls;
        private final List<SingleDiceRoll> loserRolls;
        private final int winnerTotal;
        private final int loserTotal;

        public DiceResult(String winner, String loser,
                          List<SingleDiceRoll> winnerRolls,
                          List<SingleDiceRoll> loserRolls,
                          int winnerTotal, int loserTotal) {
            this.winner = winner;
            this.loser = loser;
            this.winnerRolls = winnerRolls;
            this.loserRolls = loserRolls;
            this.winnerTotal = winnerTotal;
            this.loserTotal = loserTotal;
        }

        public String getWinner() {
            return winner;
        }

        public String getLoser() {
            return loser;
        }

        public List<SingleDiceRoll> getWinnerRolls() {
            return winnerRolls;
        }

        public List<SingleDiceRoll> getLoserRolls() {
            return loserRolls;
        }

        public int getWinnerTotal() {
            return winnerTotal;
        }

        public int getLoserTotal() {
            return loserTotal;
        }

        public HashMap<String, List<SingleDiceRoll>> getResultMap() {
            HashMap<String, List<SingleDiceRoll>> map = new HashMap<>();
            map.put(winner, winnerRolls);
            map.put(loser, loserRolls);
            return map;
        }

        @Override
        public String toString() {
            return String.format("%s venceu com %d (%s) vs %s com %d (%s)",
                    winner, winnerTotal, winnerRolls,
                    loser, loserTotal, loserRolls);
        }
    }

    private final int numberOfDices;
    private final int sidesPerDice;
    private DiceResult lastResult;

    public Dice(int numberOfDices, int sidesPerDice) {
        if (numberOfDices <= 0 || sidesPerDice <= 0) {
            throw new IllegalArgumentException("Número de dados e lados devem ser positivos");
        }
        this.numberOfDices = numberOfDices;
        this.sidesPerDice = sidesPerDice;
    }

    private int rollSingleDice() {
        return (int) (Math.random() * sidesPerDice) + 1;
    }

    public List<SingleDiceRoll> rollMultipleDices() {
        List<SingleDiceRoll> rolls = new ArrayList<>();
        for (int i = 0; i < numberOfDices; i++) {
            rolls.add(new SingleDiceRoll(rollSingleDice()));
        }
        return rolls;
    }

    private int calculateTotal(List<SingleDiceRoll> rolls) {
        return rolls.stream().mapToInt(r -> r.value).sum();
    }

    public DiceResult roll(String player1, String player2) {
        if (player1 == null || player2 == null || player1.isEmpty() || player2.isEmpty()) {
            throw new IllegalArgumentException("Nomes dos jogadores não podem ser nulos ou vazios");
        }

        List<SingleDiceRoll> player1Rolls = rollMultipleDices();
        List<SingleDiceRoll> player2Rolls = rollMultipleDices();

        int total1 = calculateTotal(player1Rolls);
        int total2 = calculateTotal(player2Rolls);

        int attempts = 0;
        while (total1 == total2 && attempts < 10) {
            player1Rolls = rollMultipleDices();
            player2Rolls = rollMultipleDices();
            total1 = calculateTotal(player1Rolls);
            total2 = calculateTotal(player2Rolls);
            attempts++;
        }

        if (total1 >= total2) {
            this.lastResult = new DiceResult(player1, player2, player1Rolls, player2Rolls, total1, total2);
        } else {
            this.lastResult = new DiceResult(player2, player1, player2Rolls, player1Rolls, total2, total1);
        }

        return this.lastResult;
    }

    public DiceResult getLastResult() {
        if (lastResult == null) {
            throw new IllegalStateException("Nenhuma rolagem foi feita ainda. Chame roll() primeiro.");
        }
        return lastResult;
    }

    public int getNumberOfDices() {
        return numberOfDices;
    }

    public int getSidesPerDice() {
        return sidesPerDice;
    }
}