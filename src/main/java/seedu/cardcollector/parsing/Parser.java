package seedu.cardcollector.parsing;

import java.nio.file.Path;
import seedu.cardcollector.card.CardHistoryType;
import seedu.cardcollector.command.AddCommand;
import seedu.cardcollector.command.AcquiredCommand;
import seedu.cardcollector.command.AnalyticsCommand;
import seedu.cardcollector.command.Command;
import seedu.cardcollector.command.CompareCommand;
import seedu.cardcollector.command.DownloadCommand;
import seedu.cardcollector.command.EditCommand;
import seedu.cardcollector.command.ExitCommand;
import seedu.cardcollector.command.FindCommand;
import seedu.cardcollector.command.HistoryCommand;
import seedu.cardcollector.command.HelpCommand;
import seedu.cardcollector.command.ListCommand;
import seedu.cardcollector.command.RemoveCardByIndexCommand;
import seedu.cardcollector.command.RemoveCardByNameCommand;
import seedu.cardcollector.command.ReorderCommand;
import seedu.cardcollector.command.TagCommand;
import seedu.cardcollector.command.UndoCommand;
import seedu.cardcollector.command.UndoUploadCommand;
import seedu.cardcollector.command.UploadCommand;
import seedu.cardcollector.card.CardSortCriteria;
import seedu.cardcollector.exception.ParseBlankCommandException;
import seedu.cardcollector.exception.ParseInvalidArgumentException;
import seedu.cardcollector.exception.ParseUnknownCommandException;
import java.util.UUID;

