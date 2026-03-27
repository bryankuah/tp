package seedu.cardcollector.parsing;

import java.nio.file.Path;
import seedu.cardcollector.card.CardHistoryType;
import seedu.cardcollector.command.AddCommand;
import seedu.cardcollector.command.AcquiredCommand;
import seedu.cardcollector.command.Command;
import seedu.cardcollector.command.EditCommand;
import seedu.cardcollector.command.CompareCommand;
import seedu.cardcollector.command.DownloadCommand;
import seedu.cardcollector.command.ExitCommand;
import seedu.cardcollector.command.FindCommand;
import seedu.cardcollector.command.HistoryCommand;
import seedu.cardcollector.command.ListCommand;
import seedu.cardcollector.command.RemoveCardByIndexCommand;
import seedu.cardcollector.command.RemoveCardByNameCommand;
import seedu.cardcollector.command.UndoUploadCommand;
import seedu.cardcollector.command.UploadCommand;
import seedu.cardcollector.card.CardSortCriteria;
import seedu.cardcollector.command.ReorderCommand;
import seedu.cardcollector.exception.ParseBlankCommandException;
import seedu.cardcollector.exception.ParseInvalidArgumentException;
import seedu.cardcollector.exception.ParseUnknownCommandException;
import java.util.UUID;

public class Parser {
    private static final String REGEX_WHITESPACES = "\\s+";

    private static final String KEYWORD_HISTORY_COMMAND = "history";
    private static final String KEYWORD_ADD_COMMAND = "add";
    private static final String KEYWORD_ACQUIRED_COMMAND = "acquired";
    private static final String KEYWORD_REMOVE_INDEX_COMMAND = "removeindex";
    private static final String KEYWORD_REMOVE_NAME_COMMAND = "removename";
    private static final String KEYWORD_FIND_COMMAND = "find";
    private static final String KEYWORD_LIST_COMMAND = "list";
    private static final String KEYWORD_EXIT_COMMAND = "bye";
    private static final String KEYWORD_EDIT_COMMAND = "edit";
    private static final String KEYWORD_COMPARE_COMMAND = "compare";
    private static final String KEYWORD_DOWNLOAD_COMMAND = "download";
    private static final String KEYWORD_UPLOAD_COMMAND = "upload";
    private static final String KEYWORD_UNDO_UPLOAD_COMMAND = "undoupload";
    private static final String KEYWORD_REORDER_COMMAND = "reorder";

    private static final String[] USAGE_REORDER_COMMAND = {
        "reorder CRITERIA [asc|desc]",
        "reorder price desc",
        "wishlist reorder name asc"
    };

    private static final String[] USAGE_HISTORY_COMMAND = {
        "history [added | modified | removed | entire] [NUMBER | all] [ascending | descending]",
        "history",
        "history added 50 ascending"
    };

    private static final String[] USAGE_FIND_COMMAND = {
        "find [/n NAME] [/p PRICE] [/q QUANTITY]",
        "find /n Pikachu",
        "find /p 12.5",
        "find /n Pikachu /q 3"
    };

    private static final String[] USAGE_TRANSFER_COMMAND = {
        "%s /f FILE_PATH",
        "download /f backups/cardcollector.txt",
        "upload /f backups/cardcollector.txt"
    };

