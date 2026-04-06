package seedu.cardcollector.card;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardSorterTest {
    //@@author HX2003
    @Test
    public void sort_byIndex_success() {
        ArrayList<Card> cards = new ArrayList<>();

        Card.Builder commonBuilder = new Card.Builder()
                .price(1.00f)
                .quantity(1);

        Card card0 = commonBuilder.name("Zero").build();
        Card card1 = commonBuilder.name("One").build();

        cards.add(card0);
        cards.add(card1);

        ArrayList<Card> resultsDescending = CardSorter.sort(
                cards, CardSortCriteria.INDEX, -1, Integer.MAX_VALUE, true);
        assertEquals(card1, resultsDescending.get(0));
        assertEquals(card0, resultsDescending.get(1));

        ArrayList<Card> resultsAscending = CardSorter.sort(
                cards, CardSortCriteria.INDEX, -1, Integer.MAX_VALUE, false);
        assertEquals(card0, resultsAscending.get(0));
        assertEquals(card1, resultsAscending.get(1));

        // Test whether the limits work too
        ArrayList<Card> resultsDescendingDefaultLimited = CardSorter.sort(
                cards, CardSortCriteria.INDEX, -1, 1, true);
        assertEquals(card1, resultsDescendingDefaultLimited.get(0));
        assertEquals(1, resultsDescendingDefaultLimited.size());

        ArrayList<Card> resultsDescendingNonDefaultLimited = CardSorter.sort(
                cards, CardSortCriteria.INDEX, 1, Integer.MAX_VALUE, true);
        assertEquals(card1, resultsDescendingNonDefaultLimited.get(0));
        assertEquals(1, resultsDescendingNonDefaultLimited.size());
    }

    @Test
    public void sort_byPrice_success() {
        ArrayList<Card> cards = new ArrayList<>();

        Card cheapCard = new Card.Builder()
                .name("Cheap card")
                .price(2.00f)
                .quantity(1)
                .build();
        Card expensiveCard = new Card.Builder()
                .name("Expensive card")
                .price(888.00f)
                .quantity(2)
                .build();
        Card moderateCard = new Card.Builder()
                .name("Moderate card")
                .price(50)
                .quantity(3)
                .build();

        cards.add(cheapCard);
        cards.add(expensiveCard);
        cards.add(moderateCard);

        ArrayList<Card> resultsDescending = CardSorter.sort(
                cards, CardSortCriteria.PRICE, -1, Integer.MAX_VALUE, true);
        assertEquals(expensiveCard, resultsDescending.get(0));
        assertEquals(moderateCard, resultsDescending.get(1));
        assertEquals(cheapCard, resultsDescending.get(2));


        ArrayList<Card> resultsAscending = CardSorter.sort(
                cards, CardSortCriteria.PRICE, -1, Integer.MAX_VALUE, false);
        assertEquals(expensiveCard, resultsAscending.get(2));
        assertEquals(moderateCard, resultsAscending.get(1));
        assertEquals(cheapCard, resultsAscending.get(0));
    }

    @Test
    public void sort_byLastRemoved_success() {
        ArrayList<Card> cards = new ArrayList<>();

        Instant instantNewest = Instant.parse("2026-03-27T21:58:02Z");
        Instant instantOlder = instantNewest.minus(1, ChronoUnit.DAYS);
        Instant instantOldest = instantOlder.minus(1, ChronoUnit.DAYS);

        Card newestCard = new Card.Builder()
                .name("Newest last removed card")
                .price(2.00f)
                .quantity(1)
                .lastRemoved(instantNewest)
                .build();
        Card olderCard = new Card.Builder()
                .name("Older last removed card")
                .price(888.00f)
                .quantity(2)
                .lastRemoved(instantOlder)
                .build();
        Card oldestCard = new Card.Builder()
                .name("Oldest last removed card")
                .price(50)
                .quantity(3)
                .lastRemoved(instantOldest)
                .build();
        Card unspecifiedCard = new Card.Builder()
                .name("Unspecified last removed card")
                .price(70.f)
                .quantity(1)
                .build();

        cards.add(olderCard);
        cards.add(unspecifiedCard);
        cards.add(oldestCard);
        cards.add(newestCard);

        ArrayList<Card> resultsDescending = CardSorter.sort(
                cards, CardSortCriteria.REMOVED, -1, Integer.MAX_VALUE, true);

        assertEquals(newestCard, resultsDescending.get(0));
        assertEquals(olderCard, resultsDescending.get(1));
        assertEquals(oldestCard, resultsDescending.get(2));
        assertEquals(unspecifiedCard, resultsDescending.get(3));
    }
}
