package seedu.cardcollector.card;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class CardsList {
    private final ArrayList<Card> cards;
    private CardsHistory history;

    public CardsList() {
        this.cards = new ArrayList<Card>();
        this.history = new CardsHistory();
    }

    public CardsList(ArrayList<Card> cards, CardsHistory history) {
        this.cards = new ArrayList<>(cards);
        this.history = history;
    }

    public void addCard(Card newCard) {
        if (newCard == null) {
            System.out.println("seedu.cardcollector.Card not found!");
            return;
        }

        assert newCard != null : "new card added should not be null";

        Instant currentInstant = Instant.now();

        for (Card existingCard : cards) {
            if (isSameCardVariant(existingCard, newCard)) {
                Card originalCard = existingCard.copy();

                int updatedQuantity = existingCard.getQuantity() + newCard.getQuantity();
                existingCard.setQuantity(updatedQuantity);
                existingCard.setLastAdded(currentInstant);

                this.history.add(originalCard, existingCard.copy());

                assert cards.contains(existingCard) : "Updated card must still be in the list";
                return;
            }
        }
        int sizeBefore = cards.size();

        newCard.setLastAdded(currentInstant);
        cards.add(newCard);

        this.history.add(null, newCard.copy());

        assert cards.size() == sizeBefore + 1 : "List size should increment by 1 after add";
        assert cards.get(cards.size() - 1).equals(newCard) : "Latest card should be at the end";
    }

    public void addCardAtIndex(int index, Card card) {
        assert card != null : "Card to re-insert should not be null";
        assert index >= 0 && index <= cards.size() : "Index out of bounds for re-insertion";

        Instant currentInstant = Instant.now();
        card.setLastAdded(currentInstant);
        cards.add(index, card);

        this.history.add(null, card.copy());
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
            removed.setLastRemoved(currentInstant);

            this.history.add(removed.copy(),  null);

            assert cards.size() == sizeBefore - 1 : "Size should decrease after removal";
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
                removed.setLastRemoved(currentInstant);

                this.history.add(removed.copy(),  null);

                assert cards.size() == sizeBefore - 1 : "Size should decrease after removal";

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

    public CardsAnalytics getAnalytics(int expensiveLimit, int topSetLimit) {
        int totalQuantity = 0;
        double totalValue = 0;
        Map<String, Integer> setCounts = new HashMap<>();

        for (Card card : cards) {
            totalQuantity += card.getQuantity();
            totalValue += card.getPrice() * card.getQuantity();

            String normalizedSetName = normalizeSetName(card.getCardSet());
            setCounts.merge(normalizedSetName, card.getQuantity(), Integer::sum);
        }

        ArrayList<CardsAnalytics.CardMetric> mostExpensiveCards = cards.stream()
                .sorted(Comparator.comparingDouble(Card::getPrice)
                        .reversed()
                        .thenComparing(Card::getName, String.CASE_INSENSITIVE_ORDER))
                .limit(expensiveLimit)
                .map(card -> new CardsAnalytics.CardMetric(card, card.getPrice() * card.getQuantity()))
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<CardsAnalytics.SetMetric> topSetsByCount = setCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER)))
                .limit(topSetLimit)
                .map(entry -> new CardsAnalytics.SetMetric(entry.getKey(), entry.getValue()))
                .collect(Collectors.toCollection(ArrayList::new));

        return new CardsAnalytics(cards.size(), totalQuantity, totalValue, mostExpensiveCards, topSetsByCount);
    }

    public int getSize() {
        return cards.size();
    }

    public void replaceWith(CardsList other) {
        assert other != null : "Replacement list should not be null";

        cards.clear();
        cards.addAll(other.getCards());
        history = other.getHistory().copy();
    }

    public CardsList deepCopy() {
        Map<java.util.UUID, Card> copiedCards = new HashMap<>();
        return new CardsList(
                copyCards(cards, copiedCards),
                history.copy()
        );
    }

    private static ArrayList<Card> copyCards(ArrayList<Card> source, Map<java.util.UUID, Card> copiedCards) {
        ArrayList<Card> result = new ArrayList<>();
        for (Card card : source) {
            Card copy = copiedCards.computeIfAbsent(card.getUid(), ignored -> card.copy());
            result.add(copy);
        }
        return result;
    }

    public ArrayList<Card> findCards(String name, Float price, Integer quantity,
            String cardSet, String rarity, String condition, String language, String cardNumber, String tag) {
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
            if (!containsIgnoreCase(card.getCardSet(), cardSet)) {
                matches = false;
            }
            if (!containsIgnoreCase(card.getRarity(), rarity)) {
                matches = false;
            }
            if (!containsIgnoreCase(card.getCondition(), condition)) {
                matches = false;
            }
            if (!containsIgnoreCase(card.getLanguage(), language)) {
                matches = false;
            }
            if (!containsIgnoreCase(card.getCardNumber(), cardNumber)) {
                matches = false;
            }
            if (!containsTagIgnoreCase(card, tag)) {
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

    public ArrayList<Card> getSortedCards(
            CardSortCriteria criteria,
            boolean isAscending,
            int maxLimit,
            int defaultMaxLimit) {
        return CardSort.sortCards(cards, criteria, isAscending, maxLimit, defaultMaxLimit);
    }

    public boolean editCard(int index, String newName, Integer newQuantity, Float newPrice,
            String newCardSet, String newRarity, String newCondition, String newLanguage, String newCardNumber) {
        assert index >= 0 && index < cards.size() : "Index should be validated before calling editCard";

        Card card = cards.get(index);

        Instant currentInstant = Instant.now();
        boolean quantityChanged = false;

        // Special handling for quantity
        if (newQuantity != null) {
            Card originalCard = card.copy();
            int previousQuantity = originalCard.getQuantity();

            if (newQuantity > previousQuantity) {
                quantityChanged = true;
                card.setQuantity(newQuantity);
                card.setLastAdded(currentInstant);
                history.add(originalCard, card.copy());
            } else if (newQuantity < previousQuantity) {
                quantityChanged = true;
                card.setQuantity(newQuantity);
                card.setLastRemoved(currentInstant);
                history.add(originalCard, card.copy());
            }
        }

        // Normal handling for the rest of the fields
        Card originalCard = card.copy();
        boolean anyFieldChanged = false;

        if (newName != null && !newName.isBlank()) {
            String trimmedNewName = newName.trim();
            if (!trimmedNewName.equals(card.getName())) {
                card.setName(trimmedNewName);
                anyFieldChanged = true;
            }
        }

        if (newPrice != null && newPrice != card.getPrice()) {
            card.setPrice(newPrice);
            anyFieldChanged = true;
        }
        if (isUpdatedTextValue(newCardSet, card.getCardSet())) {
            card.setCardSet(trimToNull(newCardSet));
            anyFieldChanged = true;
        }
        if (isUpdatedTextValue(newRarity, card.getRarity())) {
            card.setRarity(trimToNull(newRarity));
            anyFieldChanged = true;
        }
        if (isUpdatedTextValue(newCondition, card.getCondition())) {
            card.setCondition(trimToNull(newCondition));
            anyFieldChanged = true;
        }
        if (isUpdatedTextValue(newLanguage, card.getLanguage())) {
            card.setLanguage(trimToNull(newLanguage));
            anyFieldChanged = true;
        }
        if (isUpdatedTextValue(newCardNumber, card.getCardNumber())) {
            card.setCardNumber(trimToNull(newCardNumber));
            anyFieldChanged = true;
        }

        if (anyFieldChanged) {
            card.setLastModified(currentInstant);
            history.add(originalCard, card.copy());
        }

        return quantityChanged || anyFieldChanged;
    }

    public boolean addTag(int index, String tag) {
        assert index >= 0 && index < cards.size() : "Index should be validated before calling addTag";

        Card card = cards.get(index);
        Card originalCard = card.copy();
        boolean changed = card.addTag(tag);
        if (!changed) {
            return false;
        }

        card.setLastModified(Instant.now());
        history.add(originalCard, card.copy());
        return true;
    }

    public boolean removeTag(int index, String tag) {
        assert index >= 0 && index < cards.size() : "Index should be validated before calling removeTag";

        Card card = cards.get(index);
        Card originalCard = card.copy();
        boolean changed = card.removeTag(tag);
        if (!changed) {
            return false;
        }

        card.setLastModified(Instant.now());
        history.add(originalCard, card.copy());
        return true;
    }

    private static boolean isSameCardVariant(Card first, Card second) {
        return first.getName().equalsIgnoreCase(second.getName())
                && first.getPrice() == second.getPrice()
                && normalized(first.getCardSet()).equals(normalized(second.getCardSet()))
                && normalized(first.getRarity()).equals(normalized(second.getRarity()))
                && normalized(first.getCondition()).equals(normalized(second.getCondition()))
                && normalized(first.getLanguage()).equals(normalized(second.getLanguage()))
                && normalized(first.getCardNumber()).equals(normalized(second.getCardNumber()));
    }

    private static boolean containsIgnoreCase(String actualValue, String expectedFragment) {
        if (expectedFragment == null) {
            return true;
        }
        if (actualValue == null) {
            return false;
        }
        return actualValue.toLowerCase(Locale.ROOT).contains(expectedFragment.toLowerCase(Locale.ROOT));
    }

    private static boolean containsTagIgnoreCase(Card card, String expectedTag) {
        if (expectedTag == null) {
            return true;
        }
        return card.hasTag(expectedTag);
    }

    private static boolean isUpdatedTextValue(String candidate, String currentValue) {
        if (candidate == null) {
            return false;
        }
        return !normalized(candidate).equals(normalized(currentValue));
    }

    private static String normalized(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String normalizeSetName(String setName) {
        if (setName == null || setName.isBlank()) {
            return "Unspecified Set";
        }
        return setName.trim();
    }

    /**
     * Permanently reorders the main cards list (inventory or wishlist) in-place
     * by the given criteria. This changes storage order (unlike the view-only
     * getSortedCards methods).
     */
    public void reorder(CardSortCriteria criteria, boolean isAscending) {
        assert cards != null : "Cards list should be initialized before reordering";

        if (cards.isEmpty()) {
            return;
        }

        Comparator<Card> comparator = CardSort.getSortComparator(criteria);
        assert comparator != null : "No available comparator for criteria";

        if (!isAscending) {
            comparator = comparator.reversed();
        }

        cards.sort(comparator);

        assert cards.size() > 0 : "List should not be empty if it wasn't before reorder";
    }

    public CardsHistory getHistory() {
        return history;
    }
}
