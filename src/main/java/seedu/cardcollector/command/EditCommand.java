package seedu.cardcollector.command;

import seedu.cardcollector.card.Card;

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

    private String oldName;
    private Integer oldQuantity;
    private Float oldPrice;
    private String oldCardSet;
    private String oldRarity;
    private String oldCondition;
    private String oldLanguage;
    private String oldCardNumber;

    public EditCommand(int targetIndex, String newName, Integer newQuantity, Float newPrice,
            String newCardSet, String newRarity, String newCondition, String newLanguage, String newCardNumber) {
        this.targetIndex = targetIndex;
        this.newName = newName;
        this.newQuantity = newQuantity;
        this.newPrice = newPrice;
        this.newCardSet = newCardSet;
        this.newRarity = newRarity;
        this.newCondition = newCondition;
        this.newLanguage = newLanguage;
        this.newCardNumber = newCardNumber;
        this.isReversible = true;
    }

    @Override
    public CommandResult execute(CommandContext context) {
        var ui = context.getUi();
        var inventory = context.getTargetList();
        if (targetIndex < 0 || targetIndex >= inventory.getSize()) {
            ui.printInvalidIndex();
            return new CommandResult(false, false);
        }

        Card card = inventory.getCard(targetIndex);
        this.oldName = card.getName();
        this.oldQuantity = card.getQuantity();
        this.oldPrice = card.getPrice();
        this.oldCardSet = card.getCardSet();
        this.oldRarity = card.getRarity();
        this.oldCondition = card.getCondition();
        this.oldLanguage = card.getLanguage();
        this.oldCardNumber = card.getCardNumber();

        boolean changed = inventory.editCard(targetIndex, newName, newQuantity, newPrice,
                newCardSet, newRarity, newCondition, newLanguage, newCardNumber);
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
        context.getTargetList().editCard(targetIndex, oldName, oldQuantity, oldPrice,
                oldCardSet, oldRarity, oldCondition, oldLanguage, oldCardNumber);
        context.getUi().printUndoSuccess(context.getTargetList());
        return new CommandResult(false);
    }
}
