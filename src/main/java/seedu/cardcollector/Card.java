package seedu.cardcollector;

public class Card {
    protected String name;
    protected int quantity;
    protected float price;

    public Card (String name, int quantity, float price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name + " | Quantity: " + quantity + " | Price: " + price;
    }
}
