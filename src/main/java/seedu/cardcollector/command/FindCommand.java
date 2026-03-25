package seedu.cardcollector.command;

import seedu.cardcollector.Card;
import seedu.cardcollector.CardsList;
import seedu.cardcollector.Ui;

import java.util.ArrayList;

public class FindCommand extends Command {
    private final String name;
    private final Float price;
    private final Integer quantity;

    public FindCommand(String name, Float price, Integer quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public CommandResult execute(Ui ui, CardsList inventory) {
        ArrayList<Card> results = inventory.findCards(name, price, quantity);
        ui.printFound(results);
        return new CommandResult(false);
    }
}
