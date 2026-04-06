package seedu.cardcollector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Stack;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.cardcollector.card.Card;
import seedu.cardcollector.card.CardsList;
import seedu.cardcollector.command.Command;
import seedu.cardcollector.command.CommandContext;
import seedu.cardcollector.command.UploadCommand;
import seedu.cardcollector.command.DownloadCommand;
import seedu.cardcollector.command.UndoUploadCommand;
import seedu.cardcollector.ui.Ui;

public class StorageTest {
    @TempDir
    Path tempDir;

    @Test
    public void saveLoad_roundTripPreservesCardsHistoryAndSorting() throws Exception {
        CardsList inventory = new CardsList();

        Card activeCard = new Card.Builder()
                .name("Pikachu")
                .price(5.5f)
                .quantity(2)
                .cardSet("Base Set")
                .rarity("Rare")
                .condition("Near Mint")
                .language("English")
                .cardNumber("58/102")
                .addTag("deck")
                .addTag("trade")
                .build();
        inventory.addCard(activeCard);
        activeCard.setLastAdded(Instant.parse("2026-03-26T09:00:00Z"));
        activeCard.setLastModified(Instant.parse("2026-03-27T10:00:00Z"));

        Card removedCard = new Card.Builder()
                .name("Charizard")
                .price(99.99f)
                .quantity(1)
                .build();
        inventory.addCard(removedCard);
        removedCard.setLastAdded(Instant.parse("2026-03-25T08:00:00Z"));
        removedCard.setLastModified(Instant.parse("2026-03-25T08:00:00Z"));
        inventory.removeCardByName("Charizard");

        CardsList wishlist = new CardsList();
        Card wishlistCard = new Card.Builder()
                .name("Umbreon")
                .price(42.0f)
                .quantity(3)
                .build();
        wishlist.addCard(wishlistCard);
        wishlistCard.setLastAdded(Instant.parse("2026-03-24T07:00:00Z"));
        wishlistCard.setLastModified(Instant.parse("2026-03-24T07:00:00Z"));

        Storage storage = new Storage(tempDir.resolve("cardcollector.txt"));
        storage.save(new AppState(inventory, wishlist));

        AppState loadedState = storage.load();
        CardsList loadedInventory = loadedState.getInventory();
        CardsList loadedWishlist = loadedState.getWishlist();

        assertEquals(1, loadedInventory.getSize());
        assertEquals(1, loadedWishlist.getSize());

        Card loadedActiveCard = loadedInventory.getCard(0);

        assertEquals("Pikachu", loadedActiveCard.getName());
        assertEquals("Base Set", loadedActiveCard.getCardSet());
        assertEquals("58/102", loadedActiveCard.getCardNumber());
        assertEquals(true, loadedActiveCard.hasTag("deck"));
        assertEquals(true, loadedActiveCard.hasTag("trade"));
        assertEquals(Instant.parse("2026-03-26T09:00:00Z"), loadedActiveCard.getLastAdded());
        assertNotNull(loadedWishlist.getCard(0).getLastAdded());
    }

    @Test
    public void downloadUpload_commandsTransferWholeAppState() throws Exception {
        CardsList inventory = new CardsList();
        inventory.addCard(new Card.Builder()
                .name("Pikachu")
                .price(5.5f)
                .quantity(2)
                .build());

        CardsList wishlist = new CardsList();
        wishlist.addCard(new Card.Builder()
                .name("Umbreon")
                .price(42.0f)
                .quantity(1)
                .build());

        Path activePath = tempDir.resolve("active.txt");
        Path exportedPath = tempDir.resolve("exports").resolve("backup.txt");
        Storage activeStorage = new Storage(activePath);
        UploadUndoState uploadUndoState = new UploadUndoState();
        CommandContext downloadContext = new CommandContext(
                new StubUi(true), inventory, inventory, wishlist,
                activeStorage, uploadUndoState, new Stack<Command>());

        new DownloadCommand(exportedPath).execute(downloadContext);

        CardsList importedInventory = new CardsList();
        CardsList importedWishlist = new CardsList();
        importedInventory.addCard(new Card.Builder()
                .name("Eevee")
                .price(3.0f)
                .quantity(5)
                .build());
        CommandContext uploadContext = new CommandContext(
                new StubUi(true), importedInventory, importedInventory, importedWishlist,
                activeStorage, uploadUndoState, new Stack<Command>());

        new UploadCommand(exportedPath).execute(uploadContext);

        assertEquals(1, importedInventory.getSize());
        assertEquals("Pikachu", importedInventory.getCard(0).getName());
        assertEquals(1, importedWishlist.getSize());
        assertEquals("Umbreon", importedWishlist.getCard(0).getName());
    }

