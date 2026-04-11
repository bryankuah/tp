package seedu.cardcollector.card;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.StringJoiner;

/**
 * Records a change between two cards (previous and current).
 * Determines the type of change (ADDED, REMOVED, MODIFIED),
 * based on nullity, quantity, or other fields difference.
 * <p>
 * Note that a single {@code CardHistoryEntry} is unable to fully record
 * when both quantity and other fields changes.
 */
public class CardHistoryEntry {
    private final CardHistoryType cardHistoryType;

    private final Card previous;
    private final Card current;

    /**
     * Creates a history entry from a previous and current card state.
     * Both parameters may be null to represent creation or deletion.
     *
     * @param previous The card before the change (can be null).
     * @param current  The card after the change (can be null).
     */
    CardHistoryEntry(Card previous, Card current) {
        if (previous != null && current != null) {
            assert previous != current : "Cards in history should not share the same reference";
        }

        this.previous = previous;
        this.current = current;

        if (previous == null && current != null) {
            // Indicates ADDED case irrespective of the quantity,
            // even if the quantity is 0
            this.cardHistoryType = CardHistoryType.ADDED;
            return;
        }

        if (previous != null && current == null) {
            // Indicates REMOVED case irrespective of the quantity,
            // even if the quantity is originally 0
            this.cardHistoryType = CardHistoryType.REMOVED;
            return;
        }

        int changedQuantity = getChangedQuantity();
        if (changedQuantity > 0) {
            this.cardHistoryType = CardHistoryType.ADDED;
        } else if (changedQuantity < 0) {
            this.cardHistoryType = CardHistoryType.REMOVED;
        } else {
            this.cardHistoryType = CardHistoryType.MODIFIED;
        }
    }

    /**
     * Returns a deep copy of this history entry.
     *
     * @return A new CardHistoryEntry with copied card objects.
     */
    public CardHistoryEntry copy() {
        Card copyOfPrevious = null;
        Card copyOfCurrent = null;

        if (previous != null) {
            copyOfPrevious = previous.copy();
        }

        if (current != null) {
            copyOfCurrent = current.copy();
        }

        return new CardHistoryEntry(copyOfPrevious, copyOfCurrent);
    }

    /**
     * Returns the previous card state.
     *
     * @return The card before the change, or null if the card was newly added.
     */
    public Card getPrevious() {
        return previous;
    }

    /**
     * Returns the current card state.
     *
     * @return The card after the change, or null if the card was removed.
     */
    public Card getCurrent() {
        return current;
    }

    /**
     * Returns the most recent non-null card.
     *
     * @return The most recent non-null card.
     */
    public Card getMostRecent() {
        Card current = getCurrent();

        if (current != null) {
            return current;
        }

        return previous;
    }

    /**
     * Returns the type of change this entry represents.
     *
     * @return The card history type.
     */
    public CardHistoryType getCardHistoryType() {
        return cardHistoryType;
    }

    /**
     * Returns the net change in card quantity.
     *
     * @return An integer value of number of cards changed.
     */
    public int getChangedQuantity() {
        int currentQuantity = 0;
        int previousQuantity = 0;

        if (current != null) {
            currentQuantity = current.getQuantity();
        }

        if (previous != null) {
            previousQuantity = previous.getQuantity();
        }

        return currentQuantity - previousQuantity;
    }

    /**
     * Returns a map of field names to their changes,
     * but is only applicable for MODIFIED entries.
     *
     * @return The changed fields with field names.
     */
    public LinkedHashMap<String, CardFieldChange> getChangedFields() {
        assert previous != null;
        assert current != null;
        assert cardHistoryType == CardHistoryType.MODIFIED;

        LinkedHashMap<String, CardFieldChange> changedFields = new LinkedHashMap<>();

        float previousPrice = previous.getPrice();
        float currentPrice = current.getPrice();
        if (currentPrice != previousPrice) {
            CardFieldChange change = new CardFieldChange(
                    String.valueOf(previousPrice), String.valueOf(currentPrice));
            changedFields.put("price", change);
        }

        addTextChange(changedFields, "name", previous.getName(), current.getName());
        addTextChange(changedFields, "set", previous.getCardSet(), current.getCardSet());
        addTextChange(changedFields, "rarity", previous.getRarity(), current.getRarity());
        addTextChange(changedFields, "condition", previous.getCondition(), current.getCondition());
        addTextChange(changedFields, "language", previous.getLanguage(), current.getLanguage());
        addTextChange(changedFields, "card number", previous.getCardNumber(), current.getCardNumber());
        addTextChange(changedFields, "tags", formatTags(previous.getTags()), formatTags(current.getTags()));
        addTextChange(changedFields, "note", previous.getNote(), current.getNote());

        return changedFields;
    }

    private static void addTextChange(LinkedHashMap<String, CardFieldChange> changedFields,
            String fieldName, String previousValue, String currentValue) {
        String normalizedPrevious = previousValue == null ? "" : previousValue;
        String normalizedCurrent = currentValue == null ? "" : currentValue;
        if (!normalizedCurrent.equals(normalizedPrevious)) {
            changedFields.put(fieldName, new CardFieldChange(displayValue(previousValue), displayValue(currentValue)));
        }
    }

    private static String displayValue(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private static String formatTags(Collection<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }

        StringJoiner joiner = new StringJoiner(", ");
        for (String tag : tags) {
            joiner.add(tag);
        }
        return joiner.toString();
    }
}
