package Game;

import Game.Attacks.Attack;
import Game.Character.Entity;

import java.util.*;

public class TurnSystem {
    private List<Entity> turnOrder;
    private int currentIndex;

    public TurnSystem() {
        this.turnOrder = new ArrayList<>();
        this.currentIndex = 0;
    }

    public void Initialize(Entity player, List<Entity> enemies) {
        turnOrder.clear();
        turnOrder.add(player);
        turnOrder.addAll(enemies);
        PrioritizeStamina();
        currentIndex = 0;
    }

    public Entity GetCurrentEntity() {
        if (turnOrder.isEmpty()) return null;
        return turnOrder.get(currentIndex);
    }

    public void AdvanceTurn() {
        currentIndex++;
        if (currentIndex >= turnOrder.size()) {
            currentIndex = 0;
            StartNewRound();
        }
    }

    private void PrioritizeStamina(){
        turnOrder.sort(Comparator.comparingDouble(Entity::getStamina).reversed());
    }

    private void StartNewRound() {
        for (Entity entity : turnOrder) {
            entity.getAttacks().values().forEach(Attack::ReduceCooldown);
            entity.RecoverStamina(entity.getStaminaMax() * 0.15);
        }
    }

    public void RemoveEntity(Entity entity) {
        int removedIndex = turnOrder.indexOf(entity);

        if (removedIndex == -1) {
            return;
        }

        turnOrder.remove(removedIndex);
    }

    public boolean IsPlayerTurn(Entity player) {
        Entity current = turnOrder.get(currentIndex);
        return current != null && current.equals(player);
    }

    public int GetRemainingEntities() {
        return turnOrder.size();
    }
}
