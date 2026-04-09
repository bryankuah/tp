package seedu.cardcollector.command;

import seedu.cardcollector.card.Card;
import seedu.cardcollector.card.CardSortCriteria;

import java.util.ArrayList;

public class ReorderCommand extends Command {

    private final CardSortCriteria criteria;
    private final boolean isAscending;
    private ArrayList<Card> previousOrder;

    public ReorderCommand(CardSortCriteria criteria, boolean isAscending) {
        this.criteria = criteria;
        this.isAscending = isAscending;
        this.isReversible = true;
    }

    @Override
    public CommandResult execute(CommandContext context) {
        var ui = context.getUi();
        var targetList = context.getTargetList();

        // Save current order before reordering (for undo)
        this.previousOrder = new ArrayList<>(targetList.getCards());

        targetList.reorder(criteria, isAscending);
        ui.printReordered(targetList);

        return new CommandResult(false);
    }

    @Override
    public CommandResult undo(CommandContext context) {
        var targetList = context.getTargetList();
        var ui = context.getUi();

        if (previousOrder != null) {
            targetList.getCards().clear();
            targetList.getCards().addAll(previousOrder);
            ui.printUndoSuccess(targetList);
        }

        return new CommandResult(false);
    }
}
