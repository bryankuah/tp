package seedu.cardcollector.exception;

/**
 * Signals a generic error during the parsing of a command.
 */
public class ParseException extends Exception {
    /**
     * Constructs a new exception with a specified message.
     *
     * @param message The message explaining the cause of the exception.
     */
    public ParseException(String message) {
        super(message);
    }
}