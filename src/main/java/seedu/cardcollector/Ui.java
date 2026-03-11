package seedu.cardcollector;

import java.util.ArrayList;
import java.util.Scanner;

public class Ui {
    private Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
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
        printBorder();
        if (inventory.getSize() == 0) {
            System.out.println("Inventory is empty!");
        } else {
            System.out.println("Here is your inventory!");
            for (int i = 0; i < inventory.getSize(); i++) {
                System.out.println((i + 1) + ". " + inventory.getCard(i));
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
}
