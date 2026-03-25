package seedu.cardcollector.command;

import seedu.cardcollector.CardsList;
import seedu.cardcollector.Ui;

public class RemoveCardByNameCommand extends Command {
    private final String targetName;

    public RemoveCardByNameCommand(String targetName) {
        this.targetName = targetName;
    }

    @Override
    public CommandResult execute(Ui ui, CardsList inventory) {
        boolean removed = inventory.removeCardByName(targetName);

        if (removed) {
            ui.printRemoveByNameSuccess(targetName, inventory);
        } else {
            ui.printCardNotFound(targetName);
        }

        return new CommandResult(false);
    }
}
