# bryankuah's Project Portfolio Page

**CardCollector** is a lightweight command-line application for trading card enthusiasts to manage their collections quickly and efficiently. It allows users to track cards in their inventory and wishlist, search, compare, reorder, edit, and review collection history from the terminal.

Given below are my contributions to the project.

## Summary of Contributions

**Code contributed**: [RepoSense link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=bryankuah)

**Enhancements implemented**:

* **Implemented the `edit` command**  
  Allows the user to modify any field of an existing card using flag-based syntax.

* **Implemented the `compare` command**  
  Compares two cards and highlights similarities/differences. Works on both inventory and wishlist.

* **Implemented the `reorder` command**  
  Permanently reorders cards in the inventory or wishlist by multiple criteria (name, price, quantity, last added, last modified) with ascending/descending options.

* **Implemented the `find` command**  
  Advanced multi-field search with substring matching and range operators. Works on both inventory and wishlist.

* **Implemented wishlist support**  
  Added `wishlist` prefix to route commands to the separate wishlist list, plus the `wishlist acquired` command to move a card from wishlist to main inventory.

* **Implemented the `clear` command**  
  Clears all cards from the current list (inventory or wishlist). Action is reversible with `undo`.

**Testing**:
* Wrote unit tests and integration tests for all features listed above (`EditCommandTest`, `CompareCommandTest`, `ReorderCommandTest`, `FindCommandTest`, `AcquiredCommandTest`, `ClearCommandTest`, etc.).

### Contributions to the User Guide (Extracts)
* Wrote the full documentation (format, examples, and notes) for the `edit`, `compare`, `reorder`, `find`, `wishlist` (including `wishlist acquired`), and `clear` commands.
* Updated the overall command summary table and glossary where these features appear.

### Contributions to the Developer Guide (Extracts)
* Added implementation details, design considerations, and updated class/sequence diagrams for the `edit`, `compare`, `reorder`, `find`, `wishlist` routing, and `clear` features.

**Contributions to team-based tasks**:
* Reviewed and merged pull requests from team members.
* Helped coordinate work

**Review/mentoring contributions**:
* Provided feedback on PRs
