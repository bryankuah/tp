package seedu.cardcollector.command;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HelpCommand extends Command {
    private enum Mode {
        OVERVIEW,
        DETAIL,
        SEARCH,
        NO_RESULTS
    }

    private static final List<HelpTopic> TOPICS = List.of(
            new HelpTopic("add", List.of(), "add a new card to the current list", new String[] {
                "add /n NAME /q QTY /p PRICE [/s SET] [/r RARITY] [/c CONDITION] [/l LANGUAGE] [/no CARD_NUMBER]",
                "add /n Pikachu /q 1 /p 5.5",
                "wishlist add /n Charizard /q 1 /p 99.99 /s Base Set /r Holo /c Near Mint /l English /no 4/102"
            }),
            new HelpTopic("edit", List.of(), "edit fields on an existing card", new String[] {
                "edit INDEX [/n NAME] [/q QTY] [/p PRICE] [/s SET] [/r RARITY] "
                        + "[/c CONDITION] [/l LANGUAGE] [/no CARD_NUMBER]",
                "edit 1 /n Dragonite VMAX",
                "wishlist edit 2 /s Jungle /r Rare"
            }),
            new HelpTopic("compare", List.of(), "compare two cards in the same list", new String[] {
                "compare INDEX1 INDEX2",
                "compare 1 3",
                "wishlist compare 2 4"
            }),
            new HelpTopic("reorder", List.of(), "reorder the current list by a chosen field", new String[] {
                "reorder CRITERIA [ascending | descending]" +
                        System.lineSeparator() +
                        "where CRITERIA = index | name | quantity | price | set | rarity | condition | language" +
                        " | number | note | added | modified | removed",
                "reorder price",
                "wishlist reorder name descending"
            }),
            new HelpTopic("list", List.of(), "list cards in a sorted order", new String[] {
                "list [NUMBER | all] [CRITERIA] [ascending | descending]" +
                        System.lineSeparator() +
                        "where CRITERIA = index | name | quantity | price | set | rarity | condition | language" +
                        " | number | note | added | modified | removed",
                "list",
                "list 50 quantity ascending"
            }),
            new HelpTopic("filter", List.of(), "filter cards by tag", new String[] {
                "filter [/t TAG]",
                "wishlist filter /t sealed"
            }),
            new HelpTopic("analytics", List.of("stats"), "show summary analytics for the current list", new String[] {
                "analytics",
                "stats",
                "wishlist analytics"
            }),
            new HelpTopic("find", List.of(), "search cards by fields or tags", new String[] {
                "find [/n NAME] [/p PRICE] [/q QUANTITY] [/s SET] [/r RARITY] [/c CONDITION] [/l LANGUAGE] "
                        + "[/no CARD_NUMBER] [/t TAG]",
                "find /n Pikachu /q 3",
                "wishlist find /t trade"
            }),
            new HelpTopic("tag", List.of("folder"), "add or remove a tag or folder label", new String[] {
                "tag add INDEX /t TAG",
                "tag remove INDEX /t TAG",
                "folder remove 2 /t trade"
            }),
            new HelpTopic("removeindex", List.of(), "remove a card by its displayed index", new String[] {
                "removeindex INDEX",
                "removeindex 2",
                "wishlist removeindex 1"
            }),
            new HelpTopic("removename", List.of(), "remove the first exact case-insensitive name match", new String[] {
                "removename NAME",
                "removename Pikachu",
                "wishlist removename Charizard"
            }),
            new HelpTopic("history", List.of(), "show added, modified, or removed history entries", new String[] {
                "history [NUMBER | all] [added | modified | removed | entire] [ascending | descending]",
                "history",
                "history 50 added ascending"
            }),
            new HelpTopic("wishlist", List.of(), "run a list-based command against the wishlist instead of inventory",
                    new String[] {
                        "wishlist COMMAND",
                        "wishlist list",
                        "wishlist add /n Charizard /q 1 /p 99.99"
                    }),
            new HelpTopic("acquired", List.of(), "move a card from wishlist into inventory", new String[] {
                "wishlist acquired INDEX",
                "wishlist acquired 3"
            }),
            new HelpTopic("download", List.of(), "export the full app state to a file", new String[] {
                "download /f FILE_PATH",
                "download /f backups/cardcollector.txt"
            }),
            new HelpTopic("upload", List.of(), "import a previously exported app state file", new String[] {
                "upload /f FILE_PATH",
                "upload /f backups/cardcollector.txt"
            }),
            new HelpTopic("undoupload", List.of(), "restore the state from before the last upload", new String[] {
                "undoupload"
            }),
            new HelpTopic("undo", List.of(), "undo the last reversible command", new String[] {
                "undo"
            }),
            new HelpTopic("bye", List.of(), "exit the application", new String[] {
                "bye"
            }),
            new HelpTopic("help", List.of(), "list commands or show detailed command syntax", new String[] {
                "help [COMMAND]",
                "help add",
                "find /h"
            })
    );

    private static final Map<String, HelpTopic> TOPICS_BY_KEYWORD = buildTopicsByKeyword();

    private final Mode mode;
    private final HelpTopic topic;
    private final List<HelpTopic> topics;
    private final String query;

    private HelpCommand(Mode mode, HelpTopic topic, List<HelpTopic> topics, String query) {
        this.mode = mode;
        this.topic = topic;
        this.topics = topics;
        this.query = query;
    }

    public static HelpCommand overview() {
        return new HelpCommand(Mode.OVERVIEW, null, TOPICS, null);
    }

    public static HelpCommand forKeyword(String keyword) {
        HelpTopic topic = TOPICS_BY_KEYWORD.get(keyword.toLowerCase(Locale.ROOT));
        return new HelpCommand(Mode.DETAIL, topic, null, null);
    }

    public static HelpCommand forQuery(String rawQuery) {
        String query = rawQuery.trim();
        if (query.isEmpty()) {
            return overview();
        }

        Optional<HelpTopic> exactMatch = findExact(query);
        if (exactMatch.isPresent()) {
            return new HelpCommand(Mode.DETAIL, exactMatch.get(), null, query);
        }

        List<HelpTopic> prefixMatches = findPrefixMatches(query);
        if (prefixMatches.size() == 1) {
            return new HelpCommand(Mode.DETAIL, prefixMatches.get(0), null, query);
        }

        List<HelpTopic> searchMatches = search(query);
        if (searchMatches.isEmpty()) {
            return new HelpCommand(Mode.NO_RESULTS, null, null, query);
        }

        return new HelpCommand(Mode.SEARCH, null, searchMatches, query);
    }

    @Override
    public CommandResult execute(CommandContext context) {
        switch (mode) {
        case OVERVIEW:
            context.getUi().printHelpOverview(TOPICS, null);
            break;
        case DETAIL:
            context.getUi().printHelpTopic(topic);
            break;
        case SEARCH:
            context.getUi().printHelpOverview(topics, query);
            break;
        case NO_RESULTS:
            context.getUi().printHelpNotFound(query);
            break;
        default:
            throw new IllegalStateException("Unhandled help mode: " + mode);
        }
        return new CommandResult(false, false);
    }

    private static Map<String, HelpTopic> buildTopicsByKeyword() {
        Map<String, HelpTopic> map = new LinkedHashMap<>();
        for (HelpTopic topic : TOPICS) {
            map.put(topic.name(), topic);
            for (String alias : topic.aliases()) {
                map.put(alias, topic);
            }
        }
        return map;
    }

    private static Optional<HelpTopic> findExact(String query) {
        return Optional.ofNullable(TOPICS_BY_KEYWORD.get(query.toLowerCase(Locale.ROOT)));
    }

    private static List<HelpTopic> findPrefixMatches(String query) {
        String normalized = query.toLowerCase(Locale.ROOT);
        return TOPICS.stream()
                .filter(topic -> topic.name().startsWith(normalized)
                        || topic.aliases().stream().anyMatch(alias -> alias.startsWith(normalized)))
                .collect(Collectors.toList());
    }

    private static List<HelpTopic> search(String query) {
        String normalized = query.toLowerCase(Locale.ROOT);
        List<HelpTopic> matches = new ArrayList<>();
        for (HelpTopic topic : TOPICS) {
            if (contains(topic.name(), normalized)
                    || topic.aliases().stream().anyMatch(alias -> contains(alias, normalized))
                    || contains(topic.summary(), normalized)
                    || topic.usage().length > 0
                    && java.util.Arrays.stream(topic.usage()).anyMatch(usageLine -> contains(usageLine, normalized))) {
                matches.add(topic);
            }
        }
        return matches;
    }

    private static boolean contains(String haystack, String needle) {
        return haystack.toLowerCase(Locale.ROOT).contains(needle);
    }
}
