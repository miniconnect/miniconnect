package hu.webarticum.miniconnect.tool.repl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniLobResult;
import hu.webarticum.miniconnect.api.MiniResult;

// TODO: better abstraction (context/executor vs output-handling), builder
public class SqlRepl implements Repl {

    private static final Pattern LOB_PATTERN = Pattern.compile(
            "\\s*lob:\\s*(?:\\s*(?<source>@?(?:([^\\)\\\\]|\\\\.)+))\\s*)(?:;\\s*)?",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern HELP_PATTERN = Pattern.compile(
            "\\s*help\\s*(?:\\(\\s*\\)\\s*)?(?:;\\s*)?",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern QUIT_PATTERN = Pattern.compile(
            "\\s*(?:quit|exit)\\s*(?:\\(\\s*\\)\\s*)?(?:;\\s*)?",
            Pattern.CASE_INSENSITIVE);
    
    private static final Pattern COMMAND_PATTERN = Pattern.compile(
            "^(?:" + LOB_PATTERN + "|" + HELP_PATTERN + "|" + QUIT_PATTERN +
                    "|(?:[^'\"`\\\\;]++|\\\\.|(['\"`])(?:(?:\\\\|\\1)\\1|(?!\\1).)++\\1)*;.*+)$",
            Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    private static final Pattern UNESCAPE_PATTERN = Pattern.compile(
            "\\\\(.)");
    

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
        Matcher lobMatcher = LOB_PATTERN.matcher(command);
        if (lobMatcher.matches()) {
            String escapedSource = lobMatcher.group("source");
            String source = UNESCAPE_PATTERN.matcher(escapedSource).replaceAll("$1");
            lob(source);
            return true;
        }
        
        if (HELP_PATTERN.matcher(command).matches()) {
            help();
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
            out.append(String.format(
                    "  ERROR(%s): %s%n",
                    result.errorCode(),
                    result.errorMessage()));
            return;
        }

        new ResultSetPrinter().print(result.resultSet(), out);
    }
    
    private void lob(String source) throws IOException {
        long length;
        InputStream in;
        if (source.length() > 0 && source.charAt(0) == '@') {
            File file = new File(source.substring(1));
            length = file.length();
            if (length > 0) {
                in = new FileInputStream(file);
            } else {
                in = InputStream.nullInputStream();
            }
        } else {
            byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
            length = bytes.length;
            in = new ByteArrayInputStream(bytes);
        }

        MiniLobResult lobResult = session.putLargeData(length, in);
        printLobResult(lobResult, length);
    }
    
    private void printLobResult(MiniLobResult lobResult, long length) throws IOException {
        if (lobResult.success()) {
            printSuccessLobResult(lobResult, length);
        } else {
            printErrorLobResult(lobResult);
        }
    }

    private void printSuccessLobResult(MiniLobResult lobResult, long length) throws IOException {
        out.append("  Successfully stored\n");
        out.append("  Size: " + length + " bytes\n");
        out.append("  Variable name: '" + lobResult.variableName() + "'\n");
    }

    private void printErrorLobResult(MiniLobResult lobResult) throws IOException {
        out.append("  Failed to store data\n");
        out.append("  Error code: " + lobResult.errorCode() + "\n");
        out.append("  Error message: " + lobResult.errorMessage() + "\n");
    }

    private void help() throws IOException {
        out.append('\n');
        out.append(String.format("  MiniConnect SQL REPL - %s%n",
                session.getClass().getSimpleName()));
        out.append('\n');
        out.append("  Commands:\n");
        out.append("    \"help\": prints this document\n");
        out.append("    \"lob:\"<data>: sends lob data\n");
        out.append("    \"lob:@\"<file>: sends lob data from file\n");
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
