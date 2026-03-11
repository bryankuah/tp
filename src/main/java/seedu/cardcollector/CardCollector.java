package seedu.cardcollector;

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

        Card newCard = new Card(name, quantity, price);
        inventory.addCard(newCard);
        ui.printAdded(inventory);
    }

    public static void main(String[] args) {
        new CardCollector().run();
    }
}
