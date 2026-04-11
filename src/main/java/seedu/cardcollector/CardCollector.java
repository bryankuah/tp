package seedu.cardcollector;

import java.io.IOException;
import java.util.Stack;

import seedu.cardcollector.card.CardsList;
import seedu.cardcollector.command.Command;
import seedu.cardcollector.command.CommandContext;
import seedu.cardcollector.command.CommandResult;
import seedu.cardcollector.exception.ParseBlankCommandException;
import seedu.cardcollector.exception.ParseInvalidArgumentException;
import seedu.cardcollector.exception.ParseUnknownCommandException;
import seedu.cardcollector.parsing.Parser;
import seedu.cardcollector.ui.Ui;
import seedu.cardcollector.command.HelpCommand;

public class CardCollector {
    private final Ui ui;
    private final CardsList inventory;
    private final CardsList wishlist;
    private final Parser parser;
    private final Storage storage;
    private final UploadUndoState uploadUndoState;
    private final Stack<Command> commandHistory;

    public CardCollector() {
        ui = new Ui();
        parser = new Parser();
        storage = Storage.createDefault();
        uploadUndoState = new UploadUndoState();
        commandHistory = new Stack<>();

        AppState initialState = loadState();
        inventory = initialState.getInventory();
        wishlist = initialState.getWishlist();
    }

    public void run() {
        ui.printWelcome();
        boolean isRunning = true;

        while (isRunning) {
            String input = ui.readInput().trim();

            boolean isWishlistCommand = false;
            String parseInput = input;

            if (input.toLowerCase().startsWith("wishlist ")) {
                isWishlistCommand = true;
                parseInput = input.substring(9).trim();
            }

            // Prevent "wishlist" alone from crashing parser
            if (parseInput.isEmpty()) {
                if (isWishlistCommand) {
                    ui.printUnknownCommandWarning("wishlist");
                }
                continue;
            }

            // Handle: wishlist /h → show wishlist help
            if (isWishlistCommand && parseInput.equalsIgnoreCase("/h")) {
                Command command = HelpCommand.forKeyword("wishlist");
                CommandContext context = new CommandContext(
                        ui, wishlist, inventory, wishlist, storage, uploadUndoState, commandHistory);
                command.execute(context);
                continue;
            }

            try {
                Command command = parser.parse(parseInput);
                CardsList targetList = isWishlistCommand ? wishlist : inventory;
                CommandContext context = new CommandContext(
                        ui, targetList, inventory, wishlist, storage, uploadUndoState, commandHistory);

                CommandResult result = command.execute(context);

                if (command.isReversible()) {
                    context.getCommandHistory().push(command);
                }
                if (result.shouldSave()) {
                    saveState();
                }
                isRunning = !result.getIsExit();
            } catch (ParseBlankCommandException e) {
                ui.printBlankCommandWarning();
            } catch (ParseUnknownCommandException e) {
                ui.printUnknownCommandWarning(e.getMessage());
            } catch (ParseInvalidArgumentException e) {
                ui.printInvalidArgumentWarning(e.getMessage(), e.getUsages());
            }
        }
    }

    public static void main(String[] args) {
        new CardCollector().run();
    }

    private AppState loadState() {
        try {
            return storage.load();
        } catch (IOException e) {
            System.err.println("Failed to load saved data: " + e.getMessage());
            return new AppState(new CardsList(), new CardsList());
        }
    }

    private void saveState() {
        try {
            storage.save(new AppState(inventory, wishlist));
        } catch (IOException e) {
            System.err.println("Failed to save data: " + e.getMessage());
        }
    }
}