    public Command parse(String input) throws
            ParseBlankCommandException,
            ParseUnknownCommandException,
            ParseInvalidArgumentException {
        String trimmedInput = input.trim();

        if (trimmedInput.isEmpty()) {
            throw new ParseBlankCommandException();
        }

        String[] parts = trimmedInput.split(REGEX_WHITESPACES, 2);
        String commandKeyword = parts[0].toLowerCase();
        String arguments = parts.length > 1 ? parts[1] : "";

        switch (commandKeyword) {
        case KEYWORD_HISTORY_COMMAND:
            return handleHistory(arguments);
        case KEYWORD_ADD_COMMAND:
            return handleAdd(arguments);
        case KEYWORD_REMOVE_INDEX_COMMAND:
            return handleRemoveByIndex(arguments);
        case KEYWORD_REMOVE_NAME_COMMAND:
            return handleRemoveByName(arguments);
        case KEYWORD_FIND_COMMAND:
            return handleFind(arguments);
        case KEYWORD_LIST_COMMAND:
            return handleList(arguments);
        case KEYWORD_EXIT_COMMAND:
            return handleExit(arguments);
        case KEYWORD_EDIT_COMMAND:
            return handleEdit(arguments);
        case KEYWORD_COMPARE_COMMAND:
            return handleCompare(arguments);
        case KEYWORD_DOWNLOAD_COMMAND:
            return handleDownload(arguments);
        case KEYWORD_UPLOAD_COMMAND:
            return handleUpload(arguments);
        case KEYWORD_UNDO_UPLOAD_COMMAND:
            return handleUndoUpload(arguments);
        case KEYWORD_ACQUIRED_COMMAND:
            return handleAcquired(arguments);
        case KEYWORD_REORDER_COMMAND:
            return handleReorder(arguments);
        default:
            throw new ParseUnknownCommandException(commandKeyword);
        }
    }

    private Command handleAdd(String args) throws ParseInvalidArgumentException {
        assert args != null : "Arguements passed should not be null";

        if (!args.contains("/n") || !args.contains("/q") || !args.contains("/p")) {
            throw new ParseInvalidArgumentException(
                    "Missing required flags (/n /q /p)",
                    new String[]{"add /n NAME /q QTY /p PRICE"}
            );
        }

        try {
            String name = args.split("/n")[1].split("/q|/p|/id")[0].trim();
            int quantity = Integer.parseInt(args.split("/q")[1].split("/n|/p|/id")[0].trim());
            float price = Float.parseFloat(args.split("/p")[1].split("/n|/q|/id")[0].trim());

            UUID uid = null;
            if (args.contains("/id")) {
                String uidString = args.split("/id")[1].split("/n|/q|/p")[0].trim();
                uid = UUID.fromString(uidString);
            }
            return new AddCommand(uid, name, quantity, price);
        } catch (NumberFormatException e) {
            throw new ParseInvalidArgumentException(
                    "Quantity must be an integer and price must be float",
                    new String[]{"add /n NAME /q QTY /p PRICE"}
            );
        } catch (Exception e) {
            throw new ParseInvalidArgumentException(
                    "Invalid add format",
                    new String[]{"add /n NAME /q QTY /p PRICE [/id UUID]"}
            );
        }
    }

    private Command handleRemoveByIndex(String args) throws ParseInvalidArgumentException {
        try {
            int index = Integer.parseInt(args.trim()) - 1;
            return new RemoveCardByIndexCommand(index);
        } catch (NumberFormatException e) {
            throw new ParseInvalidArgumentException(
                    "Index must be a valid integer",
                    new String[] {"removeindex INDEX"}
            );
        }
    }

    private Command handleRemoveByName(String args) throws ParseInvalidArgumentException {
        if (args.isBlank()) {
            throw new ParseInvalidArgumentException(
                    "Name must be provided",
                    new String[] {"removename NAME"}
            );
        }
        return new RemoveCardByNameCommand(args.trim());
    }

    private Command handleFind(String arguments) throws ParseInvalidArgumentException {
        if (arguments.isBlank()) {
            throw new ParseInvalidArgumentException(
                    "At least one search field must be provided",
                    USAGE_FIND_COMMAND
            );
        }

        String name = null;
        Float price = null;
        Integer quantity = null;

        try {
            if (arguments.contains("/n")) {
                name = arguments.split("/n")[1].split("/q|/p")[0].trim();
                if (name.isEmpty()) {
                    name = null;
                }
            }
            if (arguments.contains("/q")) {
                quantity = Integer.parseInt(arguments.split("/q")[1].split("/n|/p")[0].trim());
            }
            if (arguments.contains("/p")) {
                price = Float.parseFloat(arguments.split("/p")[1].split("/n|/q")[0].trim());
            }
        } catch (NumberFormatException e) {
            throw new ParseInvalidArgumentException(
                    "Invalid number format for price or quantity",
                    USAGE_FIND_COMMAND
            );
        } catch (Exception e) {
            throw new ParseInvalidArgumentException(
                    "Invalid find format",
                    USAGE_FIND_COMMAND
            );
        }

        if (name == null && price == null && quantity == null) {
            throw new ParseInvalidArgumentException(
                    "At least one search field (/n, /p, /q) must be provided",
                    USAGE_FIND_COMMAND
            );
        }

        return new FindCommand(name, price, quantity);
    }

