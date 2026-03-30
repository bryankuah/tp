package seedu.cardcollector.card;

import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.StringJoiner;
import java.util.UUID;

public class Card {
    private final UUID uid;
    private String name;
    private int quantity;
    private float price;
    private String cardSet;
    private String rarity;
    private String condition;
    private String language;
    private String cardNumber;
    private LinkedHashSet<String> tags;
    private Instant lastAdded = null;    // lastAdded is a nullable type
    private Instant lastModified = null; // lastModified is a nullable type
    private Instant lastRemoved = null;  // lastRemoved is a nullable type

    private Card (Builder builder) {
        this.uid = builder.uid;
        this.name = builder.name;
        this.quantity = builder.quantity;
        this.price = builder.price;
        this.cardSet = builder.cardSet;
        this.rarity = builder.rarity;
        this.condition = builder.condition;
        this.language = builder.language;
        this.cardNumber = builder.cardNumber;
        this.tags = new LinkedHashSet<>(builder.tags);
        this.lastAdded = builder.lastAdded;
        this.lastModified = builder.lastModified;
        this.lastRemoved = builder.lastRemoved;
    }

    public static class Builder {
        private UUID uid;
        private String name;
        private Integer quantity;
        private Float price;
        private String cardSet;
        private String rarity;
        private String condition;
        private String language;
        private String cardNumber;
        private LinkedHashSet<String> tags = new LinkedHashSet<>();
        private Instant lastAdded;
        private Instant lastModified;
        private Instant lastRemoved;

        public Builder uid(UUID uid) {
            this.uid = uid;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder price(float price) {
            this.price = price;
            return this;
        }

        public Builder cardSet(String cardSet) {
            this.cardSet = cardSet;
            return this;
        }

        public Builder rarity(String rarity) {
            this.rarity = rarity;
            return this;
        }

        public Builder condition(String condition) {
            this.condition = condition;
            return this;
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public Builder cardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public Builder tags(Collection<String> tags) {
            this.tags = new LinkedHashSet<>();
            if (tags == null) {
                return this;
            }

            for (String tag : tags) {
                addTag(tag);
            }
            return this;
        }

        public Builder addTag(String tag) {
            String normalizedTag = normalizeTag(tag);
            if (normalizedTag != null && !containsTagIgnoreCase(tags, normalizedTag)) {
                this.tags.add(normalizedTag);
            }
            return this;
        }

        public Builder lastAdded(Instant lastAdded) {
            this.lastAdded = lastAdded;
            return this;
        }

        public Builder lastModified(Instant lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public Builder lastRemoved(Instant lastRemoved) {
            this.lastRemoved = lastRemoved;
            return this;
        }

        public Card build() {
            // When no uid is specified, a random one is generated.
            if (uid == null) {
                uid = UUID.randomUUID();
            }

            assert name != null;
            assert quantity != null;
            assert price != null;
            assert quantity >= 0 : "Quantity cannot be negative";

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

    public String getCardSet() {
        return cardSet;
    }

    public void setCardSet(String cardSet) {
        this.cardSet = cardSet;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public LinkedHashSet<String> getTags() {
        return new LinkedHashSet<>(tags);
    }

    public void setTags(Collection<String> tags) {
        this.tags = new LinkedHashSet<>();
        if (tags == null) {
            return;
        }

        for (String tag : tags) {
            addTag(tag);
        }
    }

    public boolean addTag(String tag) {
        String normalizedTag = normalizeTag(tag);
        if (normalizedTag == null || containsTagIgnoreCase(tags, normalizedTag)) {
            return false;
        }
        tags.add(normalizedTag);
        return true;
    }

    public boolean removeTag(String tag) {
        String normalizedTag = normalizeTag(tag);
        if (normalizedTag == null) {
            return false;
        }

        Iterator<String> iterator = tags.iterator();
        while (iterator.hasNext()) {
            String existingTag = iterator.next();
            if (existingTag.equalsIgnoreCase(normalizedTag)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public boolean hasTag(String tag) {
        String normalizedTag = normalizeTag(tag);
        return normalizedTag != null && containsTagIgnoreCase(tags, normalizedTag);
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

    public Instant getLastRemoved() {
        return lastRemoved;
    }

    public void setLastRemoved(Instant lastRemoved) {
        this.lastRemoved = lastRemoved;
    }


    public Card copy() {
        return new Card.Builder()
                .uid(uid)
                .name(name)
                .quantity(quantity)
                .price(price)
                .cardSet(cardSet)
                .rarity(rarity)
                .condition(condition)
                .language(language)
                .cardNumber(cardNumber)
                .tags(tags)
                .lastAdded(lastAdded)
                .lastModified(lastModified)
                .lastRemoved(lastRemoved)
                .build();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder()
                .append(name)
                .append(" | Quantity: ")
                .append(quantity)
                .append(" | Price: ")
                .append(price);

        appendMetadata(builder, "Set", cardSet);
        appendMetadata(builder, "Rarity", rarity);
        appendMetadata(builder, "Condition", condition);
        appendMetadata(builder, "Language", language);
        appendMetadata(builder, "Card No.", cardNumber);
        appendMetadata(builder, "Tags", formatTags(tags));

        return builder.toString();
    }

    private static void appendMetadata(StringBuilder builder, String label, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        builder.append(" | ").append(label).append(": ").append(value);
    }

    private static String normalizeTag(String tag) {
        if (tag == null) {
            return null;
        }

        String trimmed = tag.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static boolean containsTagIgnoreCase(Collection<String> tags, String tag) {
        for (String existingTag : tags) {
            if (existingTag.equalsIgnoreCase(tag)) {
                return true;
            }
        }
        return false;
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