public class Parser {
    private static final String REGEX_WHITESPACES = "\\s+";
    private static final String FLAG_NAME = "/n";
    private static final String FLAG_QUANTITY = "/q";
    private static final String FLAG_PRICE = "/p";
    private static final String FLAG_SET = "/s";
    private static final String FLAG_RARITY = "/r";
    private static final String FLAG_CONDITION = "/c";
    private static final String FLAG_LANGUAGE = "/l";
    private static final String FLAG_CARD_NUMBER = "/no";
    private static final String FLAG_ID = "/id";
    private static final String FLAG_TAG = "/t";
    private static final String FLAG_HELP = "/h";
    private static final String[] CARD_FIELD_FLAGS = {
        FLAG_NAME, FLAG_QUANTITY, FLAG_PRICE, FLAG_SET, FLAG_RARITY, FLAG_CONDITION, FLAG_LANGUAGE, FLAG_CARD_NUMBER,
        FLAG_TAG
    };
    private static final String[] ADD_FIELD_FLAGS = {
        FLAG_NAME, FLAG_QUANTITY, FLAG_PRICE, FLAG_SET, FLAG_RARITY, FLAG_CONDITION, FLAG_LANGUAGE, FLAG_CARD_NUMBER,
        FLAG_ID
    };

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
    private static final String KEYWORD_UNDO_COMMAND = "undo";
    private static final String KEYWORD_TAG_COMMAND = "tag";
    private static final String KEYWORD_FOLDER_COMMAND = "folder";
    private static final String KEYWORD_ANALYTICS_COMMAND = "analytics";
    private static final String KEYWORD_STATS_COMMAND = "stats";
    private static final String KEYWORD_HELP_COMMAND = "help";

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
        "find [/n NAME] [/p PRICE] [/q QUANTITY] [/s SET] [/r RARITY] [/c CONDITION] [/l LANGUAGE] "
                + "[/no CARD_NUMBER] [/t TAG]",
        "find /n Pikachu",
        "find /p 12.5",
        "find /n Pikachu /q 3",
        "find /s Base Set /r Rare",
        "find /t trade"
    };

    private static final String[] USAGE_ADD_COMMAND = {
        "add /n NAME /q QTY /p PRICE [/s SET] [/r RARITY] [/c CONDITION] [/l LANGUAGE] [/no CARD_NUMBER]",
        "add /n Pikachu /q 1 /p 5.5",
        "add /n Charizard /q 1 /p 99.99 /s Base Set /r Holo /c Near Mint /l English /no 4/102"
    };

    private static final String[] USAGE_TAG_COMMAND = {
        "tag add INDEX /t TAG",
        "tag remove INDEX /t TAG",
        "tag add 3 /t deck",
        "folder remove 2 /t trade"
    };

    private static final String[] USAGE_EDIT_COMMAND = {
        "edit INDEX [/n NAME] [/q QTY] [/p PRICE] [/s SET] [/r RARITY] [/c CONDITION] [/l LANGUAGE] [/no CARD_NUMBER]",
        "edit 1 /n Dragonite VMAX",
        "edit 2 /s Jungle /r Rare"
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

        if (commandKeyword.equals(KEYWORD_HELP_COMMAND)) {
            return handleHelp(arguments);
        }

        switch (commandKeyword) {
        case KEYWORD_HISTORY_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleHistory(arguments);
        case KEYWORD_ADD_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleAdd(arguments);
        case KEYWORD_REMOVE_INDEX_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleRemoveByIndex(arguments);
        case KEYWORD_REMOVE_NAME_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleRemoveByName(arguments);
        case KEYWORD_FIND_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleFind(arguments);
        case KEYWORD_LIST_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleList(arguments);
        case KEYWORD_EXIT_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleExit(arguments);
        case KEYWORD_EDIT_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleEdit(arguments);
        case KEYWORD_COMPARE_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleCompare(arguments);
        case KEYWORD_DOWNLOAD_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleDownload(arguments);
        case KEYWORD_UPLOAD_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleUpload(arguments);
        case KEYWORD_UNDO_UPLOAD_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleUndoUpload(arguments);
        case KEYWORD_ACQUIRED_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleAcquired(arguments);
        case KEYWORD_REORDER_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleReorder(arguments);
        case KEYWORD_UNDO_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleUndo(arguments);
        case KEYWORD_TAG_COMMAND:
        case KEYWORD_FOLDER_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleTag(arguments);
        case KEYWORD_ANALYTICS_COMMAND:
        case KEYWORD_STATS_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleAnalytics(arguments);
        case KEYWORD_HELP_COMMAND:
            return handleHelp(arguments);
        default:
            throw new ParseUnknownCommandException(commandKeyword);
        }
    }

    private Command handleHelp(String arguments) {
        if (arguments.isBlank()) {
            return HelpCommand.overview();
        }
        return HelpCommand.forQuery(arguments);
    }

    private boolean isInlineHelpRequest(String arguments) {
        return arguments.trim().equals(FLAG_HELP);
    }

    private Command handleAnalytics(String arguments) throws ParseInvalidArgumentException {
        if (!arguments.isBlank()) {
            throw new ParseInvalidArgumentException(
                    "analytics does not take any arguments",
                    new String[] {"analytics", "stats", "wishlist analytics"}
            );
        }
        return new AnalyticsCommand();
    }

    private Command handleAdd(String args) throws ParseInvalidArgumentException {
        assert args != null : "Arguements passed should not be null";

        if (!args.contains("/n") || !args.contains("/q") || !args.contains("/p")) {
            throw new ParseInvalidArgumentException(
                    "Missing required flags (/n /q /p)",
                    USAGE_ADD_COMMAND
            );
        }

        String name;
        int quantity;
        float price;
        String cardSet;
        String rarity;
        String condition;
        String language;
        String cardNumber;
        UUID uid = null;

        try {
            name = requireTextFlag(args, FLAG_NAME, ADD_FIELD_FLAGS);
            quantity = Integer.parseInt(requireTextFlag(args, FLAG_QUANTITY, ADD_FIELD_FLAGS));
            price = Float.parseFloat(requireTextFlag(args, FLAG_PRICE, ADD_FIELD_FLAGS));
            cardSet = optionalTextFlag(args, FLAG_SET, ADD_FIELD_FLAGS);
            rarity = optionalTextFlag(args, FLAG_RARITY, ADD_FIELD_FLAGS);
            condition = optionalTextFlag(args, FLAG_CONDITION, ADD_FIELD_FLAGS);
            language = optionalTextFlag(args, FLAG_LANGUAGE, ADD_FIELD_FLAGS);
            cardNumber = optionalTextFlag(args, FLAG_CARD_NUMBER, ADD_FIELD_FLAGS);
            String uidString = optionalTextFlag(args, FLAG_ID, ADD_FIELD_FLAGS);
            if (uidString != null) {
                uid = UUID.fromString(uidString);
            }
        } catch (NumberFormatException e) {
            throw new ParseInvalidArgumentException(
                    "Quantity must be an integer and price must be float",
                    USAGE_ADD_COMMAND
            );
        } catch (Exception e) {
            throw new ParseInvalidArgumentException(
                    "Invalid add format",
                    USAGE_ADD_COMMAND
            );
        }

        if (quantity < 0) {
            throw new ParseInvalidArgumentException(
                    "Quantity cannot be negative",
                    USAGE_ADD_COMMAND
            );
        }

        if (price < 0) {
            throw new ParseInvalidArgumentException(
                    "Price cannot be negative",
                    USAGE_ADD_COMMAND
            );
        }

        return new AddCommand(uid, name, quantity, price, cardSet, rarity, condition, language, cardNumber);
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
        String cardSet = null;
        String rarity = null;
        String condition = null;
        String language = null;
        String cardNumber = null;
        String tag = null;

        try {
            name = optionalTextFlag(arguments, FLAG_NAME, CARD_FIELD_FLAGS);
            String quantityText = optionalTextFlag(arguments, FLAG_QUANTITY, CARD_FIELD_FLAGS);
            if (quantityText != null) {
                quantity = Integer.parseInt(quantityText);
            }
            String priceText = optionalTextFlag(arguments, FLAG_PRICE, CARD_FIELD_FLAGS);
            if (priceText != null) {
                price = Float.parseFloat(priceText);
            }
            cardSet = optionalTextFlag(arguments, FLAG_SET, CARD_FIELD_FLAGS);
            rarity = optionalTextFlag(arguments, FLAG_RARITY, CARD_FIELD_FLAGS);
            condition = optionalTextFlag(arguments, FLAG_CONDITION, CARD_FIELD_FLAGS);
            language = optionalTextFlag(arguments, FLAG_LANGUAGE, CARD_FIELD_FLAGS);
            cardNumber = optionalTextFlag(arguments, FLAG_CARD_NUMBER, CARD_FIELD_FLAGS);
            tag = optionalTextFlag(arguments, FLAG_TAG, CARD_FIELD_FLAGS);
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

        if (name == null && price == null && quantity == null
                && cardSet == null && rarity == null && condition == null
                && language == null && cardNumber == null && tag == null) {
            throw new ParseInvalidArgumentException(
                    "At least one search field must be provided",
                    USAGE_FIND_COMMAND
            );
        }

        return new FindCommand(name, price, quantity, cardSet, rarity, condition, language, cardNumber, tag);
    }

    private Command handleList(String arguments) throws ParseInvalidArgumentException {
        if (arguments.isBlank()) {
            return new ListCommand();
        }

        String tag;
        try {
            tag = optionalTextFlag(arguments, FLAG_TAG, new String[] {FLAG_TAG});
        } catch (Exception e) {
            throw new ParseInvalidArgumentException(
                    "Invalid list format",
                    new String[] {"list", "list /t sealed"}
            );
        }

        if (tag == null) {
            throw new ParseInvalidArgumentException(
                    "list only supports optional /t TAG filtering",
                    new String[] {"list", "list /t sealed"}
            );
        }
        return new ListCommand(tag);
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
     * Argument matching is intentionally fuzzy for fast usage, see Disambiguator class for more info.
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

        try {
            CardHistoryType historyType = Disambiguator.disambiguate(
                    CardHistoryType.class, CardHistoryType::getKeyword, historyTypeString);

            return new HistoryCommand(historyType, maxDisplayCount, isDescending);
        } catch (IllegalArgumentException e) {
            throw new ParseInvalidArgumentException(
                    "Unknown history argument! " + e.getMessage(), USAGE_HISTORY_COMMAND);
        }
    }

    private Command handleEdit(String args) throws ParseInvalidArgumentException {
        if (args.isBlank()) {
            throw new ParseInvalidArgumentException(
                    "Index must be provided",
                    USAGE_EDIT_COMMAND
            );
        }

        String[] parts = args.trim().split(REGEX_WHITESPACES, 2);
        int index;
        try {
            index = Integer.parseInt(parts[0].trim()) - 1;
        } catch (NumberFormatException e) {
            throw new ParseInvalidArgumentException(
                    "Index must be a valid integer",
                    USAGE_EDIT_COMMAND
            );
        }

        String flagArgs = parts.length > 1 ? parts[1] : "";

        String name = null;
        Integer quantity = null;
        Float price = null;
        String cardSet = null;
        String rarity = null;
        String condition = null;
        String language = null;
        String cardNumber = null;

        if (!flagArgs.isBlank()) {
            try {
                name = optionalTextFlag(flagArgs, FLAG_NAME, CARD_FIELD_FLAGS);
                String quantityText = optionalTextFlag(flagArgs, FLAG_QUANTITY, CARD_FIELD_FLAGS);
                if (quantityText != null) {
                    quantity = Integer.parseInt(quantityText);
                }

                String priceText = optionalTextFlag(flagArgs, FLAG_PRICE, CARD_FIELD_FLAGS);
                if (priceText != null) {
                    price = Float.parseFloat(priceText);
                }

                cardSet = optionalTextFlag(flagArgs, FLAG_SET, CARD_FIELD_FLAGS);
                rarity = optionalTextFlag(flagArgs, FLAG_RARITY, CARD_FIELD_FLAGS);
                condition = optionalTextFlag(flagArgs, FLAG_CONDITION, CARD_FIELD_FLAGS);
                language = optionalTextFlag(flagArgs, FLAG_LANGUAGE, CARD_FIELD_FLAGS);
                cardNumber = optionalTextFlag(flagArgs, FLAG_CARD_NUMBER, CARD_FIELD_FLAGS);
            } catch (NumberFormatException e) {
                throw new ParseInvalidArgumentException(
                    "Quantity must be an integer and price must be float",
                    USAGE_EDIT_COMMAND
                );
            } catch (Exception e) {
                throw new ParseInvalidArgumentException(
                    "Invalid edit format",
                    USAGE_EDIT_COMMAND
                );
            }
        }

        if (quantity != null && quantity < 0) {
            throw new ParseInvalidArgumentException(
                    "Quantity cannot be negative",
                    USAGE_EDIT_COMMAND
            );
        }

        if (price != null && price < 0) {
            throw new ParseInvalidArgumentException(
                    "Price cannot be negative",
                    USAGE_EDIT_COMMAND
            );
        }

        if (name == null && quantity == null && price == null
                && cardSet == null && rarity == null && condition == null
                && language == null && cardNumber == null) {
            throw new ParseInvalidArgumentException(
                    "At least one field must be provided to edit",
                    USAGE_EDIT_COMMAND
            );
        }

        return new EditCommand(index, name, quantity, price, cardSet, rarity, condition, language, cardNumber);
    }

    private Command handleTag(String arguments) throws ParseInvalidArgumentException {
        if (arguments.isBlank()) {
            throw new ParseInvalidArgumentException("Missing tag action", USAGE_TAG_COMMAND);
        }

        String[] parts = arguments.trim().split(REGEX_WHITESPACES, 2);
        if (parts.length < 2) {
            throw new ParseInvalidArgumentException("Missing tag target", USAGE_TAG_COMMAND);
        }

        TagCommand.Operation operation;
        if ("add".startsWith(parts[0].toLowerCase())) {
            operation = TagCommand.Operation.ADD;
        } else if ("remove".startsWith(parts[0].toLowerCase())) {
            operation = TagCommand.Operation.REMOVE;
        } else {
            throw new ParseInvalidArgumentException("Unknown tag action", USAGE_TAG_COMMAND);
        }

        String targetArgs = parts[1].trim();
        int tagFlagIndex = indexOfFlag(targetArgs, FLAG_TAG);
        if (tagFlagIndex < 0) {
            throw new ParseInvalidArgumentException("Tag must be provided with /t", USAGE_TAG_COMMAND);
        }

        String indexText = targetArgs.substring(0, tagFlagIndex).trim();
        if (indexText.isBlank()) {
            throw new ParseInvalidArgumentException("Index must be provided", USAGE_TAG_COMMAND);
        }

        try {
            int index = Integer.parseInt(indexText) - 1;
            String tag = requireTextFlag(targetArgs, FLAG_TAG, new String[] {FLAG_TAG});
            return new TagCommand(operation, index, tag);
        } catch (NumberFormatException e) {
            throw new ParseInvalidArgumentException("Index must be a valid integer", USAGE_TAG_COMMAND);
        }
    }

    private static String requireTextFlag(String input, String flag, String[] knownFlags) {
        String value = optionalTextFlag(input, flag, knownFlags);
        if (value == null) {
            throw new IllegalArgumentException("Missing required flag value for " + flag);
        }
        return value;
    }

    private static String optionalTextFlag(String input, String flag, String[] knownFlags) {
        int flagIndex = indexOfFlag(input, flag);
        if (flagIndex < 0) {
            return null;
        }

        int valueStart = flagIndex + flag.length();
        int nextFlagIndex = input.length();
        for (String knownFlag : knownFlags) {
            if (knownFlag.equals(flag)) {
                continue;
            }
            int candidateIndex = indexOfFlag(input, knownFlag);
            if (candidateIndex > flagIndex && candidateIndex < nextFlagIndex) {
                nextFlagIndex = candidateIndex;
            }
        }

        String value = input.substring(valueStart, nextFlagIndex).trim();
        return value.isEmpty() ? null : value;
    }

    private static int indexOfFlag(String input, String flag) {
        int fromIndex = 0;
        while (fromIndex < input.length()) {
            int candidateIndex = input.indexOf(flag, fromIndex);
            if (candidateIndex < 0) {
                return -1;
            }

            boolean validPrefix = candidateIndex == 0 || Character.isWhitespace(input.charAt(candidateIndex - 1));
            int suffixIndex = candidateIndex + flag.length();
            boolean validSuffix = suffixIndex == input.length() || Character.isWhitespace(input.charAt(suffixIndex));

            if (validPrefix && validSuffix) {
                return candidateIndex;
            }

            fromIndex = candidateIndex + 1;
        }
        return -1;
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

    private Command handleUndo(String arguments) throws ParseInvalidArgumentException {
        if (!arguments.isBlank()) {
            throw new ParseInvalidArgumentException(
                    "undo does not take any arguments",
                    new String[]{"undo"}
            );
        }
        return new UndoCommand();
    }
}