    private Command handleList(String arguments) throws ParseInvalidArgumentException {
        if (!arguments.isBlank()) {
            throw new ParseInvalidArgumentException(
                    "list does not take any arguments",
                    new String[] {"list"}
            );
        }
        return new ListCommand();
    }

    private Command handleExit(String arguments) throws ParseInvalidArgumentException {
        if (!arguments.isBlank()) {
            throw new ParseInvalidArgumentException(
                    "bye does not take any arguments",
                    new String[] {"bye"}
            );
        }
        return new ExitCommand();
    }

    private Command handleDownload(String arguments) throws ParseInvalidArgumentException {
        return new DownloadCommand(parseTransferPath(arguments, KEYWORD_DOWNLOAD_COMMAND));
    }

    private Command handleUpload(String arguments) throws ParseInvalidArgumentException {
        return new UploadCommand(parseTransferPath(arguments, KEYWORD_UPLOAD_COMMAND));
    }

    private Command handleUndoUpload(String arguments) throws ParseInvalidArgumentException {
        if (!arguments.isBlank()) {
            throw new ParseInvalidArgumentException(
                    "undoupload does not take any arguments",
                    new String[] {"undoupload"}
            );
        }
        return new UndoUploadCommand();
    }

    private Path parseTransferPath(String arguments, String commandWord) throws ParseInvalidArgumentException {
        if (arguments.isBlank() || !arguments.contains("/f")) {
            throw new ParseInvalidArgumentException(
                    "File path must be provided with /f",
                    getTransferUsage(commandWord)
            );
        }

        String path = arguments.substring(arguments.indexOf("/f") + 2).trim();
        if (path.isEmpty()) {
            throw new ParseInvalidArgumentException(
                    "File path cannot be blank",
                    getTransferUsage(commandWord)
            );
        }

        return Path.of(path);
    }

    private String[] getTransferUsage(String commandWord) {
        return new String[] {
            String.format(USAGE_TRANSFER_COMMAND[0], commandWord),
            USAGE_TRANSFER_COMMAND[1],
            USAGE_TRANSFER_COMMAND[2]
        };
    }


    private static int getMaxDisplayCount(String maxDisplayCountString, String[] usage)
            throws ParseInvalidArgumentException {
        int maxDisplayCount = -1;

        if (maxDisplayCountString == null || maxDisplayCountString.isBlank()) {
            return maxDisplayCount;
        }

        try {
            int i = Integer.parseInt(maxDisplayCountString);

            if (i < 1) {
                throw new ParseInvalidArgumentException("Display count must be at least 1", usage);
            }

            maxDisplayCount = i;
        } catch (NumberFormatException e) {
            if ("all".startsWith(maxDisplayCountString)) {
                maxDisplayCount = Integer.MAX_VALUE;
            } else {
                throw new ParseInvalidArgumentException("Display count invalid", usage);
            }
        }

        return maxDisplayCount;
    }

    /**
     * Parses a string to determine if sorting should be in descending order.
     * Argument matching is intentionally fuzzy for fast usage.
     *
     * @param isDescendingString The string to parse, may be null or blank.
     * @param usage The command usage information array, used for error messaging
     *              when an invalid argument is provided.
     * @return Returns true when input is null, blank, or match prefix of "descending".
     * @throws ParseInvalidArgumentException If the input string is not blank,
     *                                       but does not match prefix of "descending" or "ascending"
     */
    private static boolean getIsDescending(String isDescendingString, String[] usage)
            throws ParseInvalidArgumentException {
        boolean isDescending = true;

        if (isDescendingString == null || isDescendingString.isBlank()) {
            return isDescending;
        }

        if ("descending".startsWith(isDescendingString)) {
            isDescending = true;
        } else if ("ascending".startsWith(isDescendingString)) {
            isDescending = false;
        } else {
            throw new ParseInvalidArgumentException("Sorting order not recognized!", usage);
        }

        return isDescending;
    }


