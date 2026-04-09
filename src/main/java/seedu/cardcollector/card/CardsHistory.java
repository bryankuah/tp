package seedu.cardcollector.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CardsHistory {
    private final ArrayList<CardHistoryEntry> historyList;

    public CardsHistory() {
        historyList = new ArrayList<CardHistoryEntry>();
    }

    public CardsHistory(ArrayList<Card> flattenedCards) {
        historyList = new ArrayList<>();

        assert (flattenedCards.size() % 2 == 0) :
                "Size of flattened cards should be multiple of 2";

        for (int i = 0; i < flattenedCards.size() - 1; i += 2) {
            historyList.add(new CardHistoryEntry(
                    flattenedCards.get(i), flattenedCards.get(i + 1)));
        }
    }

    public ArrayList<Card> getFlattenedCards() {
        return historyList.stream()
                .flatMap(entry -> Stream.of(entry.getPrevious(), entry.getCurrent()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<CardHistoryEntry> getSortedHistoryList(boolean isDescending) {
        ArrayList<CardHistoryEntry> historyListCopy = new ArrayList<>(historyList);

        if (isDescending) {
            Collections.reverse(historyListCopy);
        }

        return historyListCopy;
    }

    /**
     * Creates a history entry from a previous and current card and adds it to the history.
     *
     * @param previous The card before the change.
     * @param current The card after the change.
     */
    public void add(Card previous, Card current) {
        CardHistoryEntry entry = new CardHistoryEntry(previous, current);
        historyList.add(entry);
    }

    /**
     * Adds the given history entry to the history.
     *
     * @param entry The history entry to add.
     */
    public void add(CardHistoryEntry entry) {
        historyList.add(entry);
    }

    public int getSize() {
        return historyList.size();
    }


    public CardsHistory copy() {
        CardsHistory newCardHistory = new CardsHistory();
        for (CardHistoryEntry entry: historyList) {
            newCardHistory.add(entry.copy());
        }
        return newCardHistory;
    }
}
