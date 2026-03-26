package seedu.cardcollector.command;

import seedu.cardcollector.CardsList;
import seedu.cardcollector.Card;
import seedu.cardcollector.Ui;
import java.util.UUID;

public class AddCommand extends Command {
    private final UUID uid;
    private final String name;
    private final int quantity;
    private final float price;

    public AddCommand(UUID uid, String name, int quantity, float price) {
        this.uid = uid;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public CommandResult execute(Ui ui, CardsList inventory) {
        Card newCard = new Card.Builder()
                .uid(uid)
                .name(name)
                .price(price)
                .quantity(quantity)
                .build();

        inventory.addCard(newCard);
        ui.printAdded(inventory);
        return new CommandResult(false);
    }
}