    /**
     * Handles the "history" command by displaying different types of inventory change history.
     * The argument format is [added | modified | removed | entire] [NUMBER | all] [ascending | descending]
     * All arguments are optional, but if provided, they must be in order.
     * Argument matching is intentionally fuzzy for fast usage.
     * For example, input starting with "a" will
     * match "added", "m" will match "modified", and "r" will match "removed".
     *
     * @param arguments The command argument that determines which history type to display.
     */
    private Command handleHistory(String arguments) throws ParseInvalidArgumentException {
        if (arguments.isBlank()) {
            return new HistoryCommand(CardHistoryType.ENTIRE);
        }

        SplitTokenizer tokenizer = new SplitTokenizer(REGEX_WHITESPACES);
        tokenizer.tokenize(arguments);

        String historyTypeString = tokenizer.getString(0);
        String maxDisplayCountString = tokenizer.getString(1);
        String isDescendingString = tokenizer.getString(2);

        int maxDisplayCount = getMaxDisplayCount(maxDisplayCountString, USAGE_HISTORY_COMMAND);
        boolean isDescending = getIsDescending(isDescendingString, USAGE_HISTORY_COMMAND);

        if (CardHistoryType.ADDED.getName().startsWith(historyTypeString)) {
            return new HistoryCommand(CardHistoryType.ADDED, maxDisplayCount, isDescending);
        } else if (CardHistoryType.MODIFIED.getName().startsWith(historyTypeString)) {
            return new HistoryCommand(CardHistoryType.MODIFIED, maxDisplayCount, isDescending);
        } else if (CardHistoryType.REMOVED.getName().startsWith(historyTypeString)) {
            return new HistoryCommand(CardHistoryType.REMOVED, maxDisplayCount, isDescending);
        }  else if (CardHistoryType.ENTIRE.getName().startsWith(historyTypeString)) {
            return new HistoryCommand(CardHistoryType.ENTIRE, maxDisplayCount, isDescending);
        }   else {
            throw new ParseInvalidArgumentException("Unknown history argument!", USAGE_HISTORY_COMMAND);
        }
    }

    private Command handleEdit(String args) throws ParseInvalidArgumentException {
        if (args.isBlank()) {
            throw new ParseInvalidArgumentException(
                    "Index must be provided",
                    new String[]{"edit INDEX [/n NAME] [/q QTY] [/p PRICE]"}
            );
        }

        String[] parts = args.trim().split(REGEX_WHITESPACES, 2);
        int index;
        try {
            index = Integer.parseInt(parts[0].trim()) - 1;
        } catch (NumberFormatException e) {
            throw new ParseInvalidArgumentException(
                    "Index must be a valid integer",
                    new String[]{"edit INDEX [/n NAME] [/q QTY] [/p PRICE]"}
            );
        }

        String flagArgs = parts.length > 1 ? parts[1] : "";

        String name = null;
        Integer quantity = null;
        Float price = null;

        if (!flagArgs.isBlank()) {
            try {
                if (flagArgs.contains("/n")) {
                    name = flagArgs.split("/n")[1].split("/q|/p")[0].trim();
                    if (name.isEmpty()) {
                        name = null;
                    }
                }
                if (flagArgs.contains("/q")) {
                    quantity = Integer.parseInt(flagArgs.split("/q")[1].split("/n|/p")[0].trim());
                }
                if (flagArgs.contains("/p")) {
                    price = Float.parseFloat(flagArgs.split("/p")[1].split("/n|/q")[0].trim());
                }
            } catch (NumberFormatException e) {
                throw new ParseInvalidArgumentException(
                        "Quantity must be an integer and price must be float",
                        new String[]{"edit INDEX [/n NAME] [/q QTY] [/p PRICE]"}
                );
            } catch (Exception e) {
                throw new ParseInvalidArgumentException(
                        "Invalid edit format",
                        new String[]{"edit INDEX [/n NAME] [/q QTY] [/p PRICE]"}
                );
            }
        }

        if (name == null && quantity == null && price == null) {
            throw new ParseInvalidArgumentException(
                    "At least one field (/n, /q or /p) must be provided to edit",
                    new String[]{"edit INDEX [/n NAME] [/q QTY] [/p PRICE]"}
            );
        }

        return new EditCommand(index, name, quantity, price);
    }

