package seedu.cardcollector.command;

import seedu.cardcollector.CardsList;
import seedu.cardcollector.Ui;

public class RemoveCardByIndexCommand extends Command {
    private final int targetIndex;

    public RemoveCardByIndexCommand(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Ui ui, CardsList inventory) {
        if (targetIndex < 0 || targetIndex >= inventory.getSize()) {
            ui.printInvalidIndex();
            return new CommandResult(false);
        }
        inventory.removeCardByIndex(targetIndex);
        ui.printRemoved(inventory,targetIndex);
        return new CommandResult(false);
    }
}
