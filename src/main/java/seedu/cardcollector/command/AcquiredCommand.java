package seedu.cardcollector.command;

import seedu.cardcollector.card.Card;
import seedu.cardcollector.card.CardsList;
import seedu.cardcollector.ui.Ui;

public class AcquiredCommand extends Command {

    private final int targetIndex;

    public AcquiredCommand(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(CommandContext context) {
        Ui ui = context.getUi();
        CardsList wishlist = context.getWishlist();
        CardsList inventory = context.getInventory();

        if (targetIndex < 0 || targetIndex >= wishlist.getSize()) {
            ui.printInvalidIndex();
            return new CommandResult(false, false);
        }

        Card card = wishlist.getCard(targetIndex);
        wishlist.removeCardByIndex(targetIndex);
        inventory.addCard(card);

        ui.printAcquired(inventory);
        return new CommandResult(false, true);
    }
}
