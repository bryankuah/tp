package seedu.cardcollector;

import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Ui {
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

    public void printAddedHistory(CardsList inventory) {
        printBorder();

        // Sorts the cards according to last added date
        ArrayList<Card> sortedCards = inventory.getCards().stream()
                .sorted(Comparator.comparing(Card::getLastAdded))
                .collect(Collectors.toCollection(ArrayList::new));


        if (sortedCards.isEmpty()) {
            System.out.println("No history for cards added found!");
        } else {
            System.out.println("Here is the history for cards last added!");

            for (Card card: sortedCards) {
                Instant lastAdded = card.getLastAdded();
                String dateString = dateTimeFormatter.format(lastAdded);

                System.out.println(dateString + " added -> " + card);
            }
        }
        printBorder();
    }

    public void printModifiedHistory(CardsList inventory) {
        printBorder();

        // Sorts the cards according to last modified date
        ArrayList<Card> sortedCards = inventory.getCards().stream()
                .sorted(Comparator.comparing(Card::getLastModified))
                .collect(Collectors.toCollection(ArrayList::new));


        if (sortedCards.isEmpty()) {
            System.out.println("No history for cards modified found!");
        } else {
            System.out.println("Here is the history for cards last modified!");

            for (Card card: sortedCards) {
                Instant lastModified = card.getLastModified();
                String dateString = dateTimeFormatter.format(lastModified);

                System.out.println(dateString + " modified -> " + card);
            }
        }
        printBorder();
    }

    public void printRemovedHistory(CardsList removedInventory) {
        printBorder();

        // Sorts the removed cards according to last added date
        ArrayList<Card> sortedCards = removedInventory.getCards().stream()
                .sorted(Comparator.comparing(Card::getLastModified))
                .collect(Collectors.toCollection(ArrayList::new));


        if (sortedCards.isEmpty()) {
            System.out.println("No history for cards removed found!");
        } else {
            System.out.println("Here is the history for cards last removed!");

            for (Card card: sortedCards) {
                Instant lastModified = card.getLastModified();
                String dateString = dateTimeFormatter.format(lastModified);

                System.out.println(dateString + " removed -> " + card);
            }
        }
        printBorder();
    }
}
