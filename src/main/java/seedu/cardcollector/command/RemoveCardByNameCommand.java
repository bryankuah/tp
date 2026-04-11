package seedu.cardcollector.command;

import seedu.cardcollector.card.Card;

import java.util.ArrayList;

public class RemoveCardByNameCommand extends Command {
    private final String targetName;
    private Card removedCard;
    private int removedIndex;

    public RemoveCardByNameCommand(String targetName) {
        this.targetName = targetName;
    }

    @Override
    public CommandResult execute(CommandContext context) {
        var ui = context.getUi();
        var inventory = context.getTargetList();
        ArrayList<Integer> matches = inventory.getIndicesByName(targetName);

        if (matches.isEmpty()) {
            ui.printCardNotFound(targetName);
            return new CommandResult(false, false);
        }

        int targetIndex;
        if (matches.size() == 1) {
            targetIndex = matches.get(0);
        } else {
            targetIndex = ui.promptCardSelection(matches, inventory);
            if (targetIndex == -1) {
                ui.printRemoveByNameCancelled();
                return new CommandResult(false, false);
            }
        }
        this.removedCard = inventory.getCard(targetIndex).copy();
        this.removedIndex = targetIndex;
        inventory.removeCardByIndex(targetIndex);
        this.isReversible = true;
        ui.printRemoveByNameSuccess(targetName, inventory);
        return new CommandResult(false);
    }

    @Override
    public CommandResult undo(CommandContext context) {
        if (removedCard != null) {
            context.getTargetList().addCardAtIndex(removedIndex, removedCard);
            context.getUi().printUndoSuccess(context.getTargetList());
        }
        return new CommandResult(false);
    }
}
