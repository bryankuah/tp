package seedu.cardcollector;

import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Scanner;

public class Ui {
    private static final String FORMAT_UNKNOWN_COMMAND = "Unknown command \"%1$s\"%n";
    private static final String FORMAT_INVALID_ARGUMENT = "%1$s%n%n";
    private static final String FORMAT_INVALID_ARGUMENT_SYNTAX_USAGE = "Usage: \"%1$s\"%n";
    private static final String FORMAT_INVALID_ARGUMENT_EXAMPLE_USAGE = "Example: \"%1$s\"%n";

    private static final String FORMAT_HISTORY_ADDED_NO_RECORD = "No history for cards added found!%n";
    private static final String FORMAT_HISTORY_MODIFIED_NO_RECORD = "No history for cards modified found!%n";
    private static final String FORMAT_HISTORY_REMOVED_NO_RECORD = "No history for cards removed found!%n";
    private static final String FORMAT_HISTORY_ADDED_HAS_RECORD = "Fetching history for cards last added.%n";
    private static final String FORMAT_HISTORY_MODIFIED_HAS_RECORD = "Fetching history for cards last modified.%n";
    private static final String FORMAT_HISTORY_REMOVED_HAS_RECORD = "Fetching history for cards last removed.%n";
    private static final String FORMAT_HISTORY_ADDED_RECORD = "%1$s added -> %2$s%n";
    private static final String FORMAT_HISTORY_MODIFIED_RECORD = "%1$s modified -> %2$s%n";
    private static final String FORMAT_HISTORY_REMOVED_RECORD = "%1$s removed -> %2$s%n";
    private static final String FORMAT_HISTORY_DISPLAY_ALL_RECORDS = "Displaying all %1$d records:%n";
    private static final String FORMAT_HISTORY_DISPLAY_N_RECORDS = "Displaying latest %1$d out of %2$d records:%n";
    private static final int HISTORY_DISPLAY_DEFAULT_LIMIT = 15;

    private final Scanner scanner;
    private final DateTimeFormatter dateTimeFormatter;

    public Ui() {
        this.scanner = new Scanner(System.in);
        this.dateTimeFormatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
    }

    public void printBorder() {
        System.out.println("_______________________________________________________");
    }

    public void echo(String input) {
        printBorder();
        System.out.println(input);
        printBorder();
    }

    public void printWelcome() {
        String logo =
                "  ____              _  ____      _ _           _             \n"
                        + " / ___|__ _ _ __ __| |/ ___|___ | | | ___  ___| |_ ___  _ __ \n"
                        + "| |   / _` | '__/ _` | |   / _ \\| | |/ _ \\/ __| __/ _ \\| '__|\n"
                        + "| |__| (_| | | | (_| | |__| (_) | | |  __/ (__| || (_) | |   \n"
                        + " \\____\\__,_|_|  \\__,_|\\____\\___/|_|_|\\___|\\___|\\__\\___/|_|   \n";
        System.out.println("Hello I'm\n" + logo);
        System.out.println("What can I do for you?");
        printBorder();
    }

    public void printInvalidArgumentWarning(String message, String[] usage) {
        printBorder();
        System.out.printf(FORMAT_INVALID_ARGUMENT, message);

        assert usage.length > 0;

        System.out.printf(FORMAT_INVALID_ARGUMENT_SYNTAX_USAGE, usage[0]);
        for (int i = 1; i < usage.length; i++) {
            System.out.printf(FORMAT_INVALID_ARGUMENT_EXAMPLE_USAGE, usage[i]);
        }
        printBorder();
    }

    public void printUnknownCommandWarning(String message) {
        printBorder();
        System.out.printf(FORMAT_UNKNOWN_COMMAND, message);
        printBorder();
    }

    public void printExit() {
        printBorder();
        System.out.println("Bye! See you again");
        printBorder();
    }

    public String readInput() {
        return scanner.nextLine().trim();
    }

    public void printAdded(CardsList inventory) {
        System.out.println("I have added a new card!");
        printList(inventory);
    }

    public void printRemoved(CardsList inventory, int index) {
        System.out.println("I have removed card " + (index + 1));
        System.out.println("You have " + inventory.getSize() + " card(s) left");
        printList(inventory);
    }

    public void printRemoveByNameSuccess(String targetName, CardsList inventory) {
        printBorder();
        System.out.println("Card \"" + targetName + "\" removed successfully");
        System.out.println("You have " + inventory.getSize() + " card(s) left");
        printBorder();
    }

