package seedu.cardcollector.parsing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seedu.cardcollector.command.AddCommand;
import seedu.cardcollector.command.AnalyticsCommand;
import seedu.cardcollector.command.RemoveCardByIndexCommand;
import seedu.cardcollector.command.RemoveCardByNameCommand;
import seedu.cardcollector.command.HistoryCommand;
import seedu.cardcollector.command.Command;
import seedu.cardcollector.command.DownloadCommand;
import seedu.cardcollector.command.FindCommand;
import seedu.cardcollector.command.UploadCommand;
import seedu.cardcollector.command.UndoUploadCommand;
import seedu.cardcollector.command.ListCommand;
import seedu.cardcollector.command.TagCommand;
import seedu.cardcollector.exception.ParseBlankCommandException;
import seedu.cardcollector.exception.ParseInvalidArgumentException;
import seedu.cardcollector.exception.ParseUnknownCommandException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class ParserTest {
    private Parser parser;

    @BeforeEach
    public void setUp() {
        parser = new Parser();
    }

    //@@author HX2003
    @Test
    public void parse_unknownCommand_exceptionThrown() {
        assertThrows(ParseBlankCommandException.class, () -> parser.parse(""));
        assertThrows(ParseBlankCommandException.class, () -> parser.parse(" "));
        assertThrows(ParseBlankCommandException.class, () -> parser.parse("     "));
        assertThrows(ParseUnknownCommandException.class, () -> parser.parse("powwow"));
        assertThrows(ParseUnknownCommandException.class, () -> parser.parse("delicious flower"));
    }

    //@@author WeiHeng2003
    @Test
    public void parse_addCommand_success() throws Exception {
        assertInstanceOf(AddCommand.class, parser.parse("add /n Pikachu /q 1 /p 5.5"));
        assertInstanceOf(AddCommand.class, parser.parse("add /n Pikachu /p 5.5 /q 1"));
        assertInstanceOf(AddCommand.class, parser.parse("add /n Charizard /q 2 /p 99.99 /id "
                + java.util.UUID.randomUUID()));
        assertInstanceOf(AddCommand.class, parser.parse(
                "add /n Charizard /q 2 /p 99.99 /s Base Set /r Holo /c Near Mint /l English /no 4/102"));
    }

    //@@author WeiHeng2003
    @Test
    public void parse_addCommandMissingFlags_exceptionThrown() {
        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("add /n Pikachu /q 1")
        );
        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("add /n Pikachu /p 5.5")
        );
        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("add /q 1 /p 5.5")
        );
        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("add")
        );
    }

    //@@author WeiHeng2003
    @Test
    public void parse_addCommandInvalidNumbers_exceptionThrown() {
        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("add /n Pikachu /q abc /p 5.5")
        );
        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("add /n Pikachu /q 1 /p xyz")
        );
    }

    //@@author WeiHeng2003
    @Test
    public void parse_removeIndexCommand_success() throws Exception {
        assertInstanceOf(
                RemoveCardByIndexCommand.class,
                parser.parse("removeindex 1")
        );
        assertInstanceOf(
                RemoveCardByIndexCommand.class,
                parser.parse("removeindex 99")
        );
    }

    //@@author WeiHeng2003
    @Test
    public void parse_invalidRemoveIndexCommand_exceptionThrown() {
        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("removeindex abc")
        );
        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("removeindex 1.5")
        );
        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("removeindex")
        );
    }

    //@@author WeiHeng2003
    @Test
    public void parse_removeNameCommand_success() throws Exception {
        assertInstanceOf(
                RemoveCardByNameCommand.class,
                parser.parse("removename Pikachu")
        );
        assertInstanceOf(
                RemoveCardByNameCommand.class,
                parser.parse("removename Pikachu VMAX")
        );
    }

    //@@author WeiHeng2003
    @Test
    public void parse_invalidRemoveNameCommand_exceptionThrown() {
        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("removename")
        );
        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("removename   ")
        );
    }

    //@@author HX2003
    @Test
    public void parse_invalidArgumentHistory_exceptionThrown() {
        Parser parser = new Parser();

        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("history delicious")
        );

        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("history added -1")
        );

        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("history added 3t35t45")
        );

        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("history added all zz")
        );
    }

    @Test
    public void parse_historyCommand_success() throws
            ParseBlankCommandException,
            ParseUnknownCommandException,
            ParseInvalidArgumentException {
        Parser parser = new Parser();

        Command command0 = parser.parse("history");
        assertInstanceOf(HistoryCommand.class, command0);

        Command command1 = parser.parse("history added");
        assertInstanceOf(HistoryCommand.class, command1);

        Command command2 = parser.parse("history modified");
        assertInstanceOf(HistoryCommand.class, command2);

        Command command3 = parser.parse("history removed");
        assertInstanceOf(HistoryCommand.class, command3);

        Command command4 = parser.parse("history removed all");
        assertInstanceOf(HistoryCommand.class, command4);

        Command command5 = parser.parse("history modified 5");
        assertInstanceOf(HistoryCommand.class, command5);

        Command command6 = parser.parse("history entire 5 asc");
        assertInstanceOf(HistoryCommand.class, command6);

        Command command7 = parser.parse("history entire 5 descend");
        assertInstanceOf(HistoryCommand.class, command7);
    }

    //@@author Simplificatedd
    @Test
    public void parse_transferCommands_success() throws ParseBlankCommandException,
            ParseUnknownCommandException, ParseInvalidArgumentException {
        Parser parser = new Parser();

        Command downloadCommand = parser.parse("download /f backups/cards.txt");
        assertInstanceOf(DownloadCommand.class, downloadCommand);

        Command uploadCommand = parser.parse("upload /f backups/cards.txt");
        assertInstanceOf(UploadCommand.class, uploadCommand);

        Command undoUploadCommand = parser.parse("undoupload");
        assertInstanceOf(UndoUploadCommand.class, undoUploadCommand);
    }

    @Test
    public void parse_transferInvalidPath() {
        Parser parser = new Parser();

        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("download")
        );

        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("upload /f")
        );

        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("undoupload now")
        );
    }

    @Test
    public void parse_tagAndTagFilterCommands_success() throws Exception {
        assertInstanceOf(FindCommand.class, parser.parse("find /t trade"));
        assertInstanceOf(ListCommand.class, parser.parse("list /t sealed"));
        assertInstanceOf(TagCommand.class, parser.parse("tag add 3 /t deck"));
        assertInstanceOf(TagCommand.class, parser.parse("folder remove 2 /t trade"));
        assertInstanceOf(AnalyticsCommand.class, parser.parse("analytics"));
        assertInstanceOf(AnalyticsCommand.class, parser.parse("stats"));
    }

    @Test
    public void parse_invalidTagCommands_exceptionThrown() {
        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("tag add /t deck")
        );
        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("tag move 1 /t deck")
        );
        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("list sealed")
        );
        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("analytics now")
        );
        assertThrows(
                ParseInvalidArgumentException.class,
                () -> parser.parse("stats now")
        );
    }

    //@@author
}
