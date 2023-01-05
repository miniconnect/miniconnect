package hu.webarticum.miniconnect.repl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.webarticum.regexbee.Bee;
import hu.webarticum.regexbee.BeeFragment;
import hu.webarticum.regexbee.Greediness;
import hu.webarticum.regexbee.character.CharacterRangeFragment;
import hu.webarticum.miniconnect.api.MiniError;
import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.record.ResultTable;

// TODO: better abstraction (context/executor vs output-handling), builder
public class SqlRepl implements Repl {
    
    public static final String DEFAULT_TITLE_MESSAGE = "Welcome in miniConnect SQL REPL!";
    
    
    private static final BeeFragment TERMINATOR_FRAGMENT = Bee
            .then(Bee.WHITESPACE.any())
            .then(Bee.fixedChar(';'))
            ;

    private static final BeeFragment PARENTHESES_FRAGMENT = Bee
            .then(Bee.WHITESPACE.any())
            .then(Bee.fixedChar('('))
            .then(Bee.WHITESPACE.any())
            .then(Bee.fixedChar(')'))
            ;

    private static final BeeFragment DATA_FRAGMENT = Bee
            .then(Bee.WHITESPACE.any())
            .then(Bee.fixed("data:"))
            .then(Bee.simple("[^:\\\\]|\\\\.").more().as("name")) // TODO: use range/or?
            .then(Bee.fixed(":"))
            .then(Bee.WHITESPACE.any())
            .then(Bee.fixed("@").optional()
                    .then(Bee.simple("[^\\)\\\\]|\\\\.").more() // TODO: use range/or?
                    .as("source")))
            .then(TERMINATOR_FRAGMENT.optional())
            .then(Bee.WHITESPACE.any())
            ;
    
    private static final BeeFragment HELP_FRAGMENT = Bee
            .then(Bee.WHITESPACE.any())
            .then(Bee.fixed("help"))
            .then(PARENTHESES_FRAGMENT.optional())
            .then(TERMINATOR_FRAGMENT.optional())
            .then(Bee.WHITESPACE.any())
            ;
    
    private static final BeeFragment QUIT_FRAGMENT = Bee
            .then(Bee.WHITESPACE.any())
            .then(Bee.oneFixedOf("exit", "quit"))
            .then(PARENTHESES_FRAGMENT.optional())
            .then(TERMINATOR_FRAGMENT.optional())
            .then(Bee.WHITESPACE.any())
            ;
    
    private static final BeeFragment QUERY_FRAGMENT = Bee
            .then(new CharacterRangeFragment(false, "'\"`;").more(Greediness.POSSESSIVE)
                    .or(Bee.quoted('\'', '\\'))
                    .or(Bee.quoted('"', '\\'))
                    .or(Bee.quoted('`', '`'))
                    .more())
            .then(TERMINATOR_FRAGMENT)
            .then(Bee.WHITESPACE.any())
            ;
    
    private static final Pattern DATA_PATTERN = DATA_FRAGMENT.toPattern(Pattern.CASE_INSENSITIVE);
    
    private static final Pattern HELP_PATTERN = HELP_FRAGMENT.toPattern(Pattern.CASE_INSENSITIVE);

    private static final Pattern QUIT_PATTERN = QUIT_FRAGMENT.toPattern(Pattern.CASE_INSENSITIVE);
    
