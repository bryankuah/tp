package seedu.cardcollector.exception;

/**
 * Signals an argument error during the parsing of a command.
 */
public class ParseInvalidArgumentException extends ParseException {
    /**
     * Constructs a new exception with a specified message.
     *
     * @param message The message explaining the cause of the exception.
     */
    public ParseInvalidArgumentException(String message) {
        super(message);
    }
}