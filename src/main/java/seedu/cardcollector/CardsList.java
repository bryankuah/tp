package seedu.cardcollector;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class CardsList {
    private final ArrayList<Card> cards;
    private final ArrayList<Card> removedCards;
    private final ArrayList<Card> addedCards;

    public CardsList() {
        this.cards = new ArrayList<Card>();
        this.removedCards = new ArrayList<Card>();
        this.addedCards = new ArrayList<Card>();
    }

    public void addCard(Card newCard) {
        if (newCard == null) {
            System.out.println("seedu.cardcollector.Card not found!");
            return;
        }

        assert newCard != null : "new card added should not be null";

        Instant currentInstant = Instant.now();

        for (Card existingCard : cards) {
            if (existingCard.getName().equalsIgnoreCase(newCard.getName()) &&
                existingCard.getPrice() == newCard.getPrice()){
                int updatedQuantity = existingCard.getQuantity() + newCard.getQuantity();
                existingCard.setQuantity(updatedQuantity);
                existingCard.setLastModified(currentInstant);
                assert cards.contains(existingCard) : "Updated card must still be in the list";
                return;
            }
        }
        int sizeBefore = cards.size();

        newCard.setLastAdded(currentInstant);
        newCard.setLastModified(currentInstant);
        cards.add(newCard);
        addedCards.add(newCard);

        assert cards.size() == sizeBefore + 1 : "List size should increment by 1 after add";
        assert cards.get(cards.size() - 1).equals(newCard) : "Latest card should be at the end";
    }

    public void removeCardByIndex(int index) {
        assert cards != null : "Cards list should be initialized";

        int sizeBefore = cards.size();

        if (index < 0) {
            System.out.println("Index cannot be 0 or negative!");
            assert cards.size() == sizeBefore;
        } else if (index >= cards.size()) {
            System.out.println("Index cannot be greater than inventory size!");
            assert cards.size() == sizeBefore;
        } else {
            Card removed = cards.remove(index);

            Instant currentInstant = Instant.now();
            removed.setLastModified(currentInstant);
            removedCards.add(removed);

            assert cards.size() == sizeBefore - 1 : "Size should decrease after removal";
            assert removedCards.contains(removed) : "Removed card must be tracked";
        }
    }

    public boolean removeCardByName(String name) {
        assert name != null : "Name should not be null";

        int sizeBefore = cards.size();

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (card.getName().equalsIgnoreCase(name)) {
                Card removed = cards.remove(i);

                Instant currentInstant = Instant.now();
                removed.setLastModified(currentInstant);
                removedCards.add(removed);

                assert cards.size() == sizeBefore - 1 : "Size should decrease after removal";
                assert removedCards.contains(removed) : "Removed card must be tracked";

                return true;
            }
        }

        assert cards.size() == sizeBefore : "Size should not change if not found";
        return false;
    }

    public Card getCard(int index) {
        return cards.get(index);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public ArrayList<Card> getRemovedCards() {
        return removedCards;
    }

    public ArrayList<Card> getAddedCards() {
        return addedCards;
    }

    public int getSize() {
        return cards.size();
    }

    public int getRemovedSize() {
        return removedCards.size();
    }

    public int getAddedSize() {
        return addedCards.size();
    }

    public ArrayList<Card> findCards(String name, Float price, Integer quantity) {
        assert cards != null : "Cards inventory should be initialized before searching";

        ArrayList<Card> results = new ArrayList<>();
        for (Card card : cards) {
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

        assert results != null : "The results list should not be null";
        assert results.size() <= cards.size();

        return results;
    }

    private static Comparator<Card> getSortComparator(CardSortCriteria criteria) {
        switch (criteria) {
        case NAME -> {
            return Comparator.comparing(Card::getName);
        }
        case QUANTITY -> {
            return Comparator.comparingInt(Card::getQuantity);
        }
        case PRICE -> {
            return Comparator.comparingDouble(Card::getPrice);
        }
        case LAST_ADDED -> {
            return Comparator.comparing(Card::getLastAdded,
                    Comparator.nullsLast(Instant::compareTo));
        }
        case LAST_MODIFIED -> {
            return Comparator.comparing(Card::getLastModified,
                    Comparator.nullsLast(Instant::compareTo));
        }
        default -> {
            assert false : "Invalid criteria";
        }
        }
        return null;
    }

    private static ArrayList<Card> getSortedCardsFromList(
            ArrayList<Card> cards,
            CardSortCriteria criteria,
            boolean isAscending,
            int maxLimit,
            int defaultMaxLimit) {

        if (cards.isEmpty()) {
            return new ArrayList<>();
        }

        Comparator<Card> comparator = getSortComparator(criteria);

        assert comparator != null : "No available comparator for criteria";

        // Apply ascending/descending order
        if (!isAscending) {
            comparator = comparator.reversed();
        }

        int recordsLimit = (maxLimit == -1) ? defaultMaxLimit :
                Math.min(cards.size(), maxLimit);

        return cards.stream()
                .sorted(comparator)
                .limit(recordsLimit)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Card> getSortedCards(
            CardSortCriteria criteria,
            boolean isAscending,
            int maxLimit,
            int defaultMaxLimit) {
        return getSortedCardsFromList(cards, criteria, isAscending, maxLimit, defaultMaxLimit);
    }

    public ArrayList<Card> getSortedAddedCards(
            CardSortCriteria criteria,
            boolean isAscending,
            int maxLimit,
            int defaultMaxLimit) {
        return getSortedCardsFromList(addedCards, criteria, isAscending, maxLimit, defaultMaxLimit);
    }

    public ArrayList<Card> getSortedRemovedCards(
            CardSortCriteria criteria,
            boolean isAscending,
            int maxLimit,
            int defaultMaxLimit) {
        return getSortedCardsFromList(removedCards, criteria, isAscending, maxLimit, defaultMaxLimit);
    }
}
