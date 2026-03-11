package seedu.cardcollector;

import java.time.Instant;
import java.util.ArrayList;

public class CardCollector {
    private final Ui ui;
    private final CardsList inventory;

    public CardCollector() {
        ui = new Ui();
        inventory = new CardsList();
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
            case "remove":
                if (parts.length < 2) {
                    System.out.println("Missing index for remove.");
                    break;
                }
                handleRemove(parts[1]);
                break;
            case "history":
                handleHistory();
                break;
            case "bye":
                ui.printExit();
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

    private void handleRemove(String argument) {
        argument = argument.trim();

        try {
            int index = Integer.parseInt(argument) - 1;

            if (index < 0 || index >= inventory.getSize()) {
                System.out.println("Invalid card index.");
                return;
            }

            inventory.removeCard(index);
            ui.printRemoved(inventory, index);

        } catch (NumberFormatException e) {

            boolean removed = inventory.removeCardByName(argument);

            if (removed) {
                System.out.println("Card \"" + argument + "\" removed successfully.");
                ui.printList(inventory);
            } else {
                System.out.println("Card with name \"" + argument + "\" not found.");
            }
        }
    }

    private void handleHistory() {
        ArrayList<Card> sortedCards = inventory.getCardsSortedByLastAdded();
        ui.printAddedHistory(sortedCards);
    }

    public static void main(String[] args) {
        new CardCollector().run();
    }
}