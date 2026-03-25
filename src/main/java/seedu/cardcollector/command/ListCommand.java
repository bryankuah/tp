package seedu.cardcollector.command;

import seedu.cardcollector.CardsList;
import seedu.cardcollector.Ui;

public class ListCommand extends Command {
    @Override
    public CommandResult execute(Ui ui, CardsList inventory) {
        ui.printList(inventory);
        return new CommandResult(false);
    }
}
