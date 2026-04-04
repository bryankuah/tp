# WeiHeng2003's Project Portfolio Page

## Overview
CardCollector is a lightweight command-line application for trading card enthusiasts to manage their collections quickly and efficiently.

## Summary of contributions
### Code Contributed
- [RepoSense Report](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=weiheng2003&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=WeiHeng2003&tabRepo=AY2526S2-CS2113-T11-3%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code~other&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

### Enhancements implemented
- Improved `add` feature to merge cards with same name and price (and other metadata if available)
  - Justification: Appending duplicate cards to the end of the list makes it hard for the user to track.
- Added `undo` feature to undo the `add`/`remove`/`edit` commands
  - Justification: Allows user to make quick rectifications to mistakes added into the list, as opposed to having to retype the commands to edit the card
  - Difficulties faced: 
    - Having to break down the different cases of the command to undo. E.g. `add` has 2 cases, adding a new card and merging duplicates, requiring `wasMarged` boolean flag to distinguish. A normal undo to `add` will delete the new entry, while a undo to merged `add` requires a change in quantity.
    - `CommandContext` was originally constructed inside the loop, which meant that the `commandHistory` stack was reset on every command. To fix this, the stack was moved into `CardCollector` and passed in each iteration.
- Implemented different ways to remove a card from the list (`RemoveCardByIndex`,`RemoveCardByName`)
  - Justification: When items are shown in the list, sometimes it is more intuitive to remove by index rather than the name

### Contributions to the UG
- Documented the usage of `add` and `undo`

### Contributions to the DG
- Formatted the document with table of contents and hyperlinks to other sections
- Converted the PUML codes into diagrams and attached as SVGs
- Documented `add` and `undo` features and design, inclusive of Class and Sequence diagrams

### Contribution to Team-Based Tasks
- Setting up of GitHub team organization and repositary
- Releases of product
- Documenting the target user profile
