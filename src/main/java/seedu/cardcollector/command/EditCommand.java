package seedu.cardcollector.command;

import seedu.cardcollector.card.Card;

import java.time.Instant;

public class EditCommand extends Command {

    private final int targetIndex;
    private final String newName;
    private final Integer newQuantity;
    private final Float newPrice;
    private final String newCardSet;
    private final String newRarity;
    private final String newCondition;
    private final String newLanguage;
    private final String newCardNumber;
    private final String newNote;

    private Card originalCard;

    public EditCommand(int targetIndex, String newName, Integer newQuantity, Float newPrice,
                       String newCardSet, String newRarity, String newCondition,
                       String newLanguage, String newCardNumber, String newNote) {
        this.targetIndex = targetIndex;
        this.newName = newName;
        this.newQuantity = newQuantity;
        this.newPrice = newPrice;
        this.newCardSet = newCardSet;
        this.newRarity = newRarity;
        this.newCondition = newCondition;
        this.newLanguage = newLanguage;
        this.newCardNumber = newCardNumber;
        this.newNote = newNote;
        this.isReversible = true;
    }

    @Override
    public CommandResult execute(CommandContext context) {
        var ui = context.getUi();
        var inventory = context.getTargetList();
        if (targetIndex < 0 || targetIndex >= inventory.getSize()) {
            ui.printInvalidIndex();
            this.isReversible = false;
            return new CommandResult(false, false);
        }

        this.originalCard = inventory.getCard(targetIndex).copy();

        boolean changed = inventory.editCard(targetIndex, newName, newQuantity, newPrice,
                newCardSet, newRarity, newCondition, newLanguage, newCardNumber, newNote);
        this.isReversible = changed;

        if (changed) {
            ui.printEdited(inventory, targetIndex);
        } else {
            ui.printNotEdited();
        }
        return new CommandResult(false, changed);
    }

    @Override
    public CommandResult undo(CommandContext context) {
        originalCard.setLastModified(Instant.now());
        context.getTargetList().restoreCard(targetIndex, originalCard);
        context.getUi().printUndoSuccess(context.getTargetList());
        return new CommandResult(false);
    }
}
