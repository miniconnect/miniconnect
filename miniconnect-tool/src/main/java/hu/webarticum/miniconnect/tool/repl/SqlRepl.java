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
import hu.webarticum.miniconnect.api.MiniLargeDataSaveResult;
import hu.webarticum.miniconnect.api.MiniResult;

// TODO: better abstraction (context/executor vs output-handling), builder
// TODO: use regex-bee
public class SqlRepl implements Repl {

    private static final Pattern DATA_PATTERN = Pattern.compile(
            "\\s*data:(?<name>([^:\\\\]|\\\\.)+):\\s*(?:\\s*(?<source>@?(?:([^\\)\\\\]|\\\\.)+))\\s*)(?:;\\s*)?",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern HELP_PATTERN = Pattern.compile(
            "\\s*help\\s*(?:\\(\\s*\\)\\s*)?(?:;\\s*)?",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern QUIT_PATTERN = Pattern.compile(
            "\\s*(?:quit|exit)\\s*(?:\\(\\s*\\)\\s*)?(?:;\\s*)?",
            Pattern.CASE_INSENSITIVE);
    
    private static final Pattern COMMAND_PATTERN = Pattern.compile(
            "^(?:" + DATA_PATTERN + "|" + HELP_PATTERN + "|" + QUIT_PATTERN +
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
            out.append(String.format(
                    "  ERROR(%s): %s%n",
                    result.errorCode(),
                    result.errorMessage()));
            return;
        }

        new ResultSetPrinter().print(result.resultSet(), out);
    }
    
    private void putLargeData(String name, String source) throws IOException {
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

        MiniLargeDataSaveResult result = session.putLargeData(name, length, in);
        printLargeDataSaveResult(result, name, length);
    }
    
    private void printLargeDataSaveResult(
            MiniLargeDataSaveResult result, String name, long length) throws IOException {
        
        if (result.success()) {
            printSuccessLargeDataSaveResult(result, name, length);
        } else {
            printErrorLargeDataSaveResult(result);
        }
    }

    private void printSuccessLargeDataSaveResult(
            MiniLargeDataSaveResult result, String name, long length) throws IOException {
        
        out.append("  Successfully stored\n");
        out.append("  Size: " + length + " bytes\n");
        out.append("  Variable name: '" + name + "'\n");
    }

    private void printErrorLargeDataSaveResult(MiniLargeDataSaveResult result) throws IOException {
        out.append("  Failed to store data\n");
        out.append("  Error code: " + result.errorCode() + "\n");
        out.append("  Error message: " + result.errorMessage() + "\n");
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
