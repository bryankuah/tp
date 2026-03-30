package seedu.cardcollector.command;

import seedu.cardcollector.card.CardHistoryType;

public class HistoryCommand extends Command {
    private final CardHistoryType historyType;
    private final int maxDisplayCount;
    private final boolean isDescending;

    public HistoryCommand(CardHistoryType historyType, int maxDisplayCount, boolean isDescending) {
        this.historyType = historyType;
        this.maxDisplayCount = maxDisplayCount;
        this.isDescending = isDescending;
    }

    @Override
    public CommandResult execute(CommandContext context) {
        var ui = context.getUi();
        var inventory = context.getTargetList();
        var history = inventory.getHistory();
        ui.printHistory(history, historyType, maxDisplayCount, isDescending);
        return new CommandResult(false);
    }
}