    private static final Pattern COMMAND_PATTERN = Bee
            .then(Bee.BEGIN)
            .then(DATA_FRAGMENT
                    .or(HELP_FRAGMENT)
                    .or(QUIT_FRAGMENT)
                    .or(QUERY_FRAGMENT))
            .then(Bee.END)
            .toPattern(Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    
    private static final Pattern UNESCAPE_PATTERN = Pattern.compile("\\\\(.)");
    

    private final MiniSession session;
    
    private final String titleMessage;


    public SqlRepl(MiniSession session) {
        this(session, DEFAULT_TITLE_MESSAGE);
    }

    public SqlRepl(MiniSession session, String titleMessage) {
        this.session = session;
        this.titleMessage = titleMessage;
    }


    public static void printHelp(AnsiAppendable out) throws IOException {
        out.append("\n  ");
        out.appendAnsi(AnsiUtil.formatAsHeader("MiniConnect SQL REPL"));
        out.append("\n\n  ");
        out.appendAnsi(AnsiUtil.formatAsHeader("Commands:"));
        out.append("\n    ");
        out.appendAnsi(AnsiUtil.formatAsHeader("help"));
        out.append("                 prints this document\n    ");
        out.appendAnsi(
                AnsiUtil.formatAsHeader("data") +
                ":" + AnsiUtil.formatAsParameter("<name>") +
                ":" + AnsiUtil.formatAsParameter("<data>"));
        out.append("   sends large data\n    ");
        out.appendAnsi(
                AnsiUtil.formatAsHeader("data") +
                ":" + AnsiUtil.formatAsParameter("<name>") +
                ":@" + AnsiUtil.formatAsParameter("<file>"));
        out.append("  sends large data from file\n    ");
        out.appendAnsi(AnsiUtil.formatAsHeader("exit") + ", " + AnsiUtil.formatAsHeader("quit"));
        out.append("           quits this program\n    ");
        out.appendAnsi(AnsiUtil.formatAsParameter("<any SQL>"));
        out.append("            will be executed in the session\n");
        out.append("                         must be terminated with semicolon (;)\n\n");
    }

    
    @Override
    public boolean isCommandComplete(String command) {
        if (command.isEmpty()) {
            return true;
        }
        return COMMAND_PATTERN.matcher(command).matches();
    }

    @Override
    public void welcome(AnsiAppendable out) throws IOException {
        out.append("\n");
        out.appendAnsi(AnsiUtil.formatAsHeader(titleMessage));
        out.append("\n\n");
    }

    @Override
    public void prompt(AnsiAppendable out) throws IOException {
        out.appendAnsi("" + AnsiUtil.formatAsPrompt("SQL") + AnsiUtil.formatAsPrompt2(" > "));
    }

    @Override
    public void prompt2(AnsiAppendable out) throws IOException {
        out.appendAnsi(AnsiUtil.formatAsPrompt2("    > "));
    }

    @Override
    public boolean execute(String command, AnsiAppendable out) throws IOException {
        if (command.isEmpty()) {
            return true;
        }
        
        Matcher dataMatcher = DATA_PATTERN.matcher(command);
        if (dataMatcher.matches()) {
            String escapedName = dataMatcher.group("name");
            String name = UNESCAPE_PATTERN.matcher(escapedName).replaceAll("$1");
            String escapedSource = dataMatcher.group("source");
            String source = UNESCAPE_PATTERN.matcher(escapedSource).replaceAll("$1");
            putLargeData(name, source, out);
            return true;
        }
        
        if (HELP_PATTERN.matcher(command).matches()) {
            printHelp(out);
            return true;
        }

        if (QUIT_PATTERN.matcher(command).matches()) {
            return false;
        }

        MiniResult result = null;
        try {
            result = session.execute(command);
        } catch (Exception e) {
            printException(e, out);
        }
        
        if (result != null) {
            printResult(result, out);
        }

        return true;
    }

    private void printException(Exception e, AnsiAppendable out) throws IOException {
        String message = e.getMessage();
        if (message == null || message.isEmpty()) {
            message = e.getClass().getName();
        }
        out.append(message);
        out.append('\n');
    }

    private void printResult(MiniResult result, AnsiAppendable out) throws IOException {
        if (result.success()) {
            printSuccessResult(result, out);
        } else {
            printError(result.error(), out);
        }
    }

    private void printSuccessResult(MiniResult result, AnsiAppendable out) throws IOException {
        out.appendAnsi("\n  " + AnsiUtil.formatAsSuccess("Query was successfully executed!") + "\n\n");
        
        printWarnings(result.warnings(), out);
        
        if (result.hasResultSet()) {
            ResultTable resultTable = new ResultTable(result.resultSet());
            new ResultSetPrinter().print(resultTable, out);
        }
    }
    
    private void printWarnings(ImmutableList<MiniError> warnings, AnsiAppendable out) throws IOException {
        if (warnings.isEmpty()) {
            return;
        }
        
        for (MiniError warning : warnings) {
            out.appendAnsi(AnsiUtil.formatAsWarning("  WARNING: " + warning.message()));
            out.append('\n');
        }

        out.append('\n');
    }

    private void putLargeData(String name, String source, AnsiAppendable out) throws IOException {
        long length;
        InputStream in;
        if (source.length() > 0 && source.charAt(0) == '@') {
            File file = new File(source.substring(1));
            length = file.length();
            in = new FileInputStream(file);
        } else {
            byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
            length = bytes.length;
            in = new ByteArrayInputStream(bytes);
        }
        MiniLargeDataSaveResult result = session.putLargeData(name, length, in);
        printLargeDataSaveResult(result, name, length, out);
    }
    
    private void printLargeDataSaveResult(
            MiniLargeDataSaveResult result, String name, long length, AnsiAppendable out) throws IOException {
        if (result.success()) {
            printSuccessLargeDataSaveResult(name, length, out);
        } else {
            printError(result.error(), out);
        }
    }

    private void printSuccessLargeDataSaveResult(String name, long length, AnsiAppendable out) throws IOException {
        out.appendAnsi("  " + AnsiUtil.formatAsSuccess("Successfully stored") + "\n");
        out.append("  Size: " + length + " bytes\n");
        out.append("  Variable name: '" + name + "'\n");
    }

    private void printError(MiniError error, AnsiAppendable out) throws IOException {
        out.append("\n  ");
        out.appendAnsi(AnsiUtil.formatAsError("ERROR!"));
        out.append("\n\n");
        out.append("  Code: " + error.code() + "\n");
        out.append("  SQL state: " + error.sqlState() + "\n");
        out.appendAnsi("  Message: " + AnsiUtil.formatAsError(error.message()) + "\n\n");
    }
    
    @Override
    public void bye(AnsiAppendable out) throws IOException {
        out.append("\nBye-bye!\n\n");
    }

}
