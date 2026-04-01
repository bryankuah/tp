# Developer Guide

## Table of Contents
- [Acknowledgements](#acknowledgements)
- [Design & Implementation](#design--implementation)
  - [Add Feature](#add-feature)
    - [Architecture-level](#architecture-level)
    - [Implementation](#implementation)
    - [Class Diagram](#class-diagram)
  - [Edit Feature](#edit-feature)
    - [Architecture-level](#architecture-level-1)
    - [Implementation](#implementation-key-code-snippets-1)
    - [Sequence Diagram](#sequence-diagram-edit-1-n-dragonite-q-3)
  - [Undo Feature](#undo-feature)
    - [Architecture-level](#architecture-level-2)
    - [Implementation](#implementation-key-code-snippets-2)
    - [Sequence Diagram](#sequence-diagram)
  - [List Feature](#list-feature)
  - [History Feature](#history-feature)
  - [Wishlist Feature](#wishlist-feature)
    - [Architecture-level](#architecture-level-3)
    - [Implementation](#implementation-key-code-snippets-3)
    - [Class Diagram](#class-diagram-2)
    - [Sequence Diagram](#sequence-diagram-wishlist-add-example)
  - [Disambiguator](#disambiguator)
- [Appendix: Product Scope](#appendix-product-scope)
  - [Target User Profile](#target-user-profile)
  - [Value Proposition](#value-proposition)
- [Appendix: User Stories](#appendix-user-stories)
- [Appendix: Non-Functional Requirements](#appendix-non-functional-requirements)
- [Appendix: Glossary](#appendix-glossary)
- [Appendix: Instructions for Manual Testing](#appendix-instructions-for-manual-testing)

## Acknowledgements
- For the PlantUML styling, we adapted from [addressbook-level3](https://github.com/se-edu/addressbook-level3/blob/master/docs/diagrams/style.puml).

{list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}

## Design & implementation

The architecture of CardCollector consists of three main components:
1. **`Ui`**: Handles all interactions with the user (reading input and printing formatted output).
2. **`CardCollector`**: The main logic controller that parses user input and executes the appropriate commands.
3. **`CardsList` & `Card`**: The data structures storing the inventory and individual card details, including timestamp history.

### Add Feature

#### Architecture-level
1. `CardCollector` reads the raw inputs using `Ui.readInput()` and passes it to `Parser.parse()`.
2. `Parser.handleAdd()` checks for the 3 required flags (`/n`,`/q`,`/p`), then extracts the flag value and constructs an `AddCommand`
3. `CardCollector` then creates a `CommandContext` and calls `command.execute(context)`.
4. `AddCommand.execute()` calls `targetList.addCard(newCard)`.
5. `CardList.addCard` scans for an existing card with identical name, price, etc... If found, it increments that card's quantity and sets the timestamp `lastAdded`.  
Otherwise, it adds the new card at the end of the list. After which, regardless of the case records a CardHistory entry

#### Implementation
1. The core logic is in `CardsList.java`:  
```java
public void addCard(Card newCard) {
    Instant currentInstant = Instant.now();

    for (Card existingCard : cards) {
        if (isSameCardVariant(existingCard, newCard)) {
            Card originalCard = existingCard.copy();

            int updatedQuantity = existingCard.getQuantity() + newCard.getQuantity();
            existingCard.setQuantity(updatedQuantity);
            existingCard.setLastAdded(currentInstant);

            this.history.add(originalCard, existingCard.copy());
            return;
        }
    }

    newCard.setLastAdded(currentInstant);
    cards.add(newCard);
    this.history.add(null, newCard.copy());
}
```  
The check for existing card to decide merge or append:
```java
private static boolean isSameCardVariant(Card first, Card second) {
    return first.getName().equalsIgnoreCase(second.getName())
            && first.getPrice() == second.getPrice()
            && normalized(first.getCardSet()).equals(normalized(second.getCardSet()))
            && normalized(first.getRarity()).equals(normalized(second.getRarity()))
            && normalized(first.getCondition()).equals(normalized(second.getCondition()))
            && normalized(first.getLanguage()).equals(normalized(second.getLanguage()))
            && normalized(first.getCardNumber()).equals(normalized(second.getCardNumber()));
}
```

#### Class Diagram
<img src="images/AddCommandClassDiagram.svg" width="900" />

### History Feature
The history feature is a log of when cards were added, modified, or removed.
It is not intended to represent command history, but rather a changelog of the cards in the inventory.

#### History Command
The `history` command displays the historical log that were generated when other commands were executed.
As such, this command itself does not change or mutate any data.

To model the interactions that occur when the user issues the command `history all added`, below is a *Sequence Diagram* to illustrate it.
Some details related to UI input handling have been omitted for brevity.

<img src="images/HistorySequenceDiagram.svg" width="550" />

**Note:** The lifeline for `HistoryCommand` actually ends at the destroy marker (X), but due to a limitation in PlantUML, the dotted lifeline continues downwards.

### Edit Feature

The `edit` command allows users to change the name, quantity, or price of any card in the list.

#### Architecture-level
When the user types `edit 1 /n Dragonite /q 3`:
1. `Ui` reads the raw input.
2. `CardCollector` passes the input to `Parser`.
3. `Parser` creates an `EditCommand` object.
4. `CardCollector` calls `execute()` on the command, passing the correct `CardsList`.
5. The card is updated and the UI shows the new list.

#### Implementation (key code snippets)

**Parsing logic for `edit`** in `Parser.java` (inside `handleEdit`):

```java
String[] parts = args.trim().split(REGEX_WHITESPACES, 2);
int index = Integer.parseInt(parts[0].trim()) - 1;

String flagArgs = parts.length > 1 ? parts[1] : "";

String name = null;
Integer quantity = null;
Float price = null;

if (flagArgs.contains("/n")) {
    name = flagArgs.split("/n")[1].split("/q|/p")[0].trim();
}
if (flagArgs.contains("/q")) {
    quantity = Integer.parseInt(flagArgs.split("/q")[1].split("/n|/p")[0].trim());
}
if (flagArgs.contains("/p")) {
    price = Float.parseFloat(flagArgs.split("/p")[1].split("/n|/q")[0].trim());
}

if (name == null && quantity == null && price == null) {
    throw new ParseInvalidArgumentException(...);
}
return new EditCommand(index, name, quantity, price);
```

**Core editing logic** in `CardsList.java`:

```java
public void editCard(int index, String newName, Integer newQuantity, Float newPrice) {
    Card card = cards.get(index);
    Instant currentInstant = Instant.now();
    boolean anyChange = false;

    if (newName != null && !newName.trim().isEmpty()) {
        card.setName(newName.trim());
        anyChange = true;
    }
    if (newQuantity != null) {
        card.setQuantity(newQuantity);
        anyChange = true;
    }
    if (newPrice != null) {
        card.setPrice(newPrice);
        anyChange = true;
    }

    if (anyChange) {
        card.setLastModified(currentInstant);
    }
}
```

**Success message** in `Ui.java`:

```java
public void printEdited(CardsList inventory, int index) {
    System.out.println("I have edited card " + (index + 1) + "!");
    printList(inventory);
}
```

#### Sequence Diagram (`edit 1 /n Dragonite /q 3`)
<img src="images/EditSequenceDiagram.svg" width="900" />

**Design decisions**
- Require **at least one** field to be edited (enforced in Parser).
- Reuse existing flag-parsing style (`/n`, `/q`, `/p`).
- `lastModified` is updated automatically so `history modified` works without extra changes.

**Alternatives considered**
- A single `UpdateFieldCommand` for every field — rejected (too many tiny classes).
- Editing by name instead of index — rejected to keep consistency with `remove INDEX`.

### Undo Feature

The 'undo' command allows users to reverse the most recent [reversible command](#reversible-commands) by popping it from the `commandHistory` stack and calling its `undo(context)` method.

#### Architecture-level

1. `Parser` produces a `UndoCommand` and then `CardCollector` calls `execute(context)`
2. `UndoCommand` retrieves the `commandHistory` stack from `context`.
  - If empty, it prints "Nothing to Undo" and returns immediately.
  - Otherwise, it calls `history.pop()` to get the last reversible command and calls `lastCommand.undo(context)`
3. The previous command performs its targeted reversal depending on the command to undo (refer to alt frames in sequence diagram).
4. `ui.printUndoSuccess(list)` is called and `CommandResult(isExit=false)` is returned and `CardCollector` calls `storage.save()`

#### Implementation (key code snippets)

After a `Command` executes, `CardCollector` pushes it onto the `commandHistory` stack only if `isReversible()` is `true`. This stack is what `UndoCommand` pops from.

In `CardCollector`:
```java
if (command.isReversible()) {
    context.getCommandHistory().push(command);
}
```

In `UndoCommand`:
```java
Command lastCommand = history.pop();
return lastCommand.undo(context);
```

If the `lastCommand` was an:
- `AddCommand`: branches on whether the original add was a merge or a new card entry.
    ```java
    if (wasMerged) {
        Card existing = inventory.getCard(addedIndex);
        int restoredQuantity = existing.getQuantity() - quantity;
        inventory.editCard(addedIndex, null, restoredQuantity, null, null, null, null, null, null);
    } else {
        inventory.removeCardByIndex(addedIndex);
    }
    ```
- `EditCommand`: saves old field values and sets isReversible only if something actually changes
  ```java
    this.oldName = card.getName();
    this.oldQuantity = card.getQuantity();
    // ...other fields...
    boolean changed = inventory.editCard(targetIndex, newName, newQuantity, newPrice,
            newCardSet, newRarity, newCondition, newLanguage, newCardNumber);
    this.isReversible = changed;

    if (changed) {
        ui.printEdited(inventory, targetIndex);
    } else {
        ui.printNotEdited(inventory);
    }
    ```
  `EditCommand.undo()` then restores all fields by calling `editCard` with the saved old values:
    ```java
    public CommandResult undo(CommandContext context) {
    context.getTargetList().editCard(targetIndex, oldName, oldQuantity, oldPrice,
            oldCardSet, oldRarity, oldCondition, oldLanguage, oldCardNumber);
    ```
- `RemoveCardByIndexCommand` or `RemoveCardByNameCommand`: saves the card and its index
    ```java
    this.removedCard = inventory.getCard(targetIndex);
    this.removedIndex = targetIndex;
    ```
  `RemoveCardByIndexCommand.undo()` or `RemoveCardByNameCommand.undo()` then re-inserts the card at the same position
    ```java
  context.getTargetList().addCardAtIndex(removedIndex, removedCard);
    ```

#### Sequence Diagram
<img src="images/UndoSequenceDiagram.svg" width="900" />


### List Feature
This feature lists all cards in the current list in a sorted order.

#### List Command
The parsing of this command uses the [Disambiguator](#disambiguator) to support fuzzy arguments.


### History Feature
The cards history is a log of when cards were added, modified, or removed.
It should not be confused with command history, as its primary purpose is to record a changelog of the cards in the inventory,
therefore `undo` command does not revert the history, but rather adds to the history.

#### Design decisions
- For each history entry, a deep copy of the previous and current card is stored.
- 3 category types were devised. They are **mutually exclusive**
  to ensure they can be listed in a chronological sequence without duplicated entries representing the same event.
  - An `ADDED` entry occurs when a new or existing card is added, or when the edit command increases the quantity of the card.
  - A `MODIFIED` entry occurs when a card value is changed, **excluding** any changes to the quantity of the card.
  - A `REMOVED` entry occurs when a card is removed, or when the edit command decreases the quantity of the card.

#### Architecture flow
Whenever an `add`, `edit`, `remove*`, `tag` or any other command that changes the inventory is executed
1. A new `CardHistoryEntry` is created. It stores the previous version of the card before any changes (if any), and
   the current version of the card after the changes (if any).
2. This new entry is added to `CardsHistory`.

<img src="images/HistoryClassDiagram.svg" width="550" />

#### Alternatives considered
- A more compact way to store the history, is to track what changed instead of storing two copies of the card.
  While this solution is space-saving, it increases the complexity of decoding and encoding of the history state.


#### History Command
The `history` command simply displays the historical log that were generated when other commands were executed.
As such, this command itself does not change or mutate any data.
The parsing of this command uses the [Disambiguator](#disambiguator) to support fuzzy arguments.

To model the interactions that occur when the user issues the command `history all added`, below is a *Sequence Diagram* to illustrate it.
Some details related to `UI` input handling, and `CardsHistory` have been omitted for brevity.

<img src="images/HistorySequenceDiagram.svg" width="550" />

**Note:** The lifeline for `HistoryCommand` actually ends at the destroy marker (X), but due to a limitation in PlantUML, the dotted lifeline continues downwards.


### Wishlist Feature

The wishlist is a completely separate card list that supports **every** existing command (add, edit, list, find, remove, history, etc.). Users must prefix commands with `wishlist `.

#### Architecture-level
`CardCollector` holds **two independent** `CardsList` instances (`inventory` and `wishlist`).  
Prefix detection and routing to the correct list happen **only** in `CardCollector.run()`. All command classes and the `Parser` remain untouched.

#### Implementation (key code snippets)

**Prefix detection and list routing** in `CardCollector.run()`:

```java
boolean isWishlistCommand = false;
String parseInput = input;

if (input.toLowerCase().startsWith("wishlist ")) {
    isWishlistCommand = true;
    parseInput = input.substring(9).trim();
}

...

Command command = parser.parse(parseInput);
CardsList targetList = isWishlistCommand ? wishlist : inventory;
CommandResult result = command.execute(ui, targetList);
```

**Generic `printList` method** in `Ui.java` (used by both lists):

```java
public void printList(CardsList list) {
    if (listSize == 0) {
        System.out.println("Your card list is empty!");
    } else {
        System.out.println("Here is your card list!");
        for (int i = 0; i < listSize; i++) {
            System.out.println((i + 1) + ". " + list.getCard(i));
        }
    }
}
```

#### Class Diagram
<img src="images/WishlistClassDiagram.svg" width="200"/>

#### Sequence Diagram (`wishlist add` example)
<img src="images/WishlistSequenceDiagram.svg" width="900" />

**Design decisions**
- Two separate `CardsList` objects inside `CardCollector` because behaviour is identical.
- All routing logic is confined to `CardCollector.run()` so no command classes or Parser needed changes.
- Each list keeps its own history, so `history` and `history modified` work independently.

**Alternatives considered**
- Wrapper commands (e.g. `WishlistAddCommand`) — rejected (massive duplication).
- Single `CardCollectionManager` with a map — rejected (overkill for exactly two lists).


### Disambiguator
The `Disambiguator` takes an input string and matches it against a list of keywords strings
to determine which one the user intended to enter.
This is to support fuzzy arguments in certain commands to make it faster for users to type.

* To illustrate, if the keywords are "share", "shard", "shout"
* Input of "sh" matches all 3 keywords, as we cannot determine which it is, an exception is thrown.
* Input of "sha" matches all 2 keywords, as we cannot determine which it is, an exception is thrown.
* Input of "shar" matches "shard", thus the user probably intended to enter the string "shard".


## Appendix: Product Scope
### Target User Profile
- Trading Card Game (TCG) collectors
- Requires a fast and easy way to update quantity, check prices and move cards from wishlist
- is reasonably comfortable using CLI apps
### Value Proposition
- Quick commands to track cards that you currently own without having to find physical binders

## Appendix: User Stories

| Version | As a ...      | I want to ...                                                                | So that I can ...                                                              |
|---------|---------------|------------------------------------------------------------------------------|--------------------------------------------------------------------------------|
| v1.0    | TCG Collector | add/remove cards to my collection with their details (name, quantity, price) | maintain an accurate digital catalog of all my cards                           |
| v1.0    | TCG Collector | search for specific cards by name or set using text-based queries            | quickly locate cards in my collection without browsing through physical binders |
| v1.0    | TCG Collector | organise my cards by different categories (set, rarity, card type)           | browse my collection in a structured way that suits my needs                   |
| v1.0    | User          | edit any stored data                                                         | update/correct mistakes when I first add the card                              |
| v1.0    | TCG Collector | view a chronological log of cards I recently added or removed                | quickly see what’s changed in my collection                                    |
| v2.0    | User          | store my data even when I close the application                              | use the app without having to input my current cards again                     |
| v2.0    | TCG Collector | have a wishlist to track what cards I want to get                            | check them off the wishlist once I have them                                   |
| v2.0    | User          | undo my latest command                                                       | make quick rectification of errors made                                        |
| v2.0    | TCG Collector | view cards sorted by their details such as price                             | quickly see my most valuable cards                                             |

## Appendix: Non-Functional Requirements
- Should work on any [mainstream OS](#mainstream-os) as long as it has Java 17 or above installed
- Should be able to hold up to 1000 cards without a noticeable sluggishness in performance
- Should be reasonably easy for a fast typist to quickly enter the commands.

## Appendix: Glossary
### Mainstream OS
- Windows, Linux, Unix
### Reversible Commands
- `add`, `removeindex`,`removename`,`edit`

## Appendix: Instructions For Manual Testing
Given below are instructions to test the app manually.

### Adding a card
1. Test case: `add /n Pikachu EX /q 3 /p 195.50`  
   Expected: A new card is added into the list, with the corresponding name, quantity and price
2. Test case: `add /n Mewtwo /p 50.20`  
   Expected: No card added. Error details shows missing required flags.
3. Test case: `add /nCharizard/q1/p2.2`  
   Expected: No card added. Error details shows invalid add format.
4. Test case : `add /n Mewtwo /q -1 /p 5.50`  
   Expected: No card added. Error details shows quantity cannot be negative
5. Test case : `add /n Mewtwo /q 1 /p -5.50`  
   Expected: No card added. Error details shows price cannot be negative


### Undo a add/remove/edit
1. Prerequisites: A add/remove/edit command must be entered before this
2. Test case: `add /n Pikachu EX /q 3 /p 195.50` + `undo`  
   Expected: A new card is added and the same card is removed.
3. Test case: `add /n Mewtwo /q 3 /p 50.2` + `add /n Mewtwo /q 1 /p 50.2` + `undo`  
   Expected: A new card is added, quantity is increased then quantity is decreased back to original amount.
4. Test case: `undo` (without any prior [reversible command](#reversible-commands))  
   Expected: Nothing happens. Error details show Nothing to Undo.