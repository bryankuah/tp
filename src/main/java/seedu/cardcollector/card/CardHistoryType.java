package seedu.cardcollector.card;

public enum CardHistoryType {
    ADDED("added"),
    REMOVED("removed"),
    MODIFIED("modified"),
    ENTIRE("entire");

    private final String keyword;

    CardHistoryType(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }
}
