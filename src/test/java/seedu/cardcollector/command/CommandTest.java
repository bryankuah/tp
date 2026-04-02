package seedu.cardcollector.command;

import org.junit.jupiter.api.Test;
import seedu.cardcollector.Ui;
import seedu.cardcollector.UploadUndoState;
import seedu.cardcollector.card.CardHistoryEntry;
import seedu.cardcollector.card.CardHistoryType;
import seedu.cardcollector.card.CardsHistory;
import seedu.cardcollector.card.CardsList;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandTest {

    private CommandContext createCommandContext() {
        return createCommandContext(new ByteArrayOutputStream());
    }

    private CommandContext createCommandContext(ByteArrayOutputStream outputStream) {
        CardsList inventory = new CardsList();
        CardsList wishlist = new CardsList();

        InputStream inputStream = System.in;
        PrintStream printStream = new PrintStream(outputStream);
        Ui ui = new Ui(inputStream, printStream);

        return new CommandContext(
                ui, inventory, inventory, wishlist,
                null, new UploadUndoState(), new Stack<>());
    }

    //@@author HX2003
    @Test
    public void execute_addUndo_historySuccess() {
        for (int quantity = 0; quantity < 2; quantity++) {
            CommandContext commandContext = createCommandContext();

            Command addCommand = new AddCommand(null, "MyCard", quantity, 5.0f,
                    null, null, null, null, null, null);
            addCommand.execute(commandContext);
            commandContext.getCommandHistory().push(addCommand);

            new UndoCommand().execute(commandContext);

            CardsHistory history = commandContext.getInventory().getHistory();
            ArrayList<CardHistoryEntry> historyList = history.getSortedHistoryList(false);

            CardHistoryEntry entry0 = historyList.get(0);
            assertEquals(CardHistoryType.ADDED, entry0.getCardHistoryType());
            assertEquals("MyCard", entry0.getMostRecent().getName());
            assertEquals(quantity, entry0.getChangedQuantity());

            CardHistoryEntry entry1 = historyList.get(1);
            assertEquals(CardHistoryType.REMOVED, entry1.getCardHistoryType());
            assertEquals("MyCard", entry1.getMostRecent().getName());
            assertEquals(-quantity, entry1.getChangedQuantity());
        }
    }

    @Test
    public void execute_addMergeUndo_historySuccess() {
        CommandContext commandContext = createCommandContext();

        Command addCommand = new AddCommand(null, "MyCard", 4, 5.0f,
                null, null, null, null, null, null);

        addCommand.execute(commandContext);
        commandContext.getCommandHistory().push(addCommand);

        addCommand.execute(commandContext);
        commandContext.getCommandHistory().push(addCommand);

        new UndoCommand().execute(commandContext);

        CardsHistory history = commandContext.getInventory().getHistory();
        ArrayList<CardHistoryEntry> historyList = history.getSortedHistoryList(false);

        CardHistoryEntry entry0 = historyList.get(0);
        assertEquals(CardHistoryType.ADDED, entry0.getCardHistoryType());
        assertEquals(4, entry0.getChangedQuantity());

        CardHistoryEntry entry1 = historyList.get(1);
        assertEquals(CardHistoryType.ADDED, entry1.getCardHistoryType());
        assertEquals(4, entry1.getChangedQuantity());

        CardHistoryEntry entry2 = historyList.get(2);
        assertEquals(CardHistoryType.REMOVED, entry2.getCardHistoryType());
        assertEquals(-4, entry2.getChangedQuantity());
    }

    private void execute_addRemoveUndo_history(Function<CommandContext, Command> removeCommandFactory) {
        for (int quantity = 0; quantity < 2; quantity++) {
            CommandContext commandContext = createCommandContext();

            Command addCommand = new AddCommand(null, "MyCard", quantity, 5.0f,
                    null, null, null, null, null, null);
            addCommand.execute(commandContext);
            commandContext.getCommandHistory().push(addCommand);

            Command removeCommand = removeCommandFactory.apply(commandContext);
            removeCommand.execute(commandContext);
            commandContext.getCommandHistory().push(removeCommand);

            new UndoCommand().execute(commandContext);

            CardsHistory history = commandContext.getInventory().getHistory();
            ArrayList<CardHistoryEntry> historyList = history.getSortedHistoryList(false);

            assertEquals(CardHistoryType.ADDED, historyList.get(0).getCardHistoryType());
            assertEquals(CardHistoryType.REMOVED, historyList.get(1).getCardHistoryType());
            assertEquals(CardHistoryType.ADDED, historyList.get(2).getCardHistoryType());
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
                null, null, null, null, null, null);
        addCommand.execute(commandContext);
        commandContext.getCommandHistory().push(addCommand);

        Command editCommandIncreaseQuantity = new EditCommand(0, null, 8,
                null, null, null, null, null, null, null);
        editCommandIncreaseQuantity.execute(commandContext);
        commandContext.getCommandHistory().push(editCommandIncreaseQuantity);

        Command editCommandDecreaseQuantity = new EditCommand(0, null, 2,
                null, null, null, null, null, null, null);
        editCommandDecreaseQuantity.execute(commandContext);
        commandContext.getCommandHistory().push(editCommandDecreaseQuantity);

        Command editCommandChangeName = new EditCommand(0, "MyNamedCard", null,
                null, null, null, null, null, null, null);
        editCommandChangeName.execute(commandContext);
        commandContext.getCommandHistory().push(editCommandChangeName);

        new UndoCommand().execute(commandContext);
        new UndoCommand().execute(commandContext);
        new UndoCommand().execute(commandContext);
        new UndoCommand().execute(commandContext);

        CardsHistory history = commandContext.getInventory().getHistory();
        ArrayList<CardHistoryEntry> historyList = history.getSortedHistoryList(false);

        assertEquals(8, historyList.size());
    }

    //@@author WeiHeng2003
    @Test
    public void execute_addMergeUndo_restoresQuantity() {
        CommandContext commandContext = createCommandContext();

        Command add1 = new AddCommand(null, "MyCard", 3, 5.0f,
                null, null, null, null, null, null);
        Command add2 = new AddCommand(null, "MyCard", 2, 5.0f,
                null, null, null, null, null, null);

        add1.execute(commandContext);
        commandContext.getCommandHistory().push(add1);
        add2.execute(commandContext);
        commandContext.getCommandHistory().push(add2);

        assertEquals(5, commandContext.getInventory().getCard(0).getQuantity());

        new UndoCommand().execute(commandContext);

        assertEquals(1, commandContext.getInventory().getSize());
        assertEquals(3, commandContext.getInventory().getCard(0).getQuantity());
    }

    //@@author WeiHeng2003
    @Test
    public void execute_multipleUndos_restoresInOrder() {
        CommandContext commandContext = createCommandContext();

        Command add1 = new AddCommand(null, "CardA", 1, 5.0f,
                null, null, null, null, null, null);
        Command add2 = new AddCommand(null, "CardB", 1, 5.0f,
                null, null, null, null, null, null);

        add1.execute(commandContext);
        commandContext.getCommandHistory().push(add1);
        add2.execute(commandContext);
        commandContext.getCommandHistory().push(add2);

        new UndoCommand().execute(commandContext);
        assertEquals(1, commandContext.getInventory().getSize());
        assertEquals("CardA", commandContext.getInventory().getCard(0).getName());

        new UndoCommand().execute(commandContext);
        assertEquals(0, commandContext.getInventory().getSize());
    }

    //@@author WeiHeng2003
    @Test
    public void execute_removeInvalidIndex_notSavedToHistory() {
        CommandContext ctx = createCommandContext();

        Command remove = new RemoveCardByIndexCommand(0);
        remove.execute(ctx);

        if (remove.isReversible()) {
            ctx.getCommandHistory().push(remove);
        }
        assertEquals(0, ctx.getCommandHistory().size());
    }

    //@@author WeiHeng2003
    @Test
    public void execute_addSameCard_doesNotCreateDuplicateCard() {
        CommandContext ctx = createCommandContext();

        new AddCommand(null, "Pikachu", 2, 5.0f,
                null, null, null, null, null, null).execute(ctx);

        new AddCommand(null, "Pikachu", 3, 5.0f,
                null, null, null, null, null, null).execute(ctx);

        assertEquals(1, ctx.getInventory().getSize());
        assertEquals(5, ctx.getInventory().getCard(0).getQuantity());
    }


    //@@author Simplificatedd
    @Test
    public void execute_helpCommand_printsOverviewWithoutSaving() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CommandContext commandContext = createCommandContext(outputStream);

        CommandResult result = HelpCommand.overview().execute(commandContext);

        String output = outputStream.toString();
        assertFalse(result.getIsExit());
        assertFalse(result.shouldSave());
        assertTrue(output.contains("CardCollector commands:"));
    }

    @Test
    public void execute_addWithNote_success() {
        CommandContext commandContext = createCommandContext();

        Command addCommand = new AddCommand(null, "MyCard", 1, 5.0f,
                null, null, null, null, null, "binder");
        addCommand.execute(commandContext);

        assertEquals(1, commandContext.getInventory().getSize());
        assertEquals("binder", commandContext.getInventory().getCard(0).getNote());
    }

    @Test
    public void execute_editNote_success() {
        CommandContext commandContext = createCommandContext();

        Command addCommand = new AddCommand(null, "MyCard", 1, 5.0f,
                null, null, null, null, null, "old note");
        addCommand.execute(commandContext);

        Command editCommand = new EditCommand(0, null, null, null,
                null, null, null, null, null, "new note");
        editCommand.execute(commandContext);

        assertEquals("new note", commandContext.getInventory().getCard(0).getNote());
    }

    @Test
    public void execute_editNoteUndo_success() {
        CommandContext commandContext = createCommandContext();

        Command addCommand = new AddCommand(null, "MyCard", 1, 5.0f,
                null, null, null, null, null, "old note");
        addCommand.execute(commandContext);
        commandContext.getCommandHistory().push(addCommand);

        Command editCommand = new EditCommand(0, null, null, null,
                null, null, null, null, null, "new note");
        editCommand.execute(commandContext);
        commandContext.getCommandHistory().push(editCommand);

        new UndoCommand().execute(commandContext);

        assertEquals("old note", commandContext.getInventory().getCard(0).getNote());
    }

    @Test
    public void execute_addSameCardSameNote_merges() {
        CommandContext commandContext = createCommandContext();

        Command add1 = new AddCommand(null, "MyCard", 1, 5.0f,
                null, null, null, null, null, "binder");
        Command add2 = new AddCommand(null, "MyCard", 2, 5.0f,
                null, null, null, null, null, "binder");

        add1.execute(commandContext);
        add2.execute(commandContext);

        assertEquals(1, commandContext.getInventory().getSize());
        assertEquals(3, commandContext.getInventory().getCard(0).getQuantity());
        assertEquals("binder", commandContext.getInventory().getCard(0).getNote());
    }

    @Test
    public void execute_addSameCardDifferentNote_doesNotMerge() {
        CommandContext commandContext = createCommandContext();

        Command add1 = new AddCommand(null, "MyCard", 1, 5.0f,
                null, null, null, null, null, "gift");
        Command add2 = new AddCommand(null, "MyCard", 2, 5.0f,
                null, null, null, null, null, "trade");

        add1.execute(commandContext);
        add2.execute(commandContext);

        assertEquals(2, commandContext.getInventory().getSize());
    }

    @Test
    public void execute_duplicatesCommand_success() {
        CommandContext commandContext = createCommandContext();

        new AddCommand(null, "Eevee", 2, 4.0f,
                null, null, null, null, null, null).execute(commandContext);
        new AddCommand(null, "Mew", 1, 30.0f,
                null, null, null, null, null, null).execute(commandContext);
        new AddCommand(null, "Squirtle", 3, 3.0f,
                null, null, null, null, null, null).execute(commandContext);

        new DuplicatesCommand().execute(commandContext);

        String output = commandContext.getUi().toString();
        assertEquals(2, commandContext.getInventory().getDuplicateCards().size());
    }

    @Test
    public void execute_reorderCommand_reordersInventory() {
        CommandContext commandContext = createCommandContext();

        new AddCommand(null, "Zebra", 1, 10.0f,
                null, null, null, null, null, null).execute(commandContext);
        new AddCommand(null, "Apple", 1, 20.0f,
                null, null, null, null, null, null).execute(commandContext);
        new AddCommand(null, "Monkey", 1, 15.0f,
                null, null, null, null, null, null).execute(commandContext);

        new ReorderCommand(seedu.cardcollector.card.CardSortCriteria.NAME, true)
                .execute(commandContext);

        assertEquals("Apple", commandContext.getInventory().getCard(0).getName());
        assertEquals("Monkey", commandContext.getInventory().getCard(1).getName());
        assertEquals("Zebra", commandContext.getInventory().getCard(2).getName());
    }

    @Test
    public void execute_editInvalidIndex_noChange() {
        CommandContext commandContext = createCommandContext();

        Command editCommand = new EditCommand(0, "NewName", null, null,
                null, null, null, null, null, null);

        editCommand.execute(commandContext);

        assertEquals(0, commandContext.getInventory().getSize());
    }

    @Test
    public void execute_removeInvalidName_noChange() {
        CommandContext commandContext = createCommandContext();

        Command removeCommand = new RemoveCardByNameCommand("GhostCard");
        removeCommand.execute(commandContext);

        assertEquals(0, commandContext.getInventory().getSize());
    }

    @Test
    public void execute_removeInvalidIndex_noChange() {
        CommandContext commandContext = createCommandContext();

        Command removeCommand = new RemoveCardByIndexCommand(0);
        removeCommand.execute(commandContext);

        assertEquals(0, commandContext.getInventory().getSize());
    }
}
