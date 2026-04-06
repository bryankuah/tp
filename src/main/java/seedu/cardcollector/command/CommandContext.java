package seedu.cardcollector.command;

import seedu.cardcollector.card.CardsList;
import seedu.cardcollector.Storage;
import seedu.cardcollector.ui.Ui;
import seedu.cardcollector.UploadUndoState;
import java.util.Stack;

public class CommandContext {
    private final Ui ui;
    private final CardsList targetList;
    private final CardsList inventory;
    private final CardsList wishlist;
    private final Storage storage;
    private final UploadUndoState uploadUndoState;
    private final Stack<Command> commandHistory;

    public CommandContext(Ui ui, CardsList targetList, CardsList inventory, CardsList wishlist,
                          Storage storage, UploadUndoState uploadUndoState, Stack<Command> commandHistory ) {
        this.ui = ui;
        this.targetList = targetList;
        this.inventory = inventory;
        this.wishlist = wishlist;
        this.storage = storage;
        this.uploadUndoState = uploadUndoState;
        this.commandHistory = commandHistory;
    }

    public Stack<Command> getCommandHistory() {
        return commandHistory;
    }

    public Ui getUi() {
        return ui;
    }

    public CardsList getTargetList() {
        return targetList;
    }

    public CardsList getInventory() {
        return inventory;
    }

    public CardsList getWishlist() {
        return wishlist;
    }

    public Storage getStorage() {
        return storage;
    }

    public UploadUndoState getUploadUndoState() {
        return uploadUndoState;
    }
}
