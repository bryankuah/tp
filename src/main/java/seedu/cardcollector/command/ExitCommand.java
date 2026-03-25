package seedu.cardcollector.command;

import seedu.cardcollector.CardsList;
import seedu.cardcollector.Ui;

public class ExitCommand extends Command {
    @Override
    public CommandResult execute(Ui ui, CardsList inventory) {
        ui.printExit();
        return new CommandResult(true);
    }
}
