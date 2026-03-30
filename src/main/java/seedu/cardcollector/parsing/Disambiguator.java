package seedu.cardcollector.parsing;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Disambiguator {
    /**
     * Disambiguate an input string against the ArrayList of keywords strings,
     * the input string will be converted to lowercase before further matching.
     * <p>
     * To illustrate, if the keywords are "share", "shard", "shout"
     * Input of "sh" matches all 3 keywords, as we cannot determine which it is, an exception is thrown.
     * Input of "sha" matches all 2 keywords, as we cannot determine which it is, an exception is thrown.
     * Input of "shar" matches "shard", thus this valid string is returned.
     * Input of "" does not match any keyword, thus an exception is thrown.
     * Input of "boot" does not match any keyword, thus an exception is thrown.
     *
     * @param keywords An ArrayList of strings to look up against, should be lowercase.
     * @param input The input string.
     * @return The matching string if found.
     * @throws IllegalArgumentException if no match found or if ambiguous.
     */
    public static String disambiguate(ArrayList<String> keywords, String input) throws IllegalArgumentException {
        String inputTrimmed = input.trim();

        if (inputTrimmed.isEmpty()) {
            throw new IllegalArgumentException("empty argument");
        }

        // Find all keywords that start with the input argument prefix
        ArrayList<String> matches = keywords.stream()
                .filter(command -> command.startsWith(input))
                .collect(Collectors.toCollection(ArrayList::new));

        if (matches.isEmpty()) {
            throw new IllegalArgumentException("unrecognized argument \"" + input + "\"");
        }

        if (matches.size() == 1) {
            return matches.get(0);
        }

        // Multiple matches implies ambiguity
        StringBuilder builder = new StringBuilder("did you mean: ");

        for (int i = 0; i < matches.size(); i++) {
            builder.append(matches.get(0));

            if (i == matches.size() - 1) {
                builder.append("or");
            } else if (i < matches.size() - 1) {
                builder.append(",");
            }
        }

        builder.append("?");

        throw new IllegalArgumentException(builder.toString());
    }

    /**
     * Disambiguate an input string against an Enum,
     * the enum keywords will be converted to lowercase before further matching,
     * the input string will be converted to lowercase before further matching.
     *
     * @param enumClass The enum class containing possible keywords.
     * @param keywordExtractor A function to get the keyword from enum constant.
     * @param input The input string.
     * @return The matching string if found.
     * @throws IllegalArgumentException if no match found or if ambiguous.
     */
    public static <T extends Enum<T>> T disambiguate(
            Class<T> enumClass, Function<T, String> keywordExtractor, String input)
            throws IllegalArgumentException {
        T[] constants = enumClass.getEnumConstants();

        ArrayList<String> keywords = Arrays.stream(constants)
                .map(keywordExtractor)
                .map(String::toLowerCase)
                .collect(Collectors.toCollection(ArrayList::new));

        String keyword = disambiguate(keywords, input);

        return Arrays.stream(constants)
                .filter(e -> keywordExtractor.apply(e).equalsIgnoreCase(keyword))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Enum conversion failed for: " + keyword));
    }

}
