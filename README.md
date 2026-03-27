# CardCollector

CardCollector is a Java 17 CLI for managing a trading card inventory and a separate wishlist. It stores card name, quantity, price, and timestamp history for added, modified, and removed cards.

## Setup

1. Install Java 17.
2. Clone the repository.
3. Run the app:

```bash
./gradlew run
```

To run the test suite:

```bash
./gradlew test
```

## Storage

CardCollector saves its active data file automatically to `data/cardcollector.txt`.

You can also export and import full app data manually:

- `download /f backups/cardcollector.txt`
  Saves the current inventory and wishlist to the given file.
- `upload /f backups/cardcollector.txt`
  Loads inventory and wishlist from the given file into the current session.
- `undoupload`
  Restores the session data from before the last successful upload.

`upload` now shows a warning before replacing current data. After a successful upload, `undoupload` can restore the previous session state, then normal auto-save continues using the active storage file at `data/cardcollector.txt`.

## Commands

- `add /n NAME /q QTY /p PRICE`
  `NAME` can contain spaces, `QTY` must be an integer greater than or equal to 0, and `PRICE` must be a valid number.
- `edit INDEX [/n NAME] [/q QTY] [/p PRICE]`
- `list`
- `find [/n NAME] [/p PRICE] [/q QUANTITY]`
- `compare INDEX1 INDEX2`
- `removeindex INDEX`
- `removename NAME`
- `history [added | modified | removed | entire] [NUMBER | all] [ascending | descending]`
- `download /f FILE_PATH`
- `upload /f FILE_PATH`
- `undoupload`
- `bye`

Prefix a command with `wishlist ` to run list-specific commands on the wishlist instead of the main inventory, for example `wishlist add /n Umbreon /q 1 /p 42.0` or `wishlist history added`.

## Documentation

- User guide: [docs/UserGuide.md](/Users/raventang/Documents/school/Semester%206/NUS/CS2113/tp/docs/UserGuide.md)
- Developer guide: [docs/DeveloperGuide.md](/Users/raventang/Documents/school/Semester%206/NUS/CS2113/tp/docs/DeveloperGuide.md)
