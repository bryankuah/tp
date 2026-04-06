package seedu.cardcollector.ui;

import seedu.cardcollector.card.Card;
import seedu.cardcollector.card.CardFieldChange;
import seedu.cardcollector.card.CardSortCriteria;
import seedu.cardcollector.card.CardSorter;
import seedu.cardcollector.card.CardsHistory;
import seedu.cardcollector.card.CardHistoryEntry;
import seedu.cardcollector.card.CardHistoryType;
import seedu.cardcollector.card.CardsAnalytics;
import seedu.cardcollector.card.CardsList;
import seedu.cardcollector.command.HelpTopic;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Ui {
    private static final String FORMAT_UNKNOWN_COMMAND =
            "Unknown command \"%1$s\" entered%n";
    private static final String FORMAT_BLANK_COMMAND =
            "Blank command entered, did you mean to type something?%n";
    private static final String FORMAT_INVALID_ARGUMENT =
            "%1$s%n%n";
    private static final String FORMAT_INVALID_ARGUMENT_SYNTAX_USAGE =
            "Usage: \"%1$s\"%n";
    private static final String FORMAT_INVALID_ARGUMENT_EXAMPLE_USAGE =
            "Example: \"%1$s\"%n";
    
    
    private static final String FORMAT_HISTORY_HINT_ENTIRE =
            "Fetching history for addition, modification and removal of cards.%n";
    private static final String FORMAT_HISTORY_HINT_ADDED =
            "Fetching history for addition of cards.%n";
    private static final String FORMAT_HISTORY_HINT_MODIFIED =
            "Fetching history for modification of cards.%n";
    private static final String FORMAT_HISTORY_HINT_REMOVED =
            "Fetching history for removal of cards.%n";
    private static final String FORMAT_HISTORY_ADDED_RECORD =
            "[%1$s] + ADDED %2$s UNITS OF %3$s%n";
    private static final String FORMAT_HISTORY_MODIFIED_RECORD =
            "[%1$s] # MODIFIED TO %2$s%n%3$s%n";
    private static final String FORMAT_HISTORY_REMOVED_RECORD =
            "[%1$s] - REMOVED %2$s UNITS OF %3$s%n";
    private static final String FORMAT_HISTORY_DISPLAY_NO_RECORD =
            "No relevant history found!%n";
    private static final String FORMAT_HISTORY_DISPLAY_ALL_RECORDS =
            "Displaying all %1$d records:%n";
    private static final String FORMAT_HISTORY_DISPLAY_LATEST_N_RECORDS =
            "Displaying latest %1$d out of %2$d records:%n";
    private static final String FORMAT_HISTORY_DISPLAY_OLDEST_N_RECORDS =
            "Displaying oldest %1$d out of %2$d records:%n";
    private static final String FORMAT_HISTORY_CHANGED_FIELD =
            "\"%1$s\": %2$s -> %3$s";
    

    private static final String FORMAT_LIST_RECORD =
            "[index = %1$s] %2$s%n";
    private static final String FORMAT_LIST_DISPLAY_NO_RECORD =
            "Your card list is empty!";
    private static final String FORMAT_LIST_DISPLAY_ALL_RECORDS_ASCENDING =
            "Displaying all %1$d cards sorted by %2$s in ascending order:%n";
    private static final String FORMAT_LIST_DISPLAY_ALL_RECORDS_DESCENDING =
            "Displaying all %1$d cards sorted by %2$s in descending order:%n";
    private static final String FORMAT_LIST_DISPLAY_N_RECORDS_ASCENDING =
            "Displaying %1$d out of %2$d cards sorted by %3$s in ascending order:%n";
    private static final String FORMAT_LIST_DISPLAY_N_RECORDS_DESCENDING =
            "Displaying %1$d out of %2$d cards sorted by %3$s in descending order:%n";

    private static final int DISPLAY_DEFAULT_LIMIT = 15;
    private static final String DISPLAY_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";


    private final PrintStream out;
    private final Scanner scanner;
    private final DateTimeFormatter dateTimeFormatter;

    public Ui(InputStream in, PrintStream out) {
        this.out = out;
        this.scanner = new Scanner(in);
        this.dateTimeFormatter = DateTimeFormatter
                .ofPattern(DISPLAY_DATE_TIME_FORMAT)
                .withZone(ZoneId.systemDefault());
    }

    public Ui() {
        this(System.in, System.out);
    }

    public void printBorder() {
        out.println("_______________________________________________________");
    }

    public void echo(String input) {
        printBorder();
        out.println(input);
        printBorder();
    }

    public void printWelcome() {
        String logo =
                "  ____              _  ____      _ _           _             \n"
                        + " / ___|__ _ _ __ __| |/ ___|___ | | | ___  ___| |_ ___  _ __ \n"
                        + "| |   / _` | '__/ _` | |   / _ \\| | |/ _ \\/ __| __/ _ \\| '__|\n"
                        + "| |__| (_| | | | (_| | |__| (_) | | |  __/ (__| || (_) | |   \n"
                        + " \\____\\__,_|_|  \\__,_|\\____\\___/|_|_|\\___|\\___|\\__\\___/|_|   \n";
        out.println("Hello I'm\n" + logo);
        out.println("What can I do for you?");
        printBorder();
    }

    //@@author HX2003
    public void printInvalidArgumentWarning(String message, String[] usage) {
        printBorder();
        out.printf(FORMAT_INVALID_ARGUMENT, message);

        assert usage.length > 0;

        out.printf(FORMAT_INVALID_ARGUMENT_SYNTAX_USAGE, usage[0]);
        for (int i = 1; i < usage.length; i++) {
            out.printf(FORMAT_INVALID_ARGUMENT_EXAMPLE_USAGE, usage[i]);
        }
        printBorder();
    }

    public void printUnknownCommandWarning(String message) {
        printBorder();
        out.printf(FORMAT_UNKNOWN_COMMAND, message);
        printBorder();
    }

    public void printBlankCommandWarning() {
        printBorder();
        out.printf(FORMAT_BLANK_COMMAND);
        printBorder();
    }

    //@@author Simplificatedd
    public void printHelpOverview(List<HelpTopic> topics, String query) {
        printBorder();
        if (query == null || query.isBlank()) {
            out.println("CardCollector commands:");
        } else {
            out.println("Help matches for \"" + query + "\":");
        }
        for (HelpTopic topic : topics) {
            out.println(topic.getDisplayName() + " - " + topic.summary());
        }
        out.println("Run \"help COMMAND\" or \"COMMAND /h\" for syntax details.");
        printBorder();
    }

    public void printHelpTopic(HelpTopic topic) {
        printBorder();
        out.println("Command: " + topic.name());
        if (!topic.aliases().isEmpty()) {
            out.println("Aliases: " + String.join(", ", topic.aliases()));
        }
        out.println("Summary: " + topic.summary());
        out.println("Syntax:");
        for (int i = 0; i < topic.usage().length; i++) {
            String label = i == 0 ? "Usage" : "Example";
            out.println(label + ": " + topic.usage()[i]);
        }
        out.println("Run \"" + topic.name() + " /h\" to show this again.");
        printBorder();
    }

    public void printHelpNotFound(String query) {
        printBorder();
        out.println("No help entries matched \"" + query + "\".");
        out.println("Run \"help\" to list all commands.");
        printBorder();
    }

    //@@author WeiHeng2003
    public void printExit() {
        printBorder();
        out.println("Bye! See you again");
        printBorder();
    }

    public String readInput() {
        return scanner.nextLine().trim();
    }

    public void printAdded(CardsList inventory) {
        out.println("I have added a new card!");
        printList(inventory);
    }

    public void printRemoved(CardsList inventory, int index) {
        out.println("I have removed card " + (index + 1));
        out.println("You have " + inventory.getSize() + " card(s) left");
        printList(inventory);
    }

    //@@author bryankuah
    public void printEdited(CardsList inventory, int index) {
        out.println("I have edited card " + (index + 1) + "!");
        printList(inventory);
    }

    public void printNotEdited() {
        out.println("No changes found!");
    }

    public void printCompared(CardsList list, int index1, int index2) {
        printBorder();
        out.println("Comparing card " + (index1 + 1) + " and card " + (index2 + 1) + ":");
        out.println("Card " + (index1 + 1) + ": " + list.getCard(index1));
        out.println("Card " + (index2 + 1) + ": " + list.getCard(index2));
        printBorder();
    }

    public void printAcquired(CardsList inventory) {
        out.println("I have acquired the card and added it to your inventory!");
        printList(inventory);
    }

    public void printReordered(CardsList list) {
        out.println("I have reordered the cards!");
        printList(list);
    }

    //@@author Calvin-GH
    public void printRemoveByNameSuccess(String targetName, CardsList inventory) {
        printBorder();
        out.println("Card \"" + targetName + "\" removed successfully");
        out.println("You have " + inventory.getSize() + " card(s) left");
        printBorder();
    }

    public void printCardNotFound(String targetName) {
        printBorder();
        out.println("No card named \"" + targetName + "\" was found.");
        printBorder();
    }

    public void printInvalidIndex() {
        printBorder();
        out.println("Invalid card index.");
        printBorder();
    }

    //@@author
    public void printList(CardsList list) {
        assert list != null : "List should not be null when printing";
        printBorder();
        int listSize = list.getSize();
        assert listSize >= 0 : "List size cannot be negative";

        if (listSize == 0) {
            if (list.isWishlist()) {
                out.println("Your wishlist is empty!");
            } else {
                out.println(FORMAT_LIST_DISPLAY_NO_RECORD);
            }
        } else {
            if (list.isWishlist()) {
                out.println("This is your wishlist!");
            } else {
                out.println("Here is your card list!");
            }
            for (int i = 0; i < listSize; i++) {
                Card card = list.getCard(i);
                assert card != null : "List should not contain null cards";
                out.println((i + 1) + ". " + card);
            }
        }
        printBorder();
    }

    //@@author HX2003
    public void printList(CardsList list, CardSortCriteria sortCriteria,
                          int maxDisplayCount, boolean isDescending) {
        printBorder();

        ArrayList<Card> cards = list.getCards();
        ArrayList<Card> sortedCards = CardSorter.sort(cards, sortCriteria, maxDisplayCount,
                DISPLAY_DEFAULT_LIMIT, isDescending);

        printListRecordCount(list, list.getSize(), sortedCards.size(), isDescending, sortCriteria.getKeyword());

        for (Card card : sortedCards) {
            out.printf(FORMAT_LIST_RECORD, list.getIndex(card) + 1, card);
        }

        printBorder();
    }

    /**
     * Prints a formatted message indicating the number of records being displayed.
     * The message varies based on whether all records are shown or only a limited
     * subset, and reflects the current sort order.
     *
     * @param originalSize The total number of records available before the limit
     *                     is applied.
     * @param limitedSize The total number of records available after the limit
     *                    is applied.
     * @param isDescending If records are sorted in descending order.
     * @param sortCriteriaString The criteria by which records are sorted.
     */
    private void printListRecordCount(CardsList list, int originalSize, int limitedSize,
                                      boolean isDescending, String sortCriteriaString) {
        if (originalSize == 0) {
            if (list.isWishlist()) {
                out.println("Your wishlist is empty!");
            } else {
                out.println(FORMAT_LIST_DISPLAY_NO_RECORD);
            }
            return;
        }

        if (originalSize > limitedSize) {
            if (isDescending) {
                out.printf(FORMAT_LIST_DISPLAY_N_RECORDS_DESCENDING,
                        limitedSize, originalSize, sortCriteriaString);
            } else {
                out.printf(FORMAT_LIST_DISPLAY_N_RECORDS_ASCENDING,
                        limitedSize, originalSize, sortCriteriaString);
            }
        } else {
            if (isDescending) {
                out.printf(FORMAT_LIST_DISPLAY_ALL_RECORDS_DESCENDING,
                        originalSize, sortCriteriaString);
            } else {
                out.printf(FORMAT_LIST_DISPLAY_ALL_RECORDS_ASCENDING,
                        originalSize, sortCriteriaString);
            }
        }
    }

    //@@author
    public void printAnalytics(String listName, CardsAnalytics analytics) {
        printBorder();
        out.println("Analytics for your " + listName + ":");
        out.println("Distinct cards: " + analytics.getDistinctCards());
        out.println("Total quantity: " + analytics.getTotalQuantity());
        out.println("Total value: $" + formatMoney(analytics.getTotalValue()));

        if (analytics.getDistinctCards() > 0) {
            double averageQuantityPerCard = (double) analytics.getTotalQuantity() / analytics.getDistinctCards();
            double averageValuePerCard = analytics.getTotalValue() / analytics.getDistinctCards();
            double averageValuePerUnit = analytics.getTotalQuantity() == 0
                    ? 0
                    : analytics.getTotalValue() / analytics.getTotalQuantity();

            out.println("Average quantity per distinct card: "
                    + String.format(Locale.ROOT, "%.2f", averageQuantityPerCard));
            out.println("Average value per distinct card: $" + formatMoney(averageValuePerCard));
            out.println("Average value per unit: $" + formatMoney(averageValuePerUnit));
        } else {
            out.println("Average quantity per distinct card: 0.00");
            out.println("Average value per distinct card: $0.00");
            out.println("Average value per unit: $0.00");
        }

        if (analytics.getTotalValue() >= 1000) {
            out.println("Collection tier: High value");
        } else if (analytics.getTotalValue() >= 500) {
            out.println("Collection tier: Mid value");
        } else if (analytics.getTotalValue() > 0) {
            out.println("Collection tier: Starter");
        } else {
            out.println("Collection tier: Empty");
        }

        if (analytics.getTotalQuantity() >= 100) {
            out.println("Collection size: Large");
        } else if (analytics.getTotalQuantity() >= 30) {
            out.println("Collection size: Medium");
        } else if (analytics.getTotalQuantity() > 0) {
            out.println("Collection size: Small");
        } else {
            out.println("Collection size: Empty");
        }

        out.println();
        printMostExpensiveCards(analytics.getMostExpensiveCards());

        out.println();
        printTopCardsByHoldingValue(analytics.getTopCardsByHoldingValue());

        out.println();
        printCheapestCards(analytics.getCheapestCards());

        out.println();
        printTopSets(analytics.getTopSetsByCount());

        out.println();
        printTopSetsByValue(analytics.getTopSetsByValue());

        out.println();
        printPriceDistribution(analytics);

        out.println();
        printMetadataCoverage(analytics);

        printBorder();
    }

    //@@author bryankuah
    public void printFound(ArrayList<Card> results) {
        assert results != null : "Results list passed to Ui should not be null";

        printBorder();
        if (results.isEmpty()) {
            out.println("No cards found matching your criteria!");
        } else {
            out.println("Here are the matching cards!");
            for (int i = 0; i < results.size(); i++) {
                out.println((i + 1) + ". " + results.get(i));
            }
        }
        printBorder();
    }

    //@@author Simplificatedd
    public void printTaggedList(ArrayList<Card> results, String tag) {
        assert results != null : "Results list passed to Ui should not be null";

        printBorder();
        if (results.isEmpty()) {
            out.println("No cards found with tag \"" + tag + "\".");
        } else {
            out.println("Here are the cards tagged \"" + tag + "\"!");
            for (int i = 0; i < results.size(); i++) {
                out.println((i + 1) + ". " + results.get(i));
            }
        }
        printBorder();
    }

    private void printMostExpensiveCards(List<CardsAnalytics.CardMetric> expensiveCards) {
        out.println("Top expensive cards:");
        if (expensiveCards.isEmpty()) {
            out.println("None");
            return;
        }

        for (int i = 0; i < expensiveCards.size(); i++) {
            CardsAnalytics.CardMetric metric = expensiveCards.get(i);
            Card card = metric.getCard();
            out.println((i + 1) + ". " + card.getName()
                    + " ($" + formatMoney(card.getPrice()) + " each, qty " + card.getQuantity()
                    + ", total $" + formatMoney(metric.getLineValue()) + ")");
        }
    }

    //@@author Calvin-GH
    private void printTopCardsByHoldingValue(List<CardsAnalytics.CardMetric> cardsByHoldingValue) {
        out.println("Top cards by total holding value:");
        if (cardsByHoldingValue.isEmpty()) {
            out.println("None");
            return;
        }

        for (int i = 0; i < cardsByHoldingValue.size(); i++) {
            CardsAnalytics.CardMetric metric = cardsByHoldingValue.get(i);
            Card card = metric.getCard();
            out.println((i + 1) + ". " + card.getName()
                    + " ($" + formatMoney(metric.getLineValue()) + ")");
        }
    }

    private void printCheapestCards(List<CardsAnalytics.CardMetric> cheapestCards) {
        out.println("Cheapest cards:");
        if (cheapestCards.isEmpty()) {
            out.println("None");
            return;
        }

        for (int i = 0; i < cheapestCards.size(); i++) {
            CardsAnalytics.CardMetric metric = cheapestCards.get(i);
            Card card = metric.getCard();
            out.println((i + 1) + ". " + card.getName()
                    + " ($" + formatMoney(card.getPrice()) + " each, qty " + card.getQuantity()
                    + ", total $" + formatMoney(metric.getLineValue()) + ")");
        }
    }

    private void printTopSetsByValue(List<CardsAnalytics.SetValueMetric> topSetsByValue) {
        out.println("Top sets by value:");
        if (topSetsByValue.isEmpty()) {
            out.println("None");
            return;
        }

        for (int i = 0; i < topSetsByValue.size(); i++) {
            CardsAnalytics.SetValueMetric metric = topSetsByValue.get(i);
            out.println((i + 1) + ". " + metric.getSetName()
                    + " ($" + formatMoney(metric.getTotalValue()) + ")");
        }
    }

    private void printPriceDistribution(CardsAnalytics analytics) {
        out.println("Price distribution:");
        out.println("$0 cards: " + analytics.getZeroPriceCards());
        out.println("$0.01-$9.99 cards: " + analytics.getLowPriceCards());
        out.println("$10-$49.99 cards: " + analytics.getMediumPriceCards());
        out.println("$50-$99.99 cards: " + analytics.getUpperMidPriceCards());
        out.println("$100+ cards: " + analytics.getHighPriceCards());
    }

    private void printMetadataCoverage(CardsAnalytics analytics) {
        out.println("Metadata coverage:");
        out.println("Cards with notes: " + analytics.getCardsWithNotes()
                + "/" + analytics.getDistinctCards());
        out.println("Cards with set information: " + analytics.getCardsWithSetInformation()
                + "/" + analytics.getDistinctCards());
    }

    //@@author Simplificatedd
    private void printTopSets(List<CardsAnalytics.SetMetric> topSets) {
        out.println("Top sets by count:");
        if (topSets.isEmpty()) {
            out.println("None");
            return;
        }

        for (int i = 0; i < topSets.size(); i++) {
            CardsAnalytics.SetMetric metric = topSets.get(i);
            out.println((i + 1) + ". " + metric.getSetName() + " (" + metric.getTotalCount() + ")");
        }
    }

    private static String formatMoney(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    public void printTagUpdated(CardsList list, int index, String tag, String action) {
        printBorder();
        out.println("I have " + toPastTense(action) + " tag \"" + tag + "\" for card " + (index + 1) + "!");
        out.println(list.getCard(index));
        printBorder();
    }

    public void printTagNoChange(Card card, String tag, String action) {
        printBorder();
        out.println("No tag change: \"" + tag + "\" was not " + toPastTense(action) + ".");
        out.println(card);
        printBorder();
    }

    private static String toPastTense(String action) {
        if ("remove".equals(action)) {
            return "removed";
        }
        if ("add".equals(action)) {
            return "added";
        }
        return action;
    }

    public void printUndoSuccess(CardsList list) {
        printBorder();
        out.println("Undo Successful!");
        printList(list);
    }

    public void printDownloadSuccess(Path path) {
        printBorder();
        out.println("Saved current CardCollector data to: " + path);
        printBorder();
    }

    public void printUploadSuccess(Path sourcePath, Path activePath) {
        printBorder();
        out.println("Loaded CardCollector data from: " + sourcePath);
        out.println("Active storage file remains: " + activePath);
        out.println("Run \"undoupload\" to restore the previous session data.");
        printBorder();
    }

    public boolean confirmUpload(Path sourcePath, Path activePath) {
        printBorder();
        out.println("Warning: upload will replace your current inventory and wishlist.");
        out.println("Imported file: " + sourcePath);
        out.println("Your active save file will remain: " + activePath);
        out.println("Type YES to continue or anything else to cancel.");
        printBorder();
        return "YES".equals(readInput());
    }

    public void printUploadCancelled() {
        printBorder();
        out.println("Upload cancelled. Current data was not changed.");
        printBorder();
    }

    public void printUndoUploadSuccess(Path activePath) {
        printBorder();
        out.println("Restored the data from before the last upload.");
        out.println("Active storage file: " + activePath);
        printBorder();
    }

    public void printNoUploadUndoAvailable() {
        printBorder();
        out.println("No upload action is available to undo.");
        printBorder();
    }

    public void printStorageTransferError(String action, Path path, String errorMessage) {
        printBorder();
        out.println("Failed to " + action + " storage file: " + path);
        out.println(errorMessage);
        printBorder();
    }

    //@@author HX2003
    /**
     * Prints a formatted message indicating the number of historical records being displayed.
     * The message varies based on whether all records are shown or only a limited
     * subset, and reflects the current sort order.
     *
     * @param originalSize The total number of records available before the limit
     *                     is applied.
     * @param limitedSize The total number of records available after the limit
     *                    is applied.
     * @param latest If records are sorted by latest first.
     */
    private void printHistoryRecordCount(int originalSize, int limitedSize, boolean latest) {
        if (originalSize == 0) {
            out.printf(FORMAT_HISTORY_DISPLAY_NO_RECORD);
        } else if (originalSize > limitedSize) {
            if (latest) {
                out.printf(FORMAT_HISTORY_DISPLAY_LATEST_N_RECORDS, limitedSize, originalSize);
            } else {
                out.printf(FORMAT_HISTORY_DISPLAY_OLDEST_N_RECORDS, limitedSize, originalSize);
            }
        } else {
            out.printf(FORMAT_HISTORY_DISPLAY_ALL_RECORDS, originalSize);
        }
    }

    public void printHistory(CardsHistory history, CardHistoryType displayHistoryType,
            int maxDisplayCount, boolean isDescending) {
        printBorder();

        ArrayList<CardHistoryEntry> historyList = history.getSortedHistoryList(isDescending);

        ArrayList<CardHistoryEntry> filteredHistoryList;

        switch (displayHistoryType) {
        case ENTIRE -> {
            out.printf(FORMAT_HISTORY_HINT_ENTIRE);
        }
        case ADDED -> {
            out.printf(FORMAT_HISTORY_HINT_ADDED);
        }
        case MODIFIED -> {
            out.printf(FORMAT_HISTORY_HINT_MODIFIED);
        }
        case REMOVED -> {
            out.printf(FORMAT_HISTORY_HINT_REMOVED);
        }
        default -> {
            assert false : "Unhandled CardHistoryType";
        }
        }

        if (displayHistoryType == CardHistoryType.ENTIRE) {
            filteredHistoryList = historyList;
        } else {
            filteredHistoryList = historyList.stream()
                    .filter(entry -> entry.getCardHistoryType() == displayHistoryType)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        int recordsLimit = (maxDisplayCount == -1) ? DISPLAY_DEFAULT_LIMIT :
                Math.min(filteredHistoryList.size(), maxDisplayCount);

        printHistoryRecordCount(filteredHistoryList.size(), recordsLimit, isDescending);

        for (int i = 0; i < recordsLimit && i < filteredHistoryList.size(); i++) {
            CardHistoryEntry entry = filteredHistoryList.get(i);
            CardHistoryType historyType = entry.getCardHistoryType();

            printHistoryAddedEntry(entry, historyType);
            printHistoryModifiedEntry(entry, historyType);
            printHistoryRemovedEntry(entry, historyType);
        }

        printBorder();
    }

    private void printHistoryAddedEntry(CardHistoryEntry entry,
                                        CardHistoryType historyType) {
        if (historyType != CardHistoryType.ADDED) {
            return;
        }

        Card current = entry.getCurrent();
        Instant lastAdded = current.getLastAdded();
        assert lastAdded != null;
        int addedQuantity = entry.getChangedQuantity();
        String date = dateTimeFormatter.format(lastAdded);

        out.printf(FORMAT_HISTORY_ADDED_RECORD, date, addedQuantity, current);
    }

    private void printHistoryModifiedEntry(CardHistoryEntry entry,
                                           CardHistoryType historyType) {
        if (historyType != CardHistoryType.MODIFIED) {
            return;
        }

        Card current = entry.getCurrent();
        LinkedHashMap<String, CardFieldChange> changedFields = entry.getChangedFields();
        Instant lastModified = current.getLastModified();
        assert lastModified != null;
        String date = dateTimeFormatter.format(lastModified);

        String fieldsString = changedFields.entrySet().stream()
                .map(field -> String.format(FORMAT_HISTORY_CHANGED_FIELD,
                        field.getKey(), field.getValue().previous(), field.getValue().current()))
                .collect(Collectors.joining(", "));

        out.printf(FORMAT_HISTORY_MODIFIED_RECORD, date, current, fieldsString);
    }

    private void printHistoryRemovedEntry(CardHistoryEntry entry,
                                          CardHistoryType historyType) {
        if (historyType != CardHistoryType.REMOVED) {
            return;
        }

        Card mostRecent = entry.getMostRecent();

        Instant lastRemoved = mostRecent.getLastRemoved();
        assert lastRemoved != null;
        int removedQuantity = -entry.getChangedQuantity();
        String date = dateTimeFormatter.format(lastRemoved);

        out.printf(FORMAT_HISTORY_REMOVED_RECORD, date, removedQuantity, mostRecent);
    }

    //@@author bryankuah
    public void printCleared(CardsList list) {
        String listType = list.isWishlist() ? "wishlist" : "inventory";
        out.println("Your " + listType + " has been cleared!");
        printList(list);  // shows empty list message
    }
}
