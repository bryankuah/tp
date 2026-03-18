package seedu.cardcollector;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardsListTest {

    @Test
    public void addCard_card_success() {
        CardsList cardsList = new CardsList();

        Card card = new Card("Pikachu", 1, 5.50f);
        cardsList.addCard(card);

        assertEquals(1, cardsList.getSize());
        assertEquals(card, cardsList.getCard(0));
    }

    @Test
    public void findCards_byName_success() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card("Pikachu", 1, 5.50f));
        cardsList.addCard(new Card("Charizard", 2, 15.00f));
        cardsList.addCard(new Card("Pikachu VMAX", 3, 20.00f));

        // Case-insensitive and partial match for "pika"
        ArrayList<Card> results = cardsList.findCards("pika", null, null);
        
        assertEquals(2, results.size());
        assertEquals("Pikachu", results.get(0).getName());
        assertEquals("Pikachu VMAX", results.get(1).getName());
    }

    @Test
    public void findCards_byPrice_success() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card("Pikachu", 1, 5.50f));
        cardsList.addCard(new Card("Charizard", 2, 10.00f));

        // Exact price match
        ArrayList<Card> results = cardsList.findCards(null, 10.00f, null);
        
        assertEquals(1, results.size());
        assertEquals("Charizard", results.get(0).getName());
    }

    @Test
    public void findCards_byQuantity_success() {
        CardsList cardsList = new CardsList();

        cardsList.addCard(new Card("Bulbasaur", 5, 2.00f));
        cardsList.addCard(new Card("Squirtle", 1, 3.00f));

        // Exact quantity match
        ArrayList<Card> results = cardsList.findCards(null, null, 5);
        
        assertEquals(1, results.size());
        assertEquals("Bulbasaur", results.get(0).getName());
    }

    @Test
    public void findCards_multipleAttributes_success() {
        CardsList cardsList = new CardsList();
        
        cardsList.addCard(new Card("Mewtwo", 3, 20.00f));
        cardsList.addCard(new Card("Mewtwo", 1, 5.00f));
        cardsList.addCard(new Card("Mew", 3, 15.00f));

        // Matches both Name (contains "Mew") AND Quantity (exactly 3)
        ArrayList<Card> results = cardsList.findCards("Mew", null, 3);
        
        assertEquals(2, results.size());
        assertEquals(20.00f, results.get(0).getPrice());
        assertEquals(15.00f, results.get(1).getPrice());
    }

    @Test
    public void findCards_noMatch_returnsEmptyList() {
        CardsList cardsList = new CardsList();
        
        cardsList.addCard(new Card("Eevee", 1, 4.00f));

        // Searching for attributes that don't exist
        ArrayList<Card> results = cardsList.findCards("Snorlax", 100.00f, null);
        
        assertEquals(0, results.size());
    }
}
