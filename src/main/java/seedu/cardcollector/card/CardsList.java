package seedu.cardcollector.card;

import seedu.cardcollector.util.Box;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;

public class CardsList {
    private final ArrayList<Card> cards;
    private CardsHistory history;
    private boolean isWishlist;

    public CardsList() {
        this.cards = new ArrayList<>();
        this.history = new CardsHistory();
        this.isWishlist = false;
    }

    public CardsList(ArrayList<Card> cards, CardsHistory history) {
        this.cards = new ArrayList<>(cards);
        this.history = history;
        this.isWishlist = false;
    }

    public void addCard(Card newCard) {
        assert newCard != null : "new card added should not be null";

        Instant currentInstant = Instant.now();

        for (int i = 0; i < cards.size(); i++) {
            Card existingCard = cards.get(i);
            if (isSameCardVariant(existingCard, newCard)) {
                // For duplicate handling, we reuse the editCard method,
                // which gracefully handle edge cases,
                // for example when new card has quantity = 0, which means no change occurred.
                int updatedQuantity = existingCard.getQuantity() + newCard.getQuantity();
                editCard(i, null, Box.of(updatedQuantity), null, null,
                        null, null, null, null, null);

                assert cards.contains(existingCard) : "Updated card must still be in the list";
                return;
            }
        }
        int sizeBefore = cards.size();

        newCard.setLastAdded(currentInstant);
        history.add(null, newCard.copy());

        cards.add(newCard);

        assert cards.size() == sizeBefore + 1 : "List size should increment by 1 after add";
        assert cards.get(cards.size() - 1).equals(newCard) : "Latest card should be at the end";
    }

