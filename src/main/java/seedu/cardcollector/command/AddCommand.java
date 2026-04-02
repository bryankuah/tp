package seedu.cardcollector.command;

import seedu.cardcollector.card.Card;
import java.util.UUID;

public class AddCommand extends Command {
    private final UUID uid;
    private final String name;
    private final int quantity;
    private final float price;
    private final String cardSet;
    private final String rarity;
    private final String condition;
    private final String language;
    private final String cardNumber;
    private final String note;

    private Card addedCard;
    private int addedIndex;
    private boolean wasMerged = false;

    public AddCommand(UUID uid, String name, int quantity, float price,
                      String cardSet, String rarity, String condition,
                      String language, String cardNumber, String note) {
        this.uid = uid;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.cardSet = cardSet;
        this.rarity = rarity;
        this.condition = condition;
        this.language = language;
        this.cardNumber = cardNumber;
        this.note = note;
        this.isReversible = true;
    }

    @Override
    public CommandResult execute(CommandContext context) {
        var inventory = context.getTargetList();
        this.addedCard = new Card.Builder()
                .uid(uid)
                .name(name)
                .price(price)
                .quantity(quantity)
                .cardSet(cardSet)
                .rarity(rarity)
                .condition(condition)
                .language(language)
                .cardNumber(cardNumber)
                .note(note)
                .build();

        for (int i = 0; i < inventory.getSize(); i++) {
            Card existing = inventory.getCard(i);
            if (existing.getName().equalsIgnoreCase(name)
                    && existing.getPrice() == price
                    && sameText(existing.getCardSet(), cardSet)
                    && sameText(existing.getRarity(), rarity)
                    && sameText(existing.getCondition(), condition)
                    && sameText(existing.getLanguage(), language)
                    && sameText(existing.getCardNumber(), cardNumber)
                    && sameText(existing.getNote(), note)) {
                this.wasMerged = true;
                this.addedIndex = i;

                int newQuantity = existing.getQuantity() + quantity;
                inventory.editCard(i, null, newQuantity, null,
                        null, null, null, null, null, null);
                break;
            }
        }

        if (!wasMerged) {
            inventory.addCard(addedCard);
            this.addedIndex = inventory.getCards().size()-1;
        }

        context.getUi().printAdded(inventory);
        return new CommandResult(false);
    }

    @Override
    public CommandResult undo(CommandContext context) {
        var inventory = context.getTargetList();

        if (wasMerged) {
            Card existing = inventory.getCard(addedIndex);
            int restoredQuantity = existing.getQuantity() - quantity;
            inventory.editCard(addedIndex, null, restoredQuantity, null,
                    null, null, null, null, null, null);
        } else {
            inventory.removeCardByIndex(addedIndex);
        }

        context.getUi().printUndoSuccess(inventory);
        return new CommandResult(false);
    }

    private static boolean sameText(String left, String right) {
        String normalizedLeft = left == null ? "" : left.trim();
        String normalizedRight = right == null ? "" : right.trim();
        return normalizedLeft.equalsIgnoreCase(normalizedRight);
    }
}
