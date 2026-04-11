package seedu.cardcollector.command;

import seedu.cardcollector.card.Card;
import seedu.cardcollector.card.CardsList;
import seedu.cardcollector.ui.Ui;
import seedu.cardcollector.util.Box;

import java.util.Locale;

public class AcquiredCommand extends Command {

    private final int targetIndex;
    private Card acquiredCard;
    private int originalWishlistIndex;

    public AcquiredCommand(int targetIndex) {
        this.targetIndex = targetIndex;
        this.isReversible = true;
    }

    @Override
    public CommandResult execute(CommandContext context) {
        Ui ui = context.getUi();
        CardsList wishlist = context.getWishlist();
        CardsList inventory = context.getInventory();

        if (targetIndex < 0 || targetIndex >= wishlist.getSize()) {
            ui.printInvalidIndex();
            this.isReversible = false;
            return new CommandResult(false, false);
        }

        Card card = wishlist.getCard(targetIndex);
        this.acquiredCard = card.copy();
        this.originalWishlistIndex = targetIndex;

        wishlist.removeCardByIndex(targetIndex);
        inventory.addCard(card);

        ui.printAcquired(inventory);
        return new CommandResult(false, true);
    }

    @Override
    public CommandResult undo(CommandContext context) {
        CardsList wishlist = context.getWishlist();
        CardsList inventory = context.getInventory();
        Ui ui = context.getUi();

        if (acquiredCard == null) {
            ui.printUndoSuccess(wishlist);
            return new CommandResult(false);
        }

        // Reverse the effect on inventory (variant is unique, so search is safe)
        boolean reversedInventory = false;
        for (int i = 0; i < inventory.getSize(); i++) {
            if (isSameCardVariant(inventory.getCard(i), acquiredCard)) {
                Card invCard = inventory.getCard(i);
                int newQty = invCard.getQuantity() - acquiredCard.getQuantity();
                if (newQty <= 0) {
                    inventory.removeCardByIndex(i);
                } else {
                    inventory.editCard(i, null, Box.of(newQty), null,
                            null, null, null, null, null, null);
                }
                reversedInventory = true;
                break;
            }
        }

        // Restore card to wishlist at the original index (same style as RemoveCardByIndexCommand)
        if (reversedInventory) {
            wishlist.addCardAtIndex(originalWishlistIndex, acquiredCard.copy());
        }

        ui.printUndoSuccess(wishlist);
        return new CommandResult(false);
    }

    // Duplicated from CardsList (private static) to avoid changing other files
    private static boolean isSameCardVariant(Card first, Card second) {
        return first.getName().equalsIgnoreCase(second.getName())
                && first.getPrice() == second.getPrice()
                && normalized(first.getCardSet()).equals(normalized(second.getCardSet()))
                && normalized(first.getRarity()).equals(normalized(second.getRarity()))
                && normalized(first.getCondition()).equals(normalized(second.getCondition()))
                && normalized(first.getLanguage()).equals(normalized(second.getLanguage()))
                && normalized(first.getCardNumber()).equals(normalized(second.getCardNumber()))
                && normalized(first.getNote()).equals(normalized(second.getNote()));
    }

    private static String normalized(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
