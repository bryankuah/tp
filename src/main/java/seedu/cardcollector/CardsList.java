package seedu.cardcollector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class CardsList {
    private final ArrayList<Card> inventory;

    public CardsList() {
        this.inventory = new ArrayList<Card>();
    }

    public void addCard(Card card) {
        if (card == null) {
            System.out.println("seedu.cardcollector.Card not found!");
        }
        inventory.add(card);
    }

    public void removeCard(int index) {
        if (index < 0) {
            System.out.println("Index cannot be 0 or negative!");
        } else if (index >= inventory.size()) {
            System.out.println("Index cannot be greater than inventory size!");
        } else {
            inventory.remove(index);
        }
    }

    public boolean removeCardByName(String name) {
        for (int i = 0; i < inventory.size(); i++) {
            Card card = inventory.get(i);
            if (card.getName().equalsIgnoreCase(name)) {
                inventory.remove(i);
                return true;
            }
        }
        return false;
    }

    public Card getCard(int index) {
        return inventory.get(index);
    }

    public int getSize () {
        return inventory.size();
    }

    public ArrayList<Card> findCards(String name, Float price, Integer quantity) {
        ArrayList<Card> results = new ArrayList<>();
        for (Card card : inventory) {
            boolean matches = true;
            if (name != null && !card.getName().toLowerCase().contains(name.toLowerCase())) {
                matches = false;
            }
            if (price != null && card.getPrice() != price) {
                matches = false;
            }
            if (quantity != null && card.getQuantity() != quantity) {
                matches = false;
            }
            if (matches) {
                results.add(card);
            }
        }
        return results;
    }

    public ArrayList<Card> getCardsSortedByLastAdded() {
        return inventory.stream()
                .sorted(Comparator.comparing(Card::getLastAdded).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
