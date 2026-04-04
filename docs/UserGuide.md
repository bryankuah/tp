# User Guide

## Introduction

CardCollector is a CLI application for tracking a trading card inventory and a separate wishlist.
Each card stores a name, quantity, price, optional metadata (set, rarity, condition,
language, card number), and timestamps used by history commands.

## Quick Start

1. Ensure that Java 17 or above is installed.
2. Run `./gradlew run` from the project root.
3. Enter commands in the terminal.

## Features

### Getting help: `help` or `/h`

Lists all available commands in a compact reference view, or shows detailed syntax for a specific command.

**Format:** `help [COMMAND]`
**Format:** `COMMAND /h`

**Examples:**
`help`
`help add`
`find /h`

### Adding a card: `add`

Adds a new card to the current list.

**Format:** `add /n NAME /q QUANTITY /p PRICE [/s SET] [/r RARITY] [/c CONDITION] 
[/l LANGUAGE] [/no CARD_NUMBER] [/nt NOTE]`

- `NAME` can contain spaces.
- `QUANTITY` must be an integer greater than or equal to 0.
- `PRICE` must be a valid number.
- Rest of the metadata flags are optional.

**Example:** 
`add /n Pikachu VMAX /q 2 /p 25.50`
`add /n Charizard /q 1 /p 99.99 /s Base Set /r Holo /c Near Mint /l English /no 4/102`
`add /n Bulbasaur /q 3 /p 20.70 /s Base Set /r Holo /c Mint /l Japanese /no 1/102 /nt from trader jin`

### Editing a card: `edit`

Edits the name, quantity, price, or optional metadata of an existing card.

**Format:** `edit INDEX [/n NEW_NAME] [/q NEW_QUANTITY] [/p NEW_PRICE] [/s SET] [/r RARITY] [/c CONDITION] 
[/l LANGUAGE] [/no CARD_NUMBER] [/nt NOTE]`

**Examples:**
`edit 1 /n Dragonite VMAX`
`edit 2 /q 5 /p 12.99`
`edit 3 /s Jungle /r Rare`
`edit 3 /s Jungle /r Rare /nt Slight whitening on back`

### Comparing cards: `compare`

Compares two cards from the same list.

**Format:** `compare INDEX1 INDEX2`

**Examples:**
`compare 1 3`
`wishlist compare 2 4`

### Reordering the list: `reorder`

Permanently reorders the stored cards in your inventory or wishlist by the chosen criteria.

**Format:** `reorder CRITERIA [asc|desc]`

- CRITERIA = `name` | `price` | `quantity` | `lastadded` | `lastmodified`

**Examples:**
`reorder price desc`
`wishlist reorder name asc`

### Listing cards: `list`

Displays all cards in the current list in a sorted order.

**Format:** `list [NUMBER | all] [index | name | quantity | price | set | rarity | condition | language | number | added | modified | removed] [ascending | descending]`

**Examples:**
`list`
`list all`
`list 50 quantity ascending`

- By default, the displayed list is sorted by index in ascending order.
- Arguments are optional, but if specified, they must be in order.
- Argument matching is intentionally fuzzy for fast usage.
- Except for index, quantity and price, all other properties are treated as strings and thus are sorted in lexicographical order.
  - This includes `rarity` and `condition`, as they do not have any predefined order.
  - This includes card `number` (not to be confused with `index` or `quantity`). 


### Filtering cards: `filter`

Displays cards in the current list, optionally filtered by tag.

**Format:** `filter [/t TAG]`

**Examples:**
`filter`
`filter /t sealed`

- Use `filter /t TAG` to show only cards with that tag.
- Use `filter` with no tag to display the full list without applying a tag filter.
 

### Viewing analytics: `analytics` or `stats`

Displays a detailed summary of the current list, including total value, card rankings,
set insights, price distribution, and metadata coverage.

**Format:** `analytics`  
**Format:** `stats`

- Shows the number of distinct cards, total quantity, and total collection value.
- Computes averages such as quantity per card, value per card, and value per unit.
- Displays the collection tier (Starter / Mid / High value) and size (Small / Medium / Large).
- Lists the 3 most expensive cards by price.
- Lists the top cards by total holding value (price × quantity).
- Lists the cheapest cards by price.
- Displays the top sets by total quantity.
- Displays the top sets by total value.
- Groups cards into price ranges ($0, $0.01–$9.99, $10–$49.99, $50–$99.99, $100+).
- Shows metadata coverage, including how many cards contain notes or set information.
- Cards without a set are excluded from set-based analytics.

**Examples:**
`analytics`  
`stats`  
`wishlist analytics`

### Finding cards: `find`

**Format:**  
`find [/n NAME] [/p PRICE] [/q QUANTITY] [/s SET] [/r RARITY] [/c CONDITION] [/l LANGUAGE] [/no CARD_NUMBER] [/nt NOTE] [/t TAG]`

- `/p` and `/q` support **range operators**: `>`, `>=`, `<`, `<=`, or exact value.
- All other fields are **substring** (case-insensitive) matches.
- You can combine any number of flags.

**Examples:**
- `find /n charizard`
- `find /q 5` (exact quantity)
- `find /q >30`
- `find /p <3.0`
- `find /q >=5 /p <=10.5`
- `find /n pikachu /q >10 /p <1.5`
- `find /s "Base Set" /r rare`

