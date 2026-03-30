package seedu.cardcollector.card;

public enum CardSortCriteria {
    NAME("name"),
    QUANTITY("quantity"),
    PRICE("price"),
    LAST_ADDED("added"),
    LAST_MODIFIED("modified"),
    LAST_REMOVED("removed");

    private final String keyword;

    CardSortCriteria(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }
}
