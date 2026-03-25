package seedu.cardcollector;

import seedu.cardcollector.command.Command;
import seedu.cardcollector.command.CommandResult;
import seedu.cardcollector.exception.ParseInvalidArgumentException;
import seedu.cardcollector.exception.ParseUnknownCommandException;
import seedu.cardcollector.parsing.Parser;

public class CardCollector {
    private final Ui ui;
    private final CardsList inventory;
    private final Parser parser;

    public CardCollector() {
        ui = new Ui();
        inventory = new CardsList();
        parser = new Parser();
    }

    public void run() {
        ui.printWelcome();
        boolean isRunning = true;

        while (isRunning) {
            String input = ui.readInput();

            try {
                Command command = parser.parse(input);
                CommandResult result = command.execute(ui, inventory);
                isRunning = !result.getIsExit();
            } catch (ParseInvalidArgumentException e) {
                ui.printInvalidArgumentWarning(e.getMessage(), e.getUsage());
            } catch (ParseUnknownCommandException e) {
                ui.printUnknownCommandWarning(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new CardCollector().run();
    }
}