    @Test
    public void upload_cancelled_doesNotChangeState() throws Exception {
        CardsList inventory = new CardsList();
        inventory.addCard(new Card.Builder()
                .name("Current")
                .price(1.0f)
                .quantity(1)
                .build());
        CardsList wishlist = new CardsList();
        wishlist.addCard(new Card.Builder()
                .name("Wanted")
                .price(2.0f)
                .quantity(2)
                .build());

        Path exportedPath = tempDir.resolve("backup.txt");
        new Storage(exportedPath).save(new AppState(
                buildListWithSingleCard("Imported", 3.0f, 3),
                buildListWithSingleCard("Imported Wish", 4.0f, 4)));

        UploadUndoState uploadUndoState = new UploadUndoState();
        CommandContext uploadContext = new CommandContext(
                new StubUi(false), inventory, inventory, wishlist, new Storage(tempDir.resolve("active.txt")),
                uploadUndoState, new Stack<Command>());

        new UploadCommand(exportedPath).execute(uploadContext);

        assertEquals("Current", inventory.getCard(0).getName());
        assertEquals("Wanted", wishlist.getCard(0).getName());
        assertEquals(false, uploadUndoState.hasBackup());
    }

    @Test
    public void undoUpload_restoresPreviousState() throws Exception {
        CardsList originalInventory = buildListWithSingleCard("Original", 1.0f, 1);
        CardsList originalWishlist = buildListWithSingleCard("Original Wish", 2.0f, 2);
        Path exportedPath = tempDir.resolve("backup.txt");
        new Storage(exportedPath).save(new AppState(
                buildListWithSingleCard("Imported", 3.0f, 3),
                buildListWithSingleCard("Imported Wish", 4.0f, 4)));

        UploadUndoState uploadUndoState = new UploadUndoState();
        Storage activeStorage = new Storage(tempDir.resolve("active.txt"));
        CommandContext uploadContext = new CommandContext(
                new StubUi(true), originalInventory, originalInventory,
                originalWishlist, activeStorage, uploadUndoState, new Stack<Command>());

        new UploadCommand(exportedPath).execute(uploadContext);
        new UndoUploadCommand().execute(new CommandContext(
                new StubUi(true), originalInventory, originalInventory,
                originalWishlist, activeStorage, uploadUndoState, new Stack<Command>()));

        assertEquals("Original", originalInventory.getCard(0).getName());
        assertEquals("Original Wish", originalWishlist.getCard(0).getName());
        assertEquals(false, uploadUndoState.hasBackup());
    }

    @Test
    public void load_missingFile_returnsEmptyState() throws Exception {
        Storage storage = new Storage(tempDir.resolve("nonexistent.txt"));

        AppState state = storage.load();

        assertEquals(0, state.getInventory().getSize());
        assertEquals(0, state.getWishlist().getSize());
    }

    private static CardsList buildListWithSingleCard(String name, float price, int quantity) {
        CardsList list = new CardsList();
        list.addCard(new Card.Builder()
                .name(name)
                .price(price)
                .quantity(quantity)
                .build());
        return list;
    }

    private static class StubUi extends Ui {
        private final boolean confirmUpload;

        private StubUi(boolean confirmUpload) {
            this.confirmUpload = confirmUpload;
        }

        @Override
        public boolean confirmUpload(Path sourcePath, Path activePath) {
            return confirmUpload;
        }
    }
}