    private Command handleCompare(String args) throws ParseInvalidArgumentException {
        if (args.isBlank()) {
            throw new ParseInvalidArgumentException(
                    "Two indices must be provided",
                    new String[]{"compare INDEX1 INDEX2"}
            );
        }

        String[] parts = args.trim().split(REGEX_WHITESPACES);
        if (parts.length != 2) {
            throw new ParseInvalidArgumentException(
                    "Compare takes exactly two indices",
                    new String[]{"compare INDEX1 INDEX2"}
            );
        }

        int i1;
        int i2;
        try {
            i1 = Integer.parseInt(parts[0].trim()) - 1;
            i2 = Integer.parseInt(parts[1].trim()) - 1;
        } catch (NumberFormatException e) {
            throw new ParseInvalidArgumentException(
                    "Indices must be valid integers",
                    new String[]{"compare INDEX1 INDEX2"}
            );
        }

        return new CompareCommand(i1, i2);
    }

    private Command handleAcquired(String args) throws ParseInvalidArgumentException {
        if (args.isBlank()) {
            throw new ParseInvalidArgumentException(
                    "Index must be provided",
                    new String[]{"acquired INDEX"}
            );
        }

        try {
            int index = Integer.parseInt(args.trim()) - 1;
            return new AcquiredCommand(index);
        } catch (NumberFormatException e) {
            throw new ParseInvalidArgumentException(
                    "Index must be a valid integer",
                    new String[]{"acquired INDEX"}
            );
        }
    }

    private Command handleReorder(String arguments) throws ParseInvalidArgumentException {
        if (arguments.isBlank()) {
            throw new ParseInvalidArgumentException(
                    "CRITERIA must be provided for reorder",
                    USAGE_REORDER_COMMAND);
        }

        String[] parts = arguments.trim().split(REGEX_WHITESPACES);
        String criteriaStr = parts[0].toLowerCase();

        CardSortCriteria criteria;
        switch (criteriaStr) {
        case "name":
            criteria = CardSortCriteria.NAME;
            break;
        case "price":
            criteria = CardSortCriteria.PRICE;
            break;
        case "quantity":
            criteria = CardSortCriteria.QUANTITY;
            break;
        case "lastadded":
            criteria = CardSortCriteria.LAST_ADDED;
            break;
        case "lastmodified":
            criteria = CardSortCriteria.LAST_MODIFIED;
            break;
        default:
            throw new ParseInvalidArgumentException(
                    "Invalid criteria. Valid options: name, price, quantity, lastadded, lastmodified",
                    USAGE_REORDER_COMMAND);
        }

        boolean isAscending = true; // default = ascending
        if (parts.length > 1) {
            String orderStr = parts[1].toLowerCase();
            if (orderStr.equals("asc") || orderStr.equals("ascending")) {
                isAscending = true;
            } else if (orderStr.equals("desc") || orderStr.equals("descending")) {
                isAscending = false;
            } else {
                throw new ParseInvalidArgumentException(
                        "Invalid order. Use 'asc' or 'desc'",
                        USAGE_REORDER_COMMAND);
            }
        }

        if (parts.length > 2) {
            throw new ParseInvalidArgumentException(
                    "Too many arguments for reorder command",
                    USAGE_REORDER_COMMAND);
        }

        return new ReorderCommand(criteria, isAscending);
    }
}