    public void printCardNotFound(String targetName) {
        printBorder();
        System.out.println("No card named \"" + targetName + "\" was found.");
        printBorder();
    }

    public void printInvalidIndex() {
        printBorder();
        System.out.println("Invalid card index.");
        printBorder();
    }

    public void printList(CardsList inventory) {
        assert inventory != null : "Inventory should not be null when printing the list";
        printBorder();
        int inventorySize = inventory.getSize();
        assert inventorySize >= 0 : "Inventory size cannot be negative";

        if (inventorySize == 0) {
            System.out.println("Inventory is empty!");
        } else {
            System.out.println("Here is your inventory!");
            for (int i = 0; i < inventorySize; i++) {
                Card card = inventory.getCard(i);
                assert card != null : "Inventory should not contain null cards when printing the list";
                System.out.println((i + 1) + ". " + card);
            }
        }
        printBorder();
    }

    public void printFound(ArrayList<Card> results) {
        assert results != null : "Results list passed to Ui should not be null";

        printBorder();
        if (results.isEmpty()) {
            System.out.println("No cards found matching your criteria!");
        } else {
            System.out.println("Here are the matching cards!");
            for (int i = 0; i < results.size(); i++) {
                System.out.println((i + 1) + ". " + results.get(i));
            }
        }
        printBorder();
    }

    private void printHistoryRecordCount(int originalSize, int limitedSize) {
        if (originalSize > limitedSize) {
            System.out.printf(FORMAT_HISTORY_DISPLAY_N_RECORDS, limitedSize, originalSize);
        } else {
            System.out.printf(FORMAT_HISTORY_DISPLAY_ALL_RECORDS, originalSize);
        }
    }

    public void printAddedHistory(CardsList inventory, int maxDisplayCount) {
        printBorder();

        ArrayList<Card> sortedCards = inventory.getSortedAddedCards(
                CardSortCriteria.LAST_ADDED, false, maxDisplayCount, HISTORY_DISPLAY_DEFAULT_LIMIT);

        if (sortedCards.isEmpty()) {
            System.out.printf(FORMAT_HISTORY_ADDED_NO_RECORD);
        } else {
            System.out.printf(FORMAT_HISTORY_ADDED_HAS_RECORD);

            printHistoryRecordCount(inventory.getAddedSize(), sortedCards.size());

            for (Card card : sortedCards) {
                Instant lastAdded = card.getLastAdded();
                String dateString = dateTimeFormatter.format(lastAdded);
                System.out.printf(FORMAT_HISTORY_ADDED_RECORD, dateString, card);
            }
        }
        printBorder();
    }

    public void printModifiedHistory(CardsList inventory, int maxDisplayCount) {
        printBorder();

        ArrayList<Card> sortedCards = inventory.getSortedCards(
                CardSortCriteria.LAST_MODIFIED, false, maxDisplayCount, HISTORY_DISPLAY_DEFAULT_LIMIT);

        if (sortedCards.isEmpty()) {
            System.out.printf(FORMAT_HISTORY_MODIFIED_NO_RECORD);
        } else {
            System.out.printf(FORMAT_HISTORY_MODIFIED_HAS_RECORD);

            printHistoryRecordCount(inventory.getSize(), sortedCards.size());

            for (Card card : sortedCards) {
                Instant lastModified = card.getLastModified();
                String dateString = dateTimeFormatter.format(lastModified);
                System.out.printf(FORMAT_HISTORY_MODIFIED_RECORD, dateString, card);
            }
        }
        printBorder();
    }

    public void printRemovedHistory(CardsList inventory, int maxDisplayCount) {
        printBorder();

        ArrayList<Card> sortedCards = inventory.getSortedRemovedCards(
                CardSortCriteria.LAST_MODIFIED, false, maxDisplayCount, HISTORY_DISPLAY_DEFAULT_LIMIT);

        if (sortedCards.isEmpty()) {
            System.out.printf(FORMAT_HISTORY_REMOVED_NO_RECORD);
        } else {
            System.out.printf(FORMAT_HISTORY_REMOVED_HAS_RECORD);

            printHistoryRecordCount(inventory.getRemovedSize(), sortedCards.size());

            for (Card card : sortedCards) {
                Instant lastModified = card.getLastModified();
                String dateString = dateTimeFormatter.format(lastModified);
                System.out.printf(FORMAT_HISTORY_REMOVED_RECORD, dateString, card);
            }
        }
        printBorder();
    }
}
