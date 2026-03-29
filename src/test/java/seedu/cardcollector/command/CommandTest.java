package seedu.cardcollector.command;

import org.junit.jupiter.api.Test;
import seedu.cardcollector.Ui;
import seedu.cardcollector.UploadUndoState;
import seedu.cardcollector.card.CardFieldChange;
import seedu.cardcollector.card.CardHistoryEntry;
import seedu.cardcollector.card.CardHistoryType;
import seedu.cardcollector.card.CardsHistory;
import seedu.cardcollector.card.CardsList;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class CommandTest {
    private CommandContext createCommandContext() {
        CardsList inventory = new CardsList();
        CardsList wishlist = new CardsList();

        OutputStream outputStream = new ByteArrayOutputStream();
        InputStream inputStream = System.in;
        PrintStream printStream = new PrintStream(outputStream);
        Ui ui = new Ui(inputStream, printStream);

        return new CommandContext(
                ui, inventory, inventory, wishlist,
                 null, new UploadUndoState(), new Stack<>());
    }

    /**
     * Tests whether 'add', 'edit', 'removename', 'removeindex' and 'undo' commands
     * is correctly recorded in history.
     * Due to the presence of 'undo' command, extra testing is needed here.
     * The expected outcome of 'undo' command, is that it appends the reverse
     * operation to the history. Existing history should not be overridden.
     */

    //@@author HX2003
    @Test
    public void execute_addUndo_historySuccess() {
        // Here, quantity of 0 and quantity of 1 is tested
        for(int quantity=0; quantity<2; quantity++) {
            CommandContext commandContext = createCommandContext();

            Command addCommand = new AddCommand(null, "MyCard", quantity, 5.0f,
                    null, null, null, null, null);
            addCommand.execute(commandContext);
            commandContext.getCommandHistory().push(addCommand);

            new UndoCommand().execute(commandContext);

            CardsHistory history = commandContext.getInventory().getHistory();
            ArrayList<CardHistoryEntry> historyList = history.getSortedHistoryList(false);

            // Now check whether the history is correct
            CardHistoryEntry entry0 = historyList.get(0);
            assertEquals(CardHistoryType.ADDED, entry0.getCardHistoryType());
            assertEquals("MyCard", entry0.getMostRecent().getName());
            assertEquals(quantity, entry0.getChangedQuantity());

            // The following history should be the after 'undo' has been executed
            CardHistoryEntry entry1 = historyList.get(1);
            assertEquals(CardHistoryType.REMOVED, entry1.getCardHistoryType());
            assertEquals("MyCard", entry1.getMostRecent().getName());
            assertEquals(-quantity, entry1.getChangedQuantity());
        }
    }

    /**
     * Test whether adding card has that the same fields as those already in inventory
     * is correctly recorded in history.
     */
    @Test
    public void execute_addMergeUndo_historySuccess() {
        CommandContext commandContext = createCommandContext();

        Command addCommand = new AddCommand(null, "MyCard", 4, 5.0f,
                null, null, null, null, null);

        addCommand.execute(commandContext);
        commandContext.getCommandHistory().push(addCommand);

        // The same command is executed
        addCommand.execute(commandContext);
        commandContext.getCommandHistory().push(addCommand);

        new UndoCommand().execute(commandContext);

        CardsHistory history = commandContext.getInventory().getHistory();
        ArrayList<CardHistoryEntry> historyList = history.getSortedHistoryList(false);

        // Now check whether the history is correct
        CardHistoryEntry entry0 = historyList.get(0);
        assertEquals(CardHistoryType.ADDED, entry0.getCardHistoryType());
        assertEquals("MyCard", entry0.getMostRecent().getName());
        assertEquals(4, entry0.getChangedQuantity());

        CardHistoryEntry entry1 = historyList.get(1);
        assertEquals(CardHistoryType.ADDED, entry1.getCardHistoryType());
        assertEquals("MyCard", entry1.getMostRecent().getName());
        assertEquals(4, entry1.getChangedQuantity());

        // The following history should be the after 'undo' has been executed
        CardHistoryEntry entry2 = historyList.get(2);
        assertEquals(CardHistoryType.REMOVED, entry2.getCardHistoryType());
        assertEquals("MyCard", entry2.getMostRecent().getName());
        assertEquals(-4, entry2.getChangedQuantity());
    }


    private void execute_addRemoveUndo_history(Function<CommandContext, Command> removeCommandFactory) {
        // Here, quantity of 0 and quantity of 1 is tested
        for(int quantity=0; quantity<2; quantity++) {
            CommandContext commandContext = createCommandContext();

            Command addCommand = new AddCommand(null, "MyCard", quantity, 5.0f,
                    null, null, null, null, null);
            addCommand.execute(commandContext);
            commandContext.getCommandHistory().push(addCommand);

            Command removeCommand = removeCommandFactory.apply(commandContext);
            removeCommand.execute(commandContext);
            commandContext.getCommandHistory().push(removeCommand);

            new UndoCommand().execute(commandContext);

            CardsHistory history = commandContext.getInventory().getHistory();
            ArrayList<CardHistoryEntry> historyList = history.getSortedHistoryList(false);

            // Now check whether the history is correct
            CardHistoryEntry entry0 = historyList.get(0);
            assertEquals(CardHistoryType.ADDED, entry0.getCardHistoryType());
            assertEquals("MyCard", entry0.getMostRecent().getName());
            assertEquals(quantity, entry0.getChangedQuantity());

            CardHistoryEntry entry1 = historyList.get(1);
            assertEquals(CardHistoryType.REMOVED, entry1.getCardHistoryType());
            assertEquals("MyCard", entry1.getMostRecent().getName());
            assertEquals(-quantity, entry1.getChangedQuantity());

            // The following history should be the after 'undo' has been executed
            CardHistoryEntry entry2 = historyList.get(2);
            assertEquals(CardHistoryType.ADDED, entry2.getCardHistoryType());
            assertEquals("MyCard", entry2.getMostRecent().getName());
            assertEquals(quantity, entry2.getChangedQuantity());
        }
    }

    @Test
    public void execute_addRemoveByNameUndo_historySuccess() {
        execute_addRemoveUndo_history(ctx -> new RemoveCardByNameCommand("MyCard"));
    }

    @Test
    public void execute_addRemoveByIndexUndo_historySuccess() {
        execute_addRemoveUndo_history(ctx -> new RemoveCardByIndexCommand(0));
    }

    @Test
    public void execute_addEditUndo_historySuccess() {
        CommandContext commandContext = createCommandContext();

        Command addCommand = new AddCommand(null, "MyCard", 5, 5.0f,
                null, null, null, null, null);
        addCommand.execute(commandContext);
        commandContext.getCommandHistory().push(addCommand);

        // Let's increase the quantity by 3 to 8
        Command editCommandIncreaseQuantity = new EditCommand(0, null, 8,
                null, null, null, null, null, null);
        editCommandIncreaseQuantity.execute(commandContext);
        commandContext.getCommandHistory().push(editCommandIncreaseQuantity);

        // Let's decrease the quantity by 6 to 2
        Command editCommandDecreaseQuantity = new EditCommand(0, null, 2,
                null, null, null, null, null, null);
        editCommandDecreaseQuantity.execute(commandContext);
        commandContext.getCommandHistory().push(editCommandDecreaseQuantity);

        // Let's change the name to "MyNamedCard"
        Command editCommandChangeName = new EditCommand(0, "MyNamedCard", null,
                null, null, null, null, null, null);
        editCommandChangeName.execute(commandContext);
        commandContext.getCommandHistory().push(editCommandChangeName);

        new UndoCommand().execute(commandContext);
        new UndoCommand().execute(commandContext);
        new UndoCommand().execute(commandContext);
        new UndoCommand().execute(commandContext);

        CardsHistory history = commandContext.getInventory().getHistory();
        ArrayList<CardHistoryEntry> historyList = history.getSortedHistoryList(false);

        // Now check whether the history is correct
        CardHistoryEntry entry0 = historyList.get(0);
        assertEquals(CardHistoryType.ADDED, entry0.getCardHistoryType());
        assertEquals("MyCard", entry0.getMostRecent().getName());
        assertEquals(5, entry0.getChangedQuantity());

        CardHistoryEntry entry1 = historyList.get(1);
        assertEquals(CardHistoryType.ADDED, entry1.getCardHistoryType());
        assertEquals("MyCard", entry1.getMostRecent().getName());
        assertEquals(3, entry1.getChangedQuantity());

        CardHistoryEntry entry2 = historyList.get(2);
        assertEquals(CardHistoryType.REMOVED, entry2.getCardHistoryType());
        assertEquals("MyCard", entry2.getMostRecent().getName());
        assertEquals(-6, entry2.getChangedQuantity());

        CardHistoryEntry entry3 = historyList.get(3);
        assertEquals(CardHistoryType.MODIFIED, entry3.getCardHistoryType());
        assertEquals("MyNamedCard", entry3.getMostRecent().getName());
        assertEquals(0, entry3.getChangedQuantity());
        CardFieldChange changes3 = entry3.getChangedFields().get("name");
        assertEquals("MyCard", changes3.previous());
        assertEquals("MyNamedCard", changes3.current());
        assertFalse(entry3.getChangedFields().containsKey("quantity"));

        // The following history should be the after 'undo' has been executed
        CardHistoryEntry entry4 = historyList.get(4);
        assertEquals(CardHistoryType.MODIFIED, entry4.getCardHistoryType());
        assertEquals("MyCard", entry4.getMostRecent().getName());
        assertEquals(0, entry4.getChangedQuantity());
        CardFieldChange changes4 = entry4.getChangedFields().get("name");
        assertEquals("MyNamedCard", changes4.previous());
        assertEquals("MyCard", changes4.current());

        CardHistoryEntry entry5 = historyList.get(5);
        assertEquals(CardHistoryType.ADDED, entry5.getCardHistoryType());
        assertEquals("MyCard", entry5.getMostRecent().getName());
        assertEquals(6, entry5.getChangedQuantity());

        CardHistoryEntry entry6 = historyList.get(6);
        assertEquals(CardHistoryType.REMOVED, entry6.getCardHistoryType());
        assertEquals("MyCard", entry6.getMostRecent().getName());
        assertEquals(-3, entry6.getChangedQuantity());

        CardHistoryEntry entry7 = historyList.get(7);
        assertEquals(CardHistoryType.REMOVED, entry7.getCardHistoryType());
        assertEquals("MyCard", entry7.getMostRecent().getName());
        assertEquals(-5, entry7.getChangedQuantity());
    }
}
