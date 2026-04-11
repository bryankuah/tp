package seedu.cardcollector.command;

import seedu.cardcollector.card.Card;
import seedu.cardcollector.util.Box;

public class EditCommand extends Command {

    private final int targetIndex;
    private final Box<String> newName;
    private final Box<Integer> newQuantity;
    private final Box<Float> newPrice;
    private final Box<String> newCardSet;
    private final Box<String> newRarity;
    private final Box<String> newCondition;
    private final Box<String> newLanguage;
    private final Box<String> newCardNumber;
    private final Box<String> newNote;

    private Card originalCard;

    public EditCommand(int targetIndex, Box<String> newName, Box<Integer> newQuantity, Box<Float> newPrice,
                       Box<String> newCardSet, Box<String> newRarity, Box<String> newCondition,
                       Box<String> newLanguage, Box<String> newCardNumber, Box<String> newNote) {
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
        context.getTargetList().editCard(targetIndex,
                Box.of(originalCard.getName()),
                Box.of(originalCard.getQuantity()),
                Box.of(originalCard.getPrice()),
                Box.of(originalCard.getCardSet()),
                Box.of(originalCard.getRarity()),
                Box.of(originalCard.getCondition()),
                Box.of(originalCard.getLanguage()),
                Box.of(originalCard.getCardNumber()),
                Box.of(originalCard.getNote()));
        context.getUi().printUndoSuccess(context.getTargetList());
        return new CommandResult(false);
    }
}
