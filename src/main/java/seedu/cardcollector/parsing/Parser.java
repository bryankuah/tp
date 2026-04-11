package seedu.cardcollector.parsing;

import java.nio.file.Path;
import seedu.cardcollector.card.CardHistoryType;
import seedu.cardcollector.card.NumericFilter;
import seedu.cardcollector.command.AddCommand;
import seedu.cardcollector.command.AcquiredCommand;
import seedu.cardcollector.command.AnalyticsCommand;
import seedu.cardcollector.command.Command;
import seedu.cardcollector.command.CompareCommand;
import seedu.cardcollector.command.DownloadCommand;
import seedu.cardcollector.command.EditCommand;
import seedu.cardcollector.command.ExitCommand;
import seedu.cardcollector.command.FilterCommand;
import seedu.cardcollector.command.FindCommand;
import seedu.cardcollector.command.DuplicatesCommand;
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
import seedu.cardcollector.command.ClearCommand;
import seedu.cardcollector.card.CardSortCriteria;
import seedu.cardcollector.exception.ParseBlankCommandException;
import seedu.cardcollector.exception.ParseInvalidArgumentException;
import seedu.cardcollector.exception.ParseUnknownCommandException;
import seedu.cardcollector.util.Box;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

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
    private static final String FLAG_NOTE = "/nt";
    private static final String FLAG_ID = "/id";
    private static final String FLAG_TAG = "/t";
    private static final String FLAG_HELP = "/h";
    private static final String[] CARD_FIELD_FLAGS = {
        FLAG_NAME,
        FLAG_QUANTITY,
        FLAG_PRICE,
        FLAG_SET,
        FLAG_RARITY,
        FLAG_CONDITION,
        FLAG_LANGUAGE,
        FLAG_CARD_NUMBER,
        FLAG_NOTE,
        FLAG_TAG
    };

    private static final String[] ADD_FIELD_FLAGS = {
        FLAG_NAME,
        FLAG_QUANTITY,
        FLAG_PRICE,
        FLAG_SET,
        FLAG_RARITY,
        FLAG_CONDITION,
        FLAG_LANGUAGE,
        FLAG_CARD_NUMBER,
        FLAG_NOTE,
        FLAG_ID
    };

    private static final String KEYWORD_ADD_COMMAND = "add";
    private static final String KEYWORD_ACQUIRED_COMMAND = "acquired";
    private static final String KEYWORD_REMOVE_INDEX_COMMAND = "removeindex";
    private static final String KEYWORD_REMOVE_NAME_COMMAND = "removename";
    private static final String KEYWORD_FIND_COMMAND = "find";
    private static final String KEYWORD_DUPLICATES_COMMAND = "duplicates";
    private static final String KEYWORD_FILTER_COMMAND = "filter";
    private static final String KEYWORD_LIST_COMMAND = "list";
    private static final String KEYWORD_HISTORY_COMMAND = "history";
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
    private static final String KEYWORD_CLEAR_COMMAND = "clear";

    private static final String[] USAGE_REORDER_COMMAND = {
        "reorder CRITERIA [ascending | descending]" +
                System.lineSeparator() +
                "where CRITERIA = index | name | quantity | price | set | rarity | condition | language" +
                " | number | note | added | modified | removed",
        "reorder price",
        "wishlist reorder name descending"
    };

    private static final String[] USAGE_LIST_COMMAND = {
        "list [NUMBER | all] [CRITERIA] [ascending | descending]" +
                System.lineSeparator() +
                "where CRITERIA = index | name | quantity | price | set | rarity | condition | language" +
                " | number | note | added | modified | removed",
        "list",
        "list 50 quantity ascending"
    };

    private static final String[] USAGE_HISTORY_COMMAND = {
        "history [NUMBER | all] [added | modified | removed | entire] [ascending | descending]",
        "history",
        "history 50 added ascending"
    };

    private static final String[] USAGE_FIND_COMMAND = {
        "find [/n NAME] [/p PRICE] [/q QUANTITY] [/s SET] [/r RARITY] "
                + "[/c CONDITION] [/l LANGUAGE] [/no CARD_NUMBER] [/nt NOTE] [/t TAG]",
        "find /n pika",
        "find /p 5.99",
        "find /q >30",
        "find /p <3.0",
        "find /q >=5 /p <=10.5",
        "find /n charizard /q 2"
    };

    private static final String[] USAGE_CLEAR_COMMAND = {
        "clear",
        "wishlist clear"
    };

    private static final String[] USAGE_ADD_COMMAND = {
        "add /n NAME /q QTY /p PRICE [/s SET] [/r RARITY] [/c CONDITION] [/l LANGUAGE] [/no CARD_NUMBER] [/nt NOTE]",
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
        "edit INDEX [/n NAME] [/q QTY] [/p PRICE] [/s SET] [/r RARITY] [/c CONDITION] " +
                "[/l LANGUAGE] [/no CARD_NUMBER] [/nt NOTE]",
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
        case KEYWORD_CLEAR_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleClear(arguments);
        case KEYWORD_FILTER_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleFilter(arguments);
        case KEYWORD_LIST_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleList(arguments);
        case KEYWORD_DUPLICATES_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return new DuplicatesCommand();
        case KEYWORD_HISTORY_COMMAND:
            if (isInlineHelpRequest(arguments)) {
                return HelpCommand.forKeyword(commandKeyword);
            }
            return handleHistory(arguments);
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

        if (indexOfFlag(args, FLAG_NAME) < 0
                || indexOfFlag(args, FLAG_QUANTITY) < 0
                || indexOfFlag(args, FLAG_PRICE) < 0 ) {
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
        String note;
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
            note = optionalTextFlag(args, FLAG_NOTE, ADD_FIELD_FLAGS);
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

        return new AddCommand(uid, name, quantity, price, cardSet, rarity, condition, language, cardNumber, note);
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

    private Command handleClear(String arguments) throws ParseInvalidArgumentException {
        if (!arguments.isBlank()) {
            throw new ParseInvalidArgumentException(
                    "Clear command takes no arguments",
                    USAGE_CLEAR_COMMAND
            );
        }
        return new ClearCommand();
    }

    //@@author bryankuah
    private Command handleFind(String arguments) throws ParseInvalidArgumentException {
        if (arguments.isBlank()) {
            throw new ParseInvalidArgumentException(
                    "At least one search field must be provided",
                    USAGE_FIND_COMMAND
            );
        }

        String name = null;
        NumericFilter quantityFilter = null;
        NumericFilter priceFilter = null;
        String cardSet = null;
        String rarity = null;
        String condition = null;
        String language = null;
        String cardNumber = null;
        String note = null;
        String tag = null;

        try {
            name = optionalTextFlag(arguments, FLAG_NAME, CARD_FIELD_FLAGS);
            String quantityText = optionalTextFlag(arguments, FLAG_QUANTITY, CARD_FIELD_FLAGS);
            if (quantityText != null) {
                quantityFilter = NumericFilter.parse(quantityText);
            }
            String priceText = optionalTextFlag(arguments, FLAG_PRICE, CARD_FIELD_FLAGS);
            if (priceText != null) {
                priceFilter = NumericFilter.parse(priceText);
            }
            cardSet = optionalTextFlag(arguments, FLAG_SET, CARD_FIELD_FLAGS);
            rarity = optionalTextFlag(arguments, FLAG_RARITY, CARD_FIELD_FLAGS);
            condition = optionalTextFlag(arguments, FLAG_CONDITION, CARD_FIELD_FLAGS);
            language = optionalTextFlag(arguments, FLAG_LANGUAGE, CARD_FIELD_FLAGS);
            cardNumber = optionalTextFlag(arguments, FLAG_CARD_NUMBER, CARD_FIELD_FLAGS);
            note = optionalTextFlag(arguments, FLAG_NOTE, CARD_FIELD_FLAGS);
            tag = optionalTextFlag(arguments, FLAG_TAG, CARD_FIELD_FLAGS);
        } catch (NumberFormatException e) {
            throw new ParseInvalidArgumentException(
                    "Invalid number format for price or quantity (use e.g. >30 or <3.0)",
                    USAGE_FIND_COMMAND
            );
        } catch (Exception e) {
            throw new ParseInvalidArgumentException(
                    "Invalid find format",
                    USAGE_FIND_COMMAND
            );
        }

        if (name == null && quantityFilter == null && priceFilter == null
                && cardSet == null && rarity == null && condition == null
                && language == null && cardNumber == null
                && note == null && tag == null) {
            throw new ParseInvalidArgumentException(
                    "At least one search field must be provided",
                    USAGE_FIND_COMMAND
            );
        }

        return new FindCommand(name, quantityFilter, priceFilter,
                cardSet, rarity, condition, language, cardNumber, note, tag);
    }

    //@@author HX2003
    private Command handleList(String arguments) throws ParseInvalidArgumentException {
        SplitTokenizer tokenizer = new SplitTokenizer(REGEX_WHITESPACES);
        tokenizer.tokenize(arguments);

        if (tokenizer.getNumTokens() > 3) {
            throw new ParseInvalidArgumentException("Too many arguments", USAGE_LIST_COMMAND);
        }

        String maxDisplayCountString = tokenizer.getString(0);
        String sortCriteriaString = tokenizer.getString(1);
        String isDescendingString = tokenizer.getString(2);

        int maxDisplayCount = getMaxDisplayCount(maxDisplayCountString, USAGE_LIST_COMMAND);
        CardSortCriteria sortCriteria = getSortCriteria(sortCriteriaString, USAGE_LIST_COMMAND);
        boolean isDescending = getIsDescending(isDescendingString, false, USAGE_LIST_COMMAND);

        return new ListCommand(sortCriteria, maxDisplayCount, isDescending);
    }

    //@@author Simplificatedd
    private Command handleFilter(String arguments) throws ParseInvalidArgumentException {
        if (arguments.isBlank()) {
            return new FilterCommand();
        }

        String tag;
        try {
            tag = requireTextFlag(arguments, FLAG_TAG, new String[] {FLAG_TAG});
        } catch (Exception e) {
            throw new ParseInvalidArgumentException(
                    "Invalid filter format",
                    new String[] {"filter", "filter /t sealed"}
            );
        }
        
        return new FilterCommand(tag);
    }

    //@@author
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

    /**
     * Parses a string to obtain max display count.
     * The string should represent a positive integer or match with "all".
     * Argument matching for "all" is intentionally fuzzy for fast usage.
     * When "all" argument is simplified Integer.MAX_VALUE is returned.
     *
     * @param maxDisplayCountString The string to parse, may be null or blank.
     * @param usage The command usage information array, used for error messaging
     *              when an invalid argument is provided.
     * @return Returns max display count integer.
     * @throws ParseInvalidArgumentException If the input string is not null and not blank,
     *                                       but does not match prefix of "all"
     */
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
            if ("all".startsWith(maxDisplayCountString.toLowerCase())) {
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
     * @param defaultIsDescending The default value to return;
     * @param usage The command usage information array, used for error messaging
     *              when an invalid argument is provided.
     * @return Returns default value when input is null, blank
     *         return true if matching prefix of "descending".
     * @throws ParseInvalidArgumentException If the input string is not null and not blank,
     *                                       but the disambiguation fails.
     */
    private static boolean getIsDescending(String isDescendingString, boolean defaultIsDescending,
        String[] usage) throws ParseInvalidArgumentException {

        if (isDescendingString == null || isDescendingString.isBlank()) {
            return defaultIsDescending;
        }

        Map<String, Boolean> map = new HashMap<>() {{
                put("ascending", false);
                put("descending", true);
                }};

        try {
            return Disambiguator.disambiguate(map, isDescendingString);
        } catch (IllegalArgumentException e) {
            throw new ParseInvalidArgumentException(
                    "Unknown sorting direction! " + e.getMessage(), usage);
        }
    }

    /**
     * Parses a string to determine the history type.
     * Argument matching is intentionally fuzzy for fast usage.
     *
     * @param historyTypeString The string to parse, may be null or blank.
     * @return Returns the history type.
     * @throws ParseInvalidArgumentException If the input string is not null and not blank,
     *                                       but the disambiguation fails.
     */
    private static CardHistoryType getHistoryType(String historyTypeString)
            throws ParseInvalidArgumentException {
        if (historyTypeString == null) {
            return CardHistoryType.ENTIRE;
        }

        try {
            return Disambiguator.disambiguate(
                    CardHistoryType.class, CardHistoryType::getKeyword, historyTypeString);
        } catch (IllegalArgumentException e) {
            throw new ParseInvalidArgumentException(
                    "Unknown history type! " + e.getMessage(), USAGE_HISTORY_COMMAND);
        }
    }

    /**
     * Parses a string to determine the sort criteria.
     * Argument matching is intentionally fuzzy for fast usage.
     *
     * @param sortCriteriaString The string to parse, may be null or blank.
     * @param usage The command usage information array, used for error messaging
     *              when an invalid argument is provided.
     * @return Returns the sort criteria, default to INDEX if sortCriteriaString is null.
     * @throws ParseInvalidArgumentException If the input string is not null and not blank,
     *                                       but the disambiguation fails.
     */
    private static CardSortCriteria getSortCriteria(String sortCriteriaString, String[] usage)
            throws ParseInvalidArgumentException {
        if (sortCriteriaString == null) {
            return CardSortCriteria.INDEX;
        }

        try {
            return Disambiguator.disambiguate(CardSortCriteria.class,
                    CardSortCriteria::getKeyword, sortCriteriaString);
        } catch (IllegalArgumentException e) {
            throw new ParseInvalidArgumentException(
                    "Unknown sorting criteria! " + e.getMessage(), usage);
        }
    }

    /**
     * Handles the "history" command by displaying different types of inventory change history.
     * The argument format is [NUMBER | all] [added | modified | removed | entire] [ascending | descending]
     * All arguments are optional, but if provided, they must be in order.
     * Argument matching is intentionally fuzzy for fast usage, see Disambiguator class for more info.
     *
     * @param arguments The command argument that determines which history type to display.
     */
    private Command handleHistory(String arguments) throws ParseInvalidArgumentException {
        SplitTokenizer tokenizer = new SplitTokenizer(REGEX_WHITESPACES);
        tokenizer.tokenize(arguments);

        if (tokenizer.getNumTokens() > 3) {
            throw new ParseInvalidArgumentException("Too many arguments", USAGE_HISTORY_COMMAND);
        }

        String maxDisplayCountString = tokenizer.getString(0);
        String historyTypeString = tokenizer.getString(1);
        String isDescendingString = tokenizer.getString(2);

        int maxDisplayCount = getMaxDisplayCount(maxDisplayCountString, USAGE_HISTORY_COMMAND);
        CardHistoryType historyType = getHistoryType(historyTypeString);
        boolean isDescending = getIsDescending(isDescendingString, true, USAGE_HISTORY_COMMAND);


        return new HistoryCommand(historyType, maxDisplayCount, isDescending);
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

        Box<String> name = null;
        Box<Integer> quantity = null;
        Box<Float> price = null;
        Box<String> cardSet = null;
        Box<String> rarity = null;
        Box<String> condition = null;
        Box<String> language = null;
        Box<String> cardNumber = null;
        Box<String> note = null;

        if (!flagArgs.isBlank()) {
            try {
                name = optionalTextFlagBoxed(flagArgs, FLAG_NAME, CARD_FIELD_FLAGS);
                Box<String> quantityText = optionalTextFlagBoxed(flagArgs, FLAG_QUANTITY, CARD_FIELD_FLAGS);
                if (quantityText != null) {
                    quantity = Box.of(Integer.parseInt(quantityText.get()));
                }

                Box<String> priceText = optionalTextFlagBoxed(flagArgs, FLAG_PRICE, CARD_FIELD_FLAGS);
                if (priceText != null) {
                    price = Box.of(Float.parseFloat(priceText.get()));
                }

                cardSet = optionalTextFlagBoxed(flagArgs, FLAG_SET, CARD_FIELD_FLAGS);
                rarity = optionalTextFlagBoxed(flagArgs, FLAG_RARITY, CARD_FIELD_FLAGS);
                condition = optionalTextFlagBoxed(flagArgs, FLAG_CONDITION, CARD_FIELD_FLAGS);
                language = optionalTextFlagBoxed(flagArgs, FLAG_LANGUAGE, CARD_FIELD_FLAGS);
                cardNumber = optionalTextFlagBoxed(flagArgs, FLAG_CARD_NUMBER, CARD_FIELD_FLAGS);
                note = optionalTextFlagBoxed(flagArgs, FLAG_NOTE, CARD_FIELD_FLAGS);
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

        if (quantity != null && quantity.get() < 0) {
            throw new ParseInvalidArgumentException(
                    "Quantity cannot be negative",
                    USAGE_EDIT_COMMAND
            );
        }

        if (price != null && price.get() < 0) {
            throw new ParseInvalidArgumentException(
                    "Price cannot be negative",
                    USAGE_EDIT_COMMAND
            );
        }

        if (name == null && quantity == null && price == null
                && cardSet == null && rarity == null && condition == null
                && language == null && cardNumber == null && note == null) {
            throw new ParseInvalidArgumentException(
                    "At least one field must be provided to edit",
                    USAGE_EDIT_COMMAND
            );
        }

        return new EditCommand(index, name, quantity, price,
                cardSet, rarity, condition, language, cardNumber, note);
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

    /**
     * Returns the value of a text flag as a string, or {@code null}
     * if the flag is not present or is empty.
     * <p>
     * This method is a compatibility shim around {@link #optionalTextFlagBoxed}.
     *
     * @param input The string input.
     * @param flag The flag to detect.
     * @param knownFlags A list all the possible flags in the command.
     * @return A string, or {@code null} if the flag is not present or is empty.
     */
    private static String optionalTextFlag(String input, String flag, String[] knownFlags) {
        Box<String> string = optionalTextFlagBoxed(input, flag, knownFlags);

        return string == null ? null : string.get();
    }

    private static Box<String> optionalTextFlagBoxed(String input, String flag, String[] knownFlags) {
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
        return value.isEmpty() ? null : Box.of(value);
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

        SplitTokenizer tokenizer = new SplitTokenizer(REGEX_WHITESPACES);
        tokenizer.tokenize(arguments);

        if (tokenizer.getNumTokens() > 2) {
            throw new ParseInvalidArgumentException("Too many arguments", USAGE_REORDER_COMMAND);
        }

        String sortCriteriaString = tokenizer.getString(0);
        String isDescendingString = tokenizer.getString(1);

        CardSortCriteria sortCriteria = getSortCriteria(sortCriteriaString, USAGE_REORDER_COMMAND);
        boolean isDescending = getIsDescending(isDescendingString, false, USAGE_REORDER_COMMAND);


        return new ReorderCommand(sortCriteria, !isDescending);
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
