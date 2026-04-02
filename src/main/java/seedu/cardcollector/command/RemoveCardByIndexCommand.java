package seedu.cardcollector.command;
import seedu.cardcollector.card.Card;

public class RemoveCardByIndexCommand extends Command {
    private final int targetIndex;
    private Card removedCard;

    public RemoveCardByIndexCommand(int targetIndex) {
        this.targetIndex = targetIndex;
        this.isReversible = true;
    }

    @Override
    public CommandResult execute(CommandContext context) {
        var ui = context.getUi();
        var inventory = context.getTargetList();
        if (targetIndex < 0 || targetIndex >= inventory.getSize()) {
            ui.printInvalidIndex();
            this.isReversible = false;
            return new CommandResult(false);
        }
        this.removedCard = inventory.getCard(targetIndex);
        inventory.removeCardByIndex(targetIndex);
        this.isReversible = true;
        ui.printRemoved(inventory,targetIndex);
        return new CommandResult(false);
    }

    @Override
    public CommandResult undo(CommandContext context) {
        if (removedCard != null) {
            context.getTargetList().addCardAtIndex(targetIndex, removedCard);
            context.getUi().printUndoSuccess(context.getTargetList());
        }
        return new CommandResult(false);
    }
}