    public void addCardAtIndex(int index, Card card) {
        assert card != null : "Card to re-insert should not be null";
        assert index >= 0 && index <= cards.size() : "Index out of bounds for re-insertion";

        Instant currentInstant = Instant.now();

        card.setLastAdded(currentInstant);
        history.add(null, card.copy());
        cards.add(index, card);
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
            history.add(removed.copy(),  null);

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
                history.add(removed.copy(),  null);

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

    public int getIndex(Card card) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i) == card) {
                return i;
            }
        }

        return -1;
    }

    public CardsAnalytics getAnalytics(int expensiveLimit, int topSetLimit) {
        int totalQuantity = 0;
        double totalValue = 0;

        Map<String, Integer> setCounts = new HashMap<>();
        Map<String, Double> setValues = new HashMap<>();

        int zeroPriceCards = 0;
        int lowPriceCards = 0;
        int mediumPriceCards = 0;
        int upperMidPriceCards = 0;
        int highPriceCards = 0;

        int cardsWithNotes = 0;
        int cardsWithSetInformation = 0;

        for (Card card : cards) {
            totalQuantity += card.getQuantity();
            totalValue += card.getPrice() * card.getQuantity();

            if (card.getCardSet() != null && !card.getCardSet().isBlank()) {
                String normalizedSetName = normalizeSetName(card.getCardSet());
                setCounts.merge(normalizedSetName, card.getQuantity(), Integer::sum);
                setValues.merge(normalizedSetName, (double) card.getPrice() * card.getQuantity(), Double::sum);
            }

            if (card.getPrice() <= 0) {
                zeroPriceCards++;
            } else if (card.getPrice() < 10) {
                lowPriceCards++;
            } else if (card.getPrice() < 50) {
                mediumPriceCards++;
            } else if (card.getPrice() < 100) {
                upperMidPriceCards++;
            } else {
                highPriceCards++;
            }

            if (card.getNote() != null && !card.getNote().isBlank()) {
                cardsWithNotes++;
            }

            if (card.getCardSet() != null && !card.getCardSet().isBlank()) {
                cardsWithSetInformation++;
            }
        }

        ArrayList<CardsAnalytics.CardMetric> mostExpensiveCards = cards.stream()
                .sorted(Comparator.comparingDouble(Card::getPrice)
                        .reversed()
                        .thenComparing(Card::getName, String.CASE_INSENSITIVE_ORDER))
                .limit(expensiveLimit)
                .map(card -> new CardsAnalytics.CardMetric(card, card.getPrice() * card.getQuantity()))
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<CardsAnalytics.CardMetric> topCardsByHoldingValue = cards.stream()
                .sorted(Comparator.comparingDouble((Card card) -> card.getPrice() * card.getQuantity())
                        .reversed()
                        .thenComparing(Card::getName, String.CASE_INSENSITIVE_ORDER))
                .limit(expensiveLimit)
                .map(card -> new CardsAnalytics.CardMetric(card, card.getPrice() * card.getQuantity()))
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<CardsAnalytics.CardMetric> cheapestCards = cards.stream()
                .sorted(Comparator.comparingDouble(Card::getPrice)
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

        ArrayList<CardsAnalytics.SetValueMetric> topSetsByValue = setValues.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER)))
                .limit(topSetLimit)
                .map(entry -> new CardsAnalytics.SetValueMetric(entry.getKey(), entry.getValue()))
                .collect(Collectors.toCollection(ArrayList::new));

        return new CardsAnalytics(
                cards.size(),
                totalQuantity,
                totalValue,
                mostExpensiveCards,
                topCardsByHoldingValue,
                cheapestCards,
                topSetsByCount,
                topSetsByValue,
                zeroPriceCards,
                lowPriceCards,
                mediumPriceCards,
                upperMidPriceCards,
                highPriceCards,
                cardsWithNotes,
                cardsWithSetInformation
        );
    }

    public int getSize() {
        return cards.size();
    }

    public void replaceWith(CardsList other) {
        assert other != null : "Replacement list should not be null";

        cards.clear();
        cards.addAll(other.getCards());
        history = other.getHistory().copy();
        isWishlist = other.isWishlist();
    }

    public CardsList deepCopy() {
        Map<java.util.UUID, Card> copiedCards = new HashMap<>();
        CardsList copy = new CardsList(
                copyCards(cards, copiedCards),
                history.copy()
        );
        copy.setWishlist(isWishlist);
        return copy;
    }

    private static ArrayList<Card> copyCards(ArrayList<Card> source, Map<java.util.UUID, Card> copiedCards) {
        ArrayList<Card> result = new ArrayList<>();
        for (Card card : source) {
            Card copy = copiedCards.computeIfAbsent(card.getUid(), ignored -> card.copy());
            result.add(copy);
        }
        return result;
    }

    //@@author bryankuah
    public ArrayList<Card> findCards(String name, NumericFilter quantityFilter, NumericFilter priceFilter,
                                     String cardSet, String rarity, String condition,
                                     String language, String cardNumber, String note, String tag) {

        assert cards != null : "Cards inventory should be initialized before searching";

        ArrayList<Card> results = new ArrayList<>();
        for (Card card : cards) {
            boolean matches = true;

            if (name != null && !card.getName().toLowerCase().contains(name.toLowerCase())) {
                matches = false;
            }

            if (quantityFilter != null && !quantityFilter.matches(card.getQuantity())) {
                matches = false;
            }

            if (priceFilter != null && !priceFilter.matches(card.getPrice())) {
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

            if (!containsIgnoreCase(card.getNote(), note)) {
                matches = false;
            }

            if (!containsTagIgnoreCase(card, tag)) {
                matches = false;
            }

            if (matches) {
                results.add(card);
            }
        }

        return results;
    }

    public ArrayList<Card> getDuplicateCards() {
        ArrayList<Card> duplicates = new ArrayList<>();

        for (Card card : cards) {
            if (card.getQuantity() > 1) {
                duplicates.add(card);
            }
        }

        return duplicates;
    }

    public boolean editCard(int index, Box<String> newName, Box<Integer> newQuantity, Box<Float> newPrice,
                            Box<String> newCardSet, Box<String> newRarity, Box<String> newCondition,
                            Box<String> newLanguage, Box<String> newCardNumber, Box<String> newNote) {
        assert index >= 0 && index < cards.size() : "Index should be validated before calling editCard";

        Card card = cards.get(index);

        Instant currentInstant = Instant.now();
        boolean quantityChanged = false;

        // Special handling for quantity
        if (newQuantity != null) {
            Card originalCard = card.copy();
            int previousQuantity = originalCard.getQuantity();

            if (newQuantity.get() > previousQuantity) {
                quantityChanged = true;
                card.setQuantity(newQuantity.get());

                card.setLastAdded(currentInstant);
                history.add(originalCard, card.copy());
            } else if (newQuantity.get() < previousQuantity) {
                quantityChanged = true;
                card.setQuantity(newQuantity.get());

                card.setLastRemoved(currentInstant);
                history.add(originalCard, card.copy());
            }
        }

        // Normal handling for the rest of the fields
        Card originalCard = card.copy();
        boolean anyFieldChanged = false;

        if (isUpdatedValue(card.getName(), newName)) {
            card.setName(safeTrim(newName.get()));
            anyFieldChanged = true;
        }
        if (isUpdatedValue(card.getPrice(), newPrice)) {
            card.setPrice(newPrice.get());
            anyFieldChanged = true;
        }
        if (isUpdatedValue(card.getCardSet(), newCardSet)) {
            card.setCardSet(safeTrim(newCardSet.get()));
            anyFieldChanged = true;
        }
        if (isUpdatedValue(card.getRarity(), newRarity)) {
            card.setRarity(safeTrim(newRarity.get()));
            anyFieldChanged = true;
        }
        if (isUpdatedValue(card.getCondition(), newCondition)) {
            card.setCondition(safeTrim(newCondition.get()));
            anyFieldChanged = true;
        }
        if (isUpdatedValue(card.getLanguage(), newLanguage)) {
            card.setLanguage(safeTrim(newLanguage.get()));
            anyFieldChanged = true;
        }
        if (isUpdatedValue(card.getCardNumber(), newCardNumber)) {
            card.setCardNumber(safeTrim(newCardNumber.get()));
            anyFieldChanged = true;
        }

        if (isUpdatedValue(card.getNote(), newNote)) {
            card.setNote(safeTrim(newNote.get()));
            anyFieldChanged = true;
        }

        if (anyFieldChanged) {
            card.setLastModified(currentInstant);
            history.add(originalCard, card.copy());
            mergeIfDuplicateAfterEdit(index);
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
                && normalized(first.getCardNumber()).equals(normalized(second.getCardNumber()))
                && normalized(first.getNote()).equals(normalized(second.getNote()));
    }

    private void mergeIfDuplicateAfterEdit(int editedIndex) {
        if (editedIndex < 0 || editedIndex >= cards.size()) {
            return;
        }

        Card editedCard = cards.get(editedIndex);

        for (int i = 0; i < cards.size(); i++) {
            if (i == editedIndex) {
                continue;
            }
            Card otherCard = cards.get(i);
            if (isSameCardVariant(otherCard, editedCard)) {
                // Merge quantity into the surviving card (reuses existing editCard logic)
                int updatedQuantity = otherCard.getQuantity() + editedCard.getQuantity();
                editCard(i, null, Box.of(updatedQuantity), null, null,
                        null, null, null, null, null);

                // Remove the now-duplicate edited card (history records the removal)
                removeCardByIndex(editedIndex);

                assert !cards.contains(editedCard) : "Merged duplicate card should have been removed";
                return; // only merge with the first match
            }
        }
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

    private static <T> boolean isUpdatedValue(T previous, Box<T> current) {
        if (current == null) {
            return false;
        }

        if (previous == null && current.get() == null) {
            return false;
        }

        if (previous == null || current.get() == null) {
            return true;
        }

        if (previous instanceof String && current.get() instanceof String) {
            String previousTrimmed = ((String) previous).trim();
            String currentTrimmed = ((String) current.get()).trim();

            return !previousTrimmed.equals(currentTrimmed);
        }

        return !previous.equals(current.get());
    }

    private static String normalized(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static String safeTrim(String value) {
        if (value == null) {
            return null;
        }
        return value.trim();
    }

    private static String normalizeSetName(String setName) {
        if (setName == null || setName.isBlank()) {
            return "Unspecified Set";
        }
        return setName.trim();
    }

    /**
     * Permanently reorders the main cards list (inventory or wishlist) in-place
     * by the given criteria. This changes storage order.
     */
    public void reorder(CardSortCriteria criteria, boolean isAscending) {
        assert cards != null : "Cards list should be initialized before reordering";

        if (cards.isEmpty()) {
            return;
        }

        if (criteria == CardSortCriteria.INDEX && !isAscending) {
            Collections.reverse(cards);
        }

        if (criteria != CardSortCriteria.INDEX) {
            Comparator<Card> comparator = CardSorter.getSortComparator(criteria);

            assert comparator != null : "No available comparator for criteria";

            if (!isAscending) {
                comparator = comparator.reversed();
            }

            cards.sort(comparator);
        }

        assert cards.size() > 0 : "List should not be empty if it wasn't before reorder";
    }

    //@@author WeiHeng2003
    public ArrayList<Integer> getIndicesByName (String name) {
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getName().equalsIgnoreCase(name)) {
                indices.add(i);
            }
        }
        return indices;
    }

    public boolean isWishlist() {
        return isWishlist;
    }

    public void setWishlist(boolean isWishlist) {
        this.isWishlist = isWishlist;
    }

    public CardsHistory getHistory() {
        return history;
    }

    public void clear() {
        assert cards != null : "Cards list should be initialized";
        cards.clear();
        history = new CardsHistory();  // reset history for empty list (undo restores old state anyway)
    }
}
