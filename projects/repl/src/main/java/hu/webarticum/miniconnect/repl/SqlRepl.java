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
import hu.webarticum.miniconnect.api.MiniError;
import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.record.ResultTable;

// TODO: better abstraction (context/executor vs output-handling), builder
public class SqlRepl implements Repl {
    
    private static final BeeFragment TERMINATOR_FRAGMENT = Bee
            .then(Bee.WHITESPACE.any())
            .then(Bee.fixed(";"));

    private static final BeeFragment BRACKETS_FRAGMENT = Bee
            .then(Bee.WHITESPACE.any())
            .then(Bee.fixed("("))
            .then(Bee.WHITESPACE.any())
            .then(Bee.fixed(")"));

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
            .then(Bee.WHITESPACE.any());
    
    private static final BeeFragment HELP_FRAGMENT = Bee
            .then(Bee.WHITESPACE.any())
            .then(Bee.fixed("help"))
            .then(BRACKETS_FRAGMENT.optional())
            .then(TERMINATOR_FRAGMENT.optional())
            .then(Bee.WHITESPACE.any());
    
    private static final BeeFragment QUIT_FRAGMENT = Bee
            .then(Bee.WHITESPACE.any())
            .then(Bee.oneFixedOf("exit", "quit"))
            .then(BRACKETS_FRAGMENT.optional())
            .then(TERMINATOR_FRAGMENT.optional())
            .then(Bee.WHITESPACE.any());
    
    private static final BeeFragment QUERY_FRAGMENT = Bee.ANYTHING;
    
    /*
    private static final BeeFragment QUERY_FRAGMENT = Bee
            .then(Bee.simple("[^'\"`\\\\;]").more(Greediness.POSSESSIVE) // TODO: range?
                    .or(Bee.fixed("\\").then(Bee.CHAR))
                    .or(Bee.oneCharOf("'\"`").as("quote")
                            .then(Bee.fixed("\\").or(Bee.ref("quote")).then(Bee.ref("quote"))
                                    .or(Bee.lookAheadNot(Bee.ref("quote")))
                                    .more(Greediness.POSSESSIVE))
                            .then(Bee.ref("quote"))
                    ))
            .then(TERMINATOR_FRAGMENT.optional())
            .then(Bee.WHITESPACE.any());*/
    
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

    private final Appendable out;


    public SqlRepl(MiniSession session, Appendable out) {
        this.session = session;
        this.out = out;
    }


    @Override
    public Pattern commandPattern() {
        return COMMAND_PATTERN;
    }

    @Override
    public void welcome() throws IOException {
        out.append("\nWelcome in miniConnect SQL REPL!\n\n");
    }

    @Override
    public void prompt() throws IOException {
        out.append("SQL > ");
    }

    @Override
    public void prompt2() throws IOException {
        out.append("    > ");
    }

    @Override
    public boolean execute(String command) throws IOException {
        Matcher dataMatcher = DATA_PATTERN.matcher(command);
        if (dataMatcher.matches()) {
            String escapedName = dataMatcher.group("name");
            String name = UNESCAPE_PATTERN.matcher(escapedName).replaceAll("$1");
            String escapedSource = dataMatcher.group("source");
            String source = UNESCAPE_PATTERN.matcher(escapedSource).replaceAll("$1");
            putLargeData(name, source);
            return true;
        }
        
        if (HELP_PATTERN.matcher(command).matches()) {
            printHelp();
            return true;
        }

        if (QUIT_PATTERN.matcher(command).matches()) {
            return false;
        }

        MiniResult result = null;
        try {
            result = session.execute(command);
        } catch (Exception e) {
            printException(e);
        }
        
        if (result != null) {
            printResult(result);
        }

        return true;
    }

    private void printException(Exception e) throws IOException {
        String message = e.getMessage();
        if (message == null || message.isEmpty()) {
            message = e.getClass().getName();
        }
        out.append(message);
        out.append('\n');
    }

    private void printResult(MiniResult result) throws IOException {
        if (!result.success()) {
            printError(result.error());
            return;
        }

        ResultTable resultTable = new ResultTable(result.resultSet());
        new ResultSetPrinter().print(resultTable, out);
    }
    
    private void putLargeData(String name, String source) throws IOException {
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
        printLargeDataSaveResult(result, name, length);
    }
    
    private void printLargeDataSaveResult(
            MiniLargeDataSaveResult result, String name, long length) throws IOException {
        if (result.success()) {
            printSuccessLargeDataSaveResult(name, length);
        } else {
            printError(result.error());
        }
    }

    private void printSuccessLargeDataSaveResult(String name, long length) throws IOException {
        out.append("  Successfully stored\n");
        out.append("  Size: " + length + " bytes\n");
        out.append("  Variable name: '" + name + "'\n");
    }

    private void printError(MiniError error) throws IOException {
        out.append("  ERROR!\n");
        out.append("  Code: " + error.code() + "\n");
        out.append("  SQL state: '" + error.sqlState() + "'\n");
        out.append("  Message: '" + error.message() + "'\n");
    }
    
    private void printHelp() throws IOException {
        out.append('\n');
        out.append(String.format("  MiniConnect SQL REPL - %s%n",
                session.getClass().getSimpleName()));
        out.append('\n');
        out.append("  Commands:\n");
        out.append("    \"help\": prints this document\n");
        out.append("    \"data:\"<name>\":\"<data>: sends large data\n");
        out.append("    \"data:\"<name>\":@\"<file>: sends large data from file\n");
        out.append("    \"exit\", \"quit\": quits this program\n");
        out.append("    <any SQL>: will be executed in the session\n");
        out.append("      (must be terminated with \";\")\n");
        out.append('\n');
    }

    @Override
    public void bye() throws IOException {
        out.append("\nBye-bye!\n\n");
    }

}
