package seedu.cardcollector.command;

import seedu.cardcollector.card.CardSortCriteria;


public class ListCommand extends Command {
    private final CardSortCriteria sortCriteria;
    private final int maxDisplayCount;
    private final boolean isDescending;

    public ListCommand(CardSortCriteria sortCriteria, int maxDisplayCount, boolean isDescending) {
        this.sortCriteria = sortCriteria;
        this.maxDisplayCount = maxDisplayCount;
        this.isDescending = isDescending;
    }

    @Override
    public CommandResult execute(CommandContext context) {
        var ui = context.getUi();
        var inventory  = context.getTargetList();
        ui.printList(inventory, sortCriteria, maxDisplayCount, isDescending);
        return new CommandResult(false);
    }
}