### Tagging a card: `tag` or `folder`

Adds or removes an optional tag/folder label on an existing card. Tags are lightweight labels such as `deck`,
`sealed`, or `trade`, and you can use them later with `find /t ...` or `filter /t ...`.

**Format:** `tag add INDEX /t TAG`
**Format:** `tag remove INDEX /t TAG`

**Examples:**
`tag add 3 /t deck`
`folder remove 2 /t trade`

### Removing a card by index: `removeindex`

Removes a card by its displayed position.

**Format:** `removeindex INDEX`

**Example:** `removeindex 2`

### Removing a card by name: `removename`

Removes the first exact case-insensitive name match.

**Format:** `removename NAME`

**Example:** `removename Pikachu`

### Undoing the most recent change: `undo`

Undoes the most recent reversible add, remove, or edit action.

**Format:** `undo`

**Example:** `add /n Pikachu /q 1 /p 32.5` followed by `undo`

### Finding duplicate cards: `duplicates`

Displays cards in the current list that have duplicates.

**Format:** `duplicates`

**Examples:**
`duplicates`
`wishlist duplicates`

### Viewing history: `history`

Displays a historical audit log of when cards were added, modified, or removed.

**Format:** `history [NUMBER | all] [added | modified | removed | entire] [ascending | descending]`

- Arguments are optional, but if specified, they must be in order.
- Argument matching is intentionally fuzzy for fast usage.
- An 'added' entry occurs when a new or existing card is added, or when the edit command increases the quantity of the card.
- A 'modified' entry occurs when a card value is changed, **excluding** any changes to the quantity of the card.
- A 'removed' entry occurs when a card is removed, or when the edit command decreases the quantity of the card.
- The `undo` command does not revert the history, but rather adds to the history.
  An exception to this, is the undo of the `clear` command which restores the history.

**Examples:**
`history all`
`history all removed`
`history 50 added ascending`
`history 50 a a`

### Using the wishlist: `wishlist`

Prefix any list-based command with `wishlist ` to run it on the wishlist instead of the main inventory.

**Examples:**
`wishlist add /n Charizard /q 1 /p 99.99`
`wishlist list`
`wishlist edit 1 /n Shiny Charizard`
`wishlist removeindex 2`

### Acquiring a card from wishlist: `wishlist acquired`

Moves a card from the wishlist to your main inventory (and removes it from the wishlist).

**Format:** `wishlist acquired INDEX`

**Example:**
`wishlist acquired 3`

### Clearing

Clears **all** cards and their histories from the current list (inventory or wishlist).  
The action is **reversible** with `undo`.

**Format:**  
`clear`

**Alternative (explicit):**  
`wishlist clear`

**Examples:**
- `clear`
- `wishlist clear`

**Note:**
- This permanently deletes everything in the list **until** you type `undo`.
- The other list (inventory ↔ wishlist) is unaffected.

### Downloading a storage snapshot: `download`

Exports the current full app state, including inventory and wishlist, to a file path of your choice.

**Format:** `download /f FILE_PATH`

**Example:** `download /f backups/cardcollector.txt`

### Uploading a storage snapshot: `upload`

Imports a previously exported storage file into the current session.

**Format:** `upload /f FILE_PATH`

**Example:** `upload /f backups/cardcollector.txt`

`upload` warns before replacing the current in-memory inventory and wishlist. After a successful upload, you can use `undoupload` once to restore the previous session state. The app continues to auto-save to `data/cardcollector.txt`.

### Undoing the last upload: `undoupload`

Restores the inventory and wishlist from before the last successful upload.

**Format:** `undoupload`

### Exiting the program: `bye`

Exits the application.

**Format:** `bye`

## FAQ

**Q**: How do I transfer my data to another computer?

**A**: Run `download /f some-file.txt`, move that file to the other computer, then run `upload /f some-file.txt` there.

## Command Summary

| Command                        | Description                          |
|--------------------------------|--------------------------------------|
| `add /n NAME /q QTY /p PRICE`  | Add card                             |
| `edit INDEX [...]`             | Edit card                            |
| `compare INDEX1 INDEX2`        | Compare cards                        |
| `reorder CRITERIA [asc\|desc]` | Reorder list                         |
| `removeindex INDEX`            | Remove by index                      |
| `removename NAME`              | Remove by name                       |
| `undo`                         | Undo the most recent add/remove/edit |
| `duplicates`                   | Show duplicate cards                 |
| `tag add INDEX /t TAG`         | Add tag/folder                       |
| `tag remove INDEX /t TAG`      | Remove tag/folder                    |
| `wishlist acquired INDEX`      | Move to inventory                    |
| `list [...]`                   | List cards                           |
| `filter [/t TAG]`              | Filter cards                         |
| `analytics` / `stats`          | Show list insights                   |
| `help [COMMAND]`               | Show command help                    |
| `download /f FILE_PATH`        | Export data                          |
| `upload /f FILE_PATH`          | Import data                          |
| `undoupload`                   | Undo upload                          |
| `history [...]`                | View history                         |
| `wishlist <command>`           | Use wishlist                         |
| `find [...]`                   | Search cards                         |
| `clear`                        | Clear all cards and their histories  |
| `bye`                          | Exit app                             |
