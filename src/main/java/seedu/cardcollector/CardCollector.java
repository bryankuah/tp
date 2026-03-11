package seedu.cardcollector;

import java.time.Instant;
import java.util.ArrayList;

public class CardCollector {
    private final Ui ui;
    private final CardsList inventory;
    private final CardsList removedInventory;

    public CardCollector() {
        ui = new Ui();
        inventory = new CardsList();
        removedInventory = new CardsList();
    }

    public void run() {
        ui.printWelcome();
        boolean isRunning = true;

        while (isRunning) {
            String input = ui.readInput();
            String[] parts = input.split(" ", 2);
            String command = parts[0].toLowerCase();

            switch (command) {
            case "add":
                if (parts.length < 2) {
                    System.out.println("Missing details for add.");
                    break;
                }
                handleParsing(parts[1]);
                break;
            case "find":
                if (parts.length < 2) {
                    System.out.println("Usage: find [/n NAME] [/p PRICE] [/q QUANTITY]");
                    System.out.println("At least one field must be provided.");
                    break;
                }
                handleFind(parts[1]);
                break;
            case "history":
                if (parts.length < 2) {
                    System.out.println("Usage: history [added|modified|removed]");
                    System.out.println("Example: history added");
                    System.out.println("The argument must be provided.");
                    break;
                }
                handleHistory(parts[1]);
                break;
            case "bye":
                ui.printExit(); //
                isRunning = false;
                break;
            default:
                System.out.println("Unknown command!");
            }
        }
    }

    private void handleParsing(String arguments) {
        String name = arguments.split("/n")[1].split("/q|/p")[0].trim();
        int quantity = Integer.parseInt(arguments.split("/q")[1].split("/n|/p")[0].trim());
        float price = Float.parseFloat(arguments.split("/p")[1].split("/n|/q")[0].trim());

        Instant currentInstant = Instant.now();

        Card newCard = new Card(name, quantity, price, currentInstant, currentInstant);
        inventory.addCard(newCard);
        ui.printAdded(inventory);
    }

    private void handleFind(String arguments) {
        String name = null;
        Float price = null;
        Integer quantity = null;

        try {
            if (arguments.contains("/n")) {
                name = arguments.split("/n")[1].split("/q|/p")[0].trim();
            }
            if (arguments.contains("/p")) {
                price = Float.parseFloat(arguments.split("/p")[1].split("/n|/q")[0].trim());
            }
            if (arguments.contains("/q")) {
                quantity = Integer.parseInt(arguments.split("/q")[1].split("/n|/p")[0].trim());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format for price or quantity.");
            return;
        }

        if (name == null && price == null && quantity == null) {
            System.out.println("At least one search field (/n, /p, /q) must be provided.");
            return;
        }

        ArrayList<Card> results = inventory.findCards(name, price, quantity);
        ui.printFound(results);
    }

    /**
     * Handles the "history" command by displaying different types of inventory change history.
     * The argument matching is intentionally fuzzy - for example, input starting with "a" will
     * match "added", "m" will match "modified", and "r" will match "removed". The method uses
     * string prefix matching against the full command words.
     *
     * @param arguments The command argument that determines which history type to display.
     *                  The argument is matched as a prefix against "added", "modified",
     *                  and "removed" to determine which history to show.
     */
    private void handleHistory(String arguments) {
        if ("added".startsWith(arguments)){
            ui.printAddedHistory(inventory);
        } else if ("modified".startsWith(arguments)) {
            ui.printModifiedHistory(inventory);
        } else if ("removed".startsWith(arguments)) {
            ui.printRemovedHistory(removedInventory);
        } else {
            System.out.println("Unknown argument!");
        }
    }

    public static void main(String[] args) {
        new CardCollector().run();
    }
}
