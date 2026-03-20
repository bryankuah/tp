package seedu.cardcollector.exception;

/**
 * Signals an unknown command error during the parsing of a command.
 */
public class ParseUnknownCommandException extends ParseException {
    /**
     * Constructs a new exception with a specified message.
     *
     * @param message The message explaining the cause of the exception.
     */
    public ParseUnknownCommandException(String message) {
        super(message);
    }
}