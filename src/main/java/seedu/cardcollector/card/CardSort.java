package seedu.cardcollector.card;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class CardSort {
    public static Comparator<Card> getSortComparator(CardSortCriteria criteria) {
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
                    Comparator.nullsFirst(Instant::compareTo));
        }
        case LAST_MODIFIED -> {
            return Comparator.comparing(Card::getLastModified,
                    Comparator.nullsFirst(Instant::compareTo));
        }
        case LAST_REMOVED -> {
            return Comparator.comparing(Card::getLastRemoved,
                    Comparator.nullsFirst(Instant::compareTo));
        }
        default -> {
            assert false : "Unhandled CardSortCriteria";
        }
        }
        return null;
    }

    public static ArrayList<Card> sortCards(
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
}
