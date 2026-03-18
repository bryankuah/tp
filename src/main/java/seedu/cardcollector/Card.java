package seedu.cardcollector;

import java.time.Instant;

public class Card {
    private String name;
    private int quantity;
    private float price;
    private Instant lastAdded = null;
    private Instant lastModified = null;

    public Card (String name, int quantity, float price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Instant getLastAdded() {
        return lastAdded;
    }

    public void setLastAdded(Instant lastAdded) {
        this.lastAdded = lastAdded;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return name + " | Quantity: " + quantity + " | Price: " + price;
    }
}
