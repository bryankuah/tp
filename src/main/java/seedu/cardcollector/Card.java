package seedu.cardcollector;

import java.time.Instant;
import java.util.UUID;

public class Card {
    private final UUID uid;
    private String name;
    private int quantity;
    private float price;
    private Instant lastAdded = null;
    private Instant lastModified = null;

    public Card (Builder builder) {
        assert builder != null : "builder cannot be null";
        assert builder.name != null : "Name cannot be null";
        assert builder.quantity >= 0 : "Quantity must be assigned";

        this.uid = builder.uid;
        this.name = builder.name;
        this.quantity = builder.quantity;
        this.price = builder.price;
    }

    public static class Builder {
        private UUID uid;
        private String name;
        private Integer quantity;
        private Float price;

        public Builder uid(UUID uid) {
            this.uid = uid;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder price(float price) {
            this.price = price;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }


        public Card build() {
            // When no uid is specified, a random one is generated.
            if (uid == null) {
                uid = UUID.randomUUID();
            }

            assert name != null;
            assert price != null;
            assert quantity != null;

            return new Card(this);
        }
    }

    public UUID getUid() {
        return uid;
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
