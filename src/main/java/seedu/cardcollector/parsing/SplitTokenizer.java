package seedu.cardcollector.parsing;

import java.util.Arrays;
import java.util.ArrayList;


/**
 * Tokenizes a string by splitting it around matches of the given regular expression,
 * and provides indexed access to the resulting tokens.
 */
public class SplitTokenizer {
    private final String regex;
    private ArrayList<String> tokens;

    /**
     * Creates a tokenizer with the specified regular expression,
     * that can be used to split strings around matches of the given regular expression.
     *
     * @param regex The delimiting regular expression.
     */
    public SplitTokenizer(String regex) {
        this.regex = regex;
        this.tokens = new ArrayList<>();
    }

    /**
     * Tokenizes the input string using the configured regex and stores the resulting tokens.
     *
     * @param input The string to tokenize.
     */
    public void tokenize(String input) {
        tokens = new ArrayList<>(Arrays.asList(input.split(regex)));
    }

    /**
     * Returns the token string at the specified position in the tokenized list.
     *
     * @param index The position of the desired token in the tokenized list.
     * @return The token string, or {@code null} if the index is out of bounds.
     */
    public String getString(int index) {
        if (index >= 0 && index < tokens.size()) {
            return tokens.get(index);
        }

        return null;
    }

    /**
     * Returns the number of tokens in the tokenized list.
     *
     * @return The number of tokens. This value is 0 if nothing has been tokenized yet.
     */
    public int getNumTokens() {
        return tokens.size();
    }
}
