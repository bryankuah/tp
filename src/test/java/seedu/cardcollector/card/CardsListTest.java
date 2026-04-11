package seedu.cardcollector.card;

import org.junit.jupiter.api.Test;
import seedu.cardcollector.util.Box;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CardsListTest {

    //@@author bryankuah
    @Test
    public void addCard_card_success() {
        CardsList cardsList = new CardsList();

        Card card = new Card.Builder()
                .name("Pikachu")
                .price(5.50f)
                .quantity(1)
                .build();
        cardsList.addCard(card);

        assertEquals(1, cardsList.getSize());
        assertEquals(card, cardsList.getCard(0));
    }

    @Test
    public void findCards_byName_success() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card.Builder()
                .name("Pikachu")
                .price(5.50f)
                .quantity(1)
                .build());
        cardsList.addCard(new Card.Builder()
                .name("Charizard")
                .price(15.00f)
                .quantity(2)
                .build());
        cardsList.addCard(new Card.Builder()
                .name("Pikachu VMAX")
                .price(20.00f)
                .quantity(3)
                .build());

        ArrayList<Card> results = cardsList.findCards(
                "pika", null, null, null, null, null, null, null, null, null);

        assertEquals(2, results.size());
        assertEquals("Pikachu", results.get(0).getName());
        assertEquals("Pikachu VMAX", results.get(1).getName());
    }

    @Test
    public void findCards_byPrice_success() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card.Builder()
                .name("Pikachu")
                .price(5.50f)
                .quantity(1)
                .build());
        cardsList.addCard(new Card.Builder()
                .name("Charizard")
                .price(10.00f)
                .quantity(2)
                .build());

        ArrayList<Card> results = cardsList.findCards(
                null, null, NumericFilter.parse("10.0"), null, null, null, null, null, null, null);

        assertEquals(1, results.size());
        assertEquals("Charizard", results.get(0).getName());
    }

    @Test
    public void findCards_byQuantity_success() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card.Builder()
                .name("Bulbasaur")
                .price(2.00f)
                .quantity(5)
                .build());
        cardsList.addCard(new Card.Builder()
                .name("Squirtle")
                .price(3.00f)
                .quantity(1)
                .build());

        ArrayList<Card> results = cardsList.findCards(
                null, NumericFilter.parse("5"), null, null, null, null, null, null, null, null);

        assertEquals(1, results.size());
        assertEquals("Bulbasaur", results.get(0).getName());
    }

    @Test
    public void findCards_multipleAttributes_success() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card.Builder()
                .name("Mewtwo")
                .price(20.00f)
                .quantity(3)
                .build());
        cardsList.addCard(new Card.Builder()
                .name("Mewtwo")
                .price(5.00f)
                .quantity(1)
                .build());
        cardsList.addCard(new Card.Builder()
                .name("Mew")
                .price(15.00f)
                .quantity(3)
                .build());

        ArrayList<Card> results = cardsList.findCards(
                "Mew", NumericFilter.parse("3"), null, null, null, null, null, null, null, null);

        assertEquals(2, results.size());
        assertEquals(20.00f, results.get(0).getPrice());
        assertEquals(15.00f, results.get(1).getPrice());
    }

    @Test
    public void findCards_noMatch_returnsEmptyList() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card.Builder()
                .name("Eevee")
                .price(4.00f)
                .quantity(1)
                .build());

        ArrayList<Card> results = cardsList.findCards(
                "Snorlax", null, NumericFilter.parse("100.0"), null, null, null, null, null, null, null);

        assertEquals(0, results.size());
    }

    @Test
    public void editCard_partialAndFullEdit_success() {
        CardsList cardsList = new CardsList();

        Card original = new Card.Builder()
                .name("Pikachu")
                .price(5.50f)
                .quantity(1)
                .build();
        cardsList.addCard(original);

        cardsList.editCard(0, Box.of("Pikachu VMAX"), Box.of(5), null,
                null, null, null, null, null, null);
        assertEquals("Pikachu VMAX", cardsList.getCard(0).getName());
        assertEquals(5, cardsList.getCard(0).getQuantity());
        assertEquals(5.50f, cardsList.getCard(0).getPrice());

        cardsList.editCard(0, Box.of("Charizard"), Box.of(10), Box.of(25.0f),
                null, null, null, null, null, null);
        assertEquals("Charizard", cardsList.getCard(0).getName());
        assertEquals(10, cardsList.getCard(0).getQuantity());
        assertEquals(25.0f, cardsList.getCard(0).getPrice());
    }

    @Test
    public void addAndFindCards_withMetadata_success() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card.Builder()
                .name("Pikachu")
                .price(5.50f)
                .quantity(1)
                .cardSet("Base Set")
                .rarity("Rare")
                .condition("Near Mint")
                .language("English")
                .cardNumber("58/102")
                .build());
        cardsList.addCard(new Card.Builder()
                .name("Pikachu")
                .price(5.50f)
                .quantity(1)
                .cardSet("Jungle")
                .rarity("Common")
                .condition("Played")
                .language("Japanese")
                .cardNumber("12/64")
                .build());

        ArrayList<Card> results = cardsList.findCards(
                null, null, null, "base", "rare", "near", "english", "58/102", null, null);

        assertEquals(1, results.size());
        assertEquals("Base Set", results.get(0).getCardSet());
    }

    @Test
    public void addTagRemoveTagAndFindByTag_success() {
        CardsList cardsList = new CardsList();
        cardsList.addCard(new Card.Builder()
                .name("Pikachu")
                .price(5.50f)
                .quantity(1)
                .build());
        cardsList.addCard(new Card.Builder()
                .name("Charizard")
                .price(99.99f)
                .quantity(1)
                .build());

        assertTrue(cardsList.addTag(0, "deck"));
        assertFalse(cardsList.addTag(0, "deck"));
        assertTrue(cardsList.getCard(0).hasTag("DECK"));

        ArrayList<Card> taggedResults = cardsList.findCards(
                null, null, null, null, null, null, null, null, null, "deck");
        assertEquals(1, taggedResults.size());
        assertEquals("Pikachu", taggedResults.get(0).getName());

        assertTrue(cardsList.removeTag(0, "deck"));
        assertFalse(cardsList.getCard(0).hasTag("deck"));
    }

    @Test
    public void findCards_byNote_success() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card.Builder()
                .name("Pikachu")
                .price(5.50f)
                .quantity(1)
                .note("childhood favorite")
                .build());

        cardsList.addCard(new Card.Builder()
                .name("Charizard")
                .price(20.00f)
                .quantity(1)
                .note("trade binder")
                .build());

        ArrayList<Card> results = cardsList.findCards(
                null, null, null, null, null, null, null, null, "favorite", null);

        assertEquals(1, results.size());
        assertEquals("Pikachu", results.get(0).getName());
    }

    @Test
    public void editCard_note_success() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card.Builder()
                .name("Bulbasaur")
                .price(3.00f)
                .quantity(1)
                .note("starter")
                .build());

        boolean changed = cardsList.editCard(0, null, null, null,
                null, null, null, null,
                null, Box.of("starter deck"));

        assertTrue(changed);
        assertEquals("starter deck", cardsList.getCard(0).getNote());
    }

    @Test
    public void getDuplicateCards_success() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card.Builder()
                .name("Eevee")
                .price(4.00f)
                .quantity(2)
                .build());

        cardsList.addCard(new Card.Builder()
                .name("Mew")
                .price(30.00f)
                .quantity(1)
                .build());

        cardsList.addCard(new Card.Builder()
                .name("Squirtle")
                .price(3.00f)
                .quantity(3)
                .build());

        ArrayList<Card> duplicates = cardsList.getDuplicateCards();

        assertEquals(2, duplicates.size());
        assertEquals("Eevee", duplicates.get(0).getName());
        assertEquals("Squirtle", duplicates.get(1).getName());
    }

    @Test
    public void addCard_sameCardDifferentNote_doesNotMerge() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card.Builder()
                .name("Pikachu")
                .price(5.50f)
                .quantity(1)
                .note("gift")
                .build());

        cardsList.addCard(new Card.Builder()
                .name("Pikachu")
                .price(5.50f)
                .quantity(1)
                .note("trade")
                .build());

        assertEquals(2, cardsList.getSize());
        assertEquals("gift", cardsList.getCard(0).getNote());
        assertEquals("trade", cardsList.getCard(1).getNote());
    }

    @Test
    public void addCard_sameCardSameNote_merges() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card.Builder()
                .name("Charmander")
                .price(6.00f)
                .quantity(1)
                .note("binder")
                .build());

        cardsList.addCard(new Card.Builder()
                .name("Charmander")
                .price(6.00f)
                .quantity(2)
                .note("binder")
                .build());

        assertEquals(1, cardsList.getSize());
        assertEquals(3, cardsList.getCard(0).getQuantity());
        assertEquals("binder", cardsList.getCard(0).getNote());
    }

    @Test
    public void inventoryAndWishlistAreIndependent_success() {
        CardsList inventory = new CardsList();
        CardsList wishlist = new CardsList();

        Card invCard = new Card.Builder()
                .name("Pikachu")
                .price(5.5f)
                .quantity(1)
                .build();
        inventory.addCard(invCard);

        Card wishCard = new Card.Builder()
                .name("Charizard")
                .price(99.99f)
                .quantity(1)
                .build();
        wishlist.addCard(wishCard);

        assertEquals(1, inventory.getSize());
        assertEquals(1, wishlist.getSize());
        assertEquals("Pikachu", inventory.getCard(0).getName());
        assertEquals("Charizard", wishlist.getCard(0).getName());

        inventory.removeCardByIndex(0);
        assertEquals(0, inventory.getSize());
        assertEquals(1, wishlist.getSize());
    }

    @Test
    public void getTwoDifferentCardsForComparison_success() {
        CardsList cardsList = new CardsList();

        Card card1 = new Card.Builder()
                .name("Pikachu")
                .price(5.5f)
                .quantity(1)
                .build();
        Card card2 = new Card.Builder()
                .name("Charizard")
                .price(99.99f)
                .quantity(1)
                .build();

        cardsList.addCard(card1);
        cardsList.addCard(card2);

        assertEquals("Pikachu | Quantity: 1 | Price: 5.5", cardsList.getCard(0).toString());
        assertEquals("Charizard | Quantity: 1 | Price: 99.99", cardsList.getCard(1).toString());
    }

    @Test
    public void acquired_movesCardFromWishlistToInventory_success() {
        CardsList inventory = new CardsList();
        CardsList wishlist = new CardsList();

        Card wishCard = new Card.Builder()
                .name("Charizard")
                .price(99.99f)
                .quantity(1)
                .build();
        wishlist.addCard(wishCard);

        int index = 0;
        Card card = wishlist.getCard(index);
        wishlist.removeCardByIndex(index);
        inventory.addCard(card);

        assertEquals(0, wishlist.getSize());
        assertEquals(1, inventory.getSize());
        assertEquals("Charizard", inventory.getCard(0).getName());
    }

    @Test
    public void reorder_byNameAscending_success() {
        CardsList cardsList = new CardsList();

        Card cardZ = new Card.Builder().name("Zebra").price(10).quantity(1).build();
        Card cardA = new Card.Builder().name("Apple").price(20).quantity(1).build();
        Card cardM = new Card.Builder().name("Monkey").price(15).quantity(1).build();

        cardsList.addCard(cardZ);
        cardsList.addCard(cardA);
        cardsList.addCard(cardM);

        cardsList.reorder(CardSortCriteria.NAME, true);

        ArrayList<Card> ordered = cardsList.getCards();
        assertEquals("Apple", ordered.get(0).getName());
        assertEquals("Monkey", ordered.get(1).getName());
        assertEquals("Zebra", ordered.get(2).getName());
    }

    @Test
    public void reorder_byPriceDescending_success() {
        CardsList cardsList = new CardsList();

        Card cardLow = new Card.Builder().name("Low").price(5).quantity(1).build();
        Card cardHigh = new Card.Builder().name("High").price(100).quantity(1).build();
        Card cardMid = new Card.Builder().name("Mid").price(50).quantity(1).build();

        cardsList.addCard(cardLow);
        cardsList.addCard(cardHigh);
        cardsList.addCard(cardMid);

        cardsList.reorder(CardSortCriteria.PRICE, false);

        ArrayList<Card> ordered = cardsList.getCards();
        assertEquals("High", ordered.get(0).getName());
        assertEquals("Mid", ordered.get(1).getName());
        assertEquals("Low", ordered.get(2).getName());
    }

    @Test
    public void getAnalytics_populatedList_success() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card.Builder()
                .name("Charizard")
                .price(100.0f)
                .quantity(2)
                .cardSet("Base Set")
                .build());
        cardsList.addCard(new Card.Builder()
                .name("Blastoise")
                .price(80.0f)
                .quantity(1)
                .cardSet("Base Set")
                .note("starter")
                .build());
        cardsList.addCard(new Card.Builder()
                .name("Pikachu")
                .price(20.0f)
                .quantity(4)
                .cardSet("Jungle")
                .build());
        cardsList.addCard(new Card.Builder()
                .name("Mew")
                .price(90.0f)
                .quantity(1)
                .build());

        CardsAnalytics analytics = cardsList.getAnalytics(3, 3);

        assertEquals(4, analytics.getDistinctCards());
        assertEquals(8, analytics.getTotalQuantity());
        assertEquals(450.0, analytics.getTotalValue(), 0.001);

        assertEquals(3, analytics.getMostExpensiveCards().size());
        assertEquals("Charizard", analytics.getMostExpensiveCards().get(0).getCard().getName());
        assertEquals("Mew", analytics.getMostExpensiveCards().get(1).getCard().getName());
        assertEquals("Blastoise", analytics.getMostExpensiveCards().get(2).getCard().getName());
        assertEquals(200.0, analytics.getMostExpensiveCards().get(0).getLineValue(), 0.001);

        assertEquals(3, analytics.getTopCardsByHoldingValue().size());
        assertEquals("Charizard", analytics.getTopCardsByHoldingValue().get(0).getCard().getName());
        assertEquals(200.0, analytics.getTopCardsByHoldingValue().get(0).getLineValue(), 0.001);
        assertEquals("Mew", analytics.getTopCardsByHoldingValue().get(1).getCard().getName());
        assertEquals(90.0, analytics.getTopCardsByHoldingValue().get(1).getLineValue(), 0.001);
        assertEquals("Blastoise", analytics.getTopCardsByHoldingValue().get(2).getCard().getName());
        assertEquals(80.0, analytics.getTopCardsByHoldingValue().get(2).getLineValue(), 0.001);

        assertEquals(3, analytics.getCheapestCards().size());
        assertEquals("Pikachu", analytics.getCheapestCards().get(0).getCard().getName());
        assertEquals("Blastoise", analytics.getCheapestCards().get(1).getCard().getName());
        assertEquals("Mew", analytics.getCheapestCards().get(2).getCard().getName());

        assertEquals(2, analytics.getTopSetsByCount().size());
        assertEquals("Jungle", analytics.getTopSetsByCount().get(0).getSetName());
        assertEquals(4, analytics.getTopSetsByCount().get(0).getTotalCount());
        assertEquals("Base Set", analytics.getTopSetsByCount().get(1).getSetName());
        assertEquals(3, analytics.getTopSetsByCount().get(1).getTotalCount());

        assertEquals(2, analytics.getTopSetsByValue().size());
        assertEquals("Base Set", analytics.getTopSetsByValue().get(0).getSetName());
        assertEquals(280.0, analytics.getTopSetsByValue().get(0).getTotalValue(), 0.001);
        assertEquals("Jungle", analytics.getTopSetsByValue().get(1).getSetName());
        assertEquals(80.0, analytics.getTopSetsByValue().get(1).getTotalValue(), 0.001);

        assertEquals(0, analytics.getZeroPriceCards());
        assertEquals(0, analytics.getLowPriceCards());
        assertEquals(1, analytics.getMediumPriceCards());
        assertEquals(2, analytics.getUpperMidPriceCards());
        assertEquals(1, analytics.getHighPriceCards());

        assertEquals(1, analytics.getCardsWithNotes());
        assertEquals(3, analytics.getCardsWithSetInformation());
    }

    @Test
    public void getAnalytics_emptyList_success() {
        CardsList cardsList = new CardsList();

        CardsAnalytics analytics = cardsList.getAnalytics(3, 3);

        assertEquals(0, analytics.getDistinctCards());
        assertEquals(0, analytics.getTotalQuantity());
        assertEquals(0.0, analytics.getTotalValue(), 0.001);
        assertTrue(analytics.getMostExpensiveCards().isEmpty());
        assertTrue(analytics.getTopCardsByHoldingValue().isEmpty());
        assertTrue(analytics.getCheapestCards().isEmpty());
        assertTrue(analytics.getTopSetsByCount().isEmpty());
        assertTrue(analytics.getTopSetsByValue().isEmpty());

        assertEquals(0, analytics.getZeroPriceCards());
        assertEquals(0, analytics.getLowPriceCards());
        assertEquals(0, analytics.getMediumPriceCards());
        assertEquals(0, analytics.getUpperMidPriceCards());
        assertEquals(0, analytics.getHighPriceCards());

        assertEquals(0, analytics.getCardsWithNotes());
        assertEquals(0, analytics.getCardsWithSetInformation());
    }

    //@@author HX2003
    @Test
    public void cardsList_addEditRemove_historySuccess() {
        CardsList cardsList = new CardsList();

        Card card0 = new Card.Builder().name("Zero").price(5.0f).quantity(1).build();
        Card card1 = new Card.Builder().name("One").price(15.0f).quantity(5).build();

        cardsList.addCard(card0);
        cardsList.addCard(card1);

        cardsList.removeCardByIndex(1);

        cardsList.editCard(0, Box.of("Zero noro"), null, null,
                null, null, null, null, null, null);

        cardsList.editCard(0, null, Box.of(5), null,
                null, null, null, null, null, null);

        cardsList.editCard(0, null, Box.of(4), null,
                null, null, null, null, null, null);

        cardsList.editCard(0, null, Box.of(4), null,
                null, null, null, null, null, null);

        cardsList.editCard(0, null, Box.of(3), Box.of(9.99f),
                null, null, null, null, null, null);

        CardsHistory history = cardsList.getHistory();
        ArrayList<CardHistoryEntry> historyList = history.getSortedHistoryList(false);

        assertEquals(CardHistoryType.ADDED, historyList.get(0).getCardHistoryType());
        assertEquals(card0.getUid(), historyList.get(0).getMostRecent().getUid());

        assertEquals(CardHistoryType.ADDED, historyList.get(1).getCardHistoryType());
        assertEquals(card1.getUid(), historyList.get(1).getMostRecent().getUid());

        assertEquals(CardHistoryType.REMOVED, historyList.get(2).getCardHistoryType());
        assertEquals(card1.getUid(), historyList.get(2).getMostRecent().getUid());

        assertEquals(CardHistoryType.MODIFIED, historyList.get(3).getCardHistoryType());
        assertEquals("Zero noro", historyList.get(3).getMostRecent().getName());

        assertEquals(CardHistoryType.ADDED, historyList.get(4).getCardHistoryType());
        assertEquals(5, historyList.get(4).getMostRecent().getQuantity());

        assertEquals(CardHistoryType.REMOVED, historyList.get(5).getCardHistoryType());
        assertEquals(4, historyList.get(5).getMostRecent().getQuantity());

        assertEquals(CardHistoryType.REMOVED, historyList.get(6).getCardHistoryType());
        assertEquals(3, historyList.get(6).getMostRecent().getQuantity());

        assertEquals(CardHistoryType.MODIFIED, historyList.get(7).getCardHistoryType());
        assertEquals(9.99f, historyList.get(7).getMostRecent().getPrice());
    }
    //@@author

    @Test
    public void findCards_byNote_noMatch() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card.Builder()
                .name("Pikachu")
                .price(5.0f)
                .quantity(1)
                .note("starter deck")
                .build());

        ArrayList<Card> results = cardsList.findCards(
                null, null, null, null, null, null, null, null, "legendary", null);

        assertTrue(results.isEmpty());
    }

    @Test
    public void findCards_byNoteAndTag_success() {
        CardsList cardsList = new CardsList();

        Card card1 = new Card.Builder()
                .name("Pikachu")
                .price(5.0f)
                .quantity(1)
                .note("trade binder")
                .build();
        card1.addTag("trade");

        Card card2 = new Card.Builder()
                .name("Charizard")
                .price(50.0f)
                .quantity(1)
                .note("trade binder")
                .build();
        card2.addTag("display");

        cardsList.addCard(card1);
        cardsList.addCard(card2);

        ArrayList<Card> results = cardsList.findCards(
                null, null, null, null, null, null, null, null, "trade", "trade");

        assertEquals(1, results.size());
        assertEquals("Pikachu", results.get(0).getName());
    }

    @Test
    public void editCard_noteFromNull_success() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card.Builder()
                .name("Bulbasaur")
                .price(3.0f)
                .quantity(1)
                .build());

        boolean changed = cardsList.editCard(0, null, null,
                null, null, null, null,
                null, null, Box.of("new note"));

        assertTrue(changed);
        assertEquals("new note", cardsList.getCard(0).getNote());
    }

    @Test
    public void editCard_noteSameValue_noChange() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card.Builder()
                .name("Bulbasaur")
                .price(3.0f)
                .quantity(1)
                .note("starter")
                .build());

        boolean changed = cardsList.editCard(0, null, null,
                null, null, null, null,
                null, null, Box.of("starter"));

        assertFalse(changed);
    }

    @Test
    public void editCard_clearNote_success() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card.Builder()
                .name("Squirtle")
                .price(4.0f)
                .quantity(1)
                .note("water type")
                .build());

        boolean changed = cardsList.editCard(0, null, null,
                null, null, null, null,
                null, null, Box.of(null));

        assertTrue(changed);
        assertEquals(null, cardsList.getCard(0).getNote());
    }

    @Test
    public void getDuplicateCards_emptyList_returnsEmptyList() {
        CardsList cardsList = new CardsList();

        ArrayList<Card> duplicates = cardsList.getDuplicateCards();

        assertTrue(duplicates.isEmpty());
    }

    @Test
    public void getDuplicateCards_noDuplicates_returnsEmptyList() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card.Builder().name("A").price(1.0f).quantity(1).build());
        cardsList.addCard(new Card.Builder().name("B").price(2.0f).quantity(1).build());

        ArrayList<Card> duplicates = cardsList.getDuplicateCards();

        assertTrue(duplicates.isEmpty());
    }

    @Test
    public void getDuplicateCards_singleDuplicate_success() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card.Builder().name("Eevee").price(4.0f).quantity(2).build());
        cardsList.addCard(new Card.Builder().name("Mew").price(30.0f).quantity(1).build());

        ArrayList<Card> duplicates = cardsList.getDuplicateCards();

        assertEquals(1, duplicates.size());
        assertEquals("Eevee", duplicates.get(0).getName());
    }

    @Test
    public void addCard_sameCardDifferentTagStillMerges_whenNoteSame() {
        CardsList cardsList = new CardsList();

        Card card1 = new Card.Builder()
                .name("Charmander")
                .price(6.0f)
                .quantity(1)
                .note("binder")
                .build();
        card1.addTag("trade");

        Card card2 = new Card.Builder()
                .name("Charmander")
                .price(6.0f)
                .quantity(2)
                .note("binder")
                .build();
        card2.addTag("display");

        cardsList.addCard(card1);
        cardsList.addCard(card2);

        assertEquals(1, cardsList.getSize());
        assertEquals(3, cardsList.getCard(0).getQuantity());
    }

    @Test
    public void findCards_byTagNoMatch_returnsEmptyList() {
        CardsList cardsList = new CardsList();

        Card card = new Card.Builder()
                .name("Pikachu")
                .price(5.0f)
                .quantity(1)
                .build();
        card.addTag("deck");
        cardsList.addCard(card);

        ArrayList<Card> results = cardsList.findCards(
                null, null, null, null, null, null, null, null, null, "rare");

        assertTrue(results.isEmpty());
    }

    @Test
    public void deepCopy_wishlistFlag_preserved() {
        CardsList wishlist = new CardsList();
        wishlist.setWishlist(true);

        CardsList copy = wishlist.deepCopy();

        assertTrue(copy.isWishlist());
    }

    @Test
    public void replaceWith_wishlistFlag_preserved() {
        CardsList source = new CardsList();
        source.setWishlist(true);

        CardsList target = new CardsList();
        target.setWishlist(false);

        target.replaceWith(source);

        assertTrue(target.isWishlist());
    }
}
