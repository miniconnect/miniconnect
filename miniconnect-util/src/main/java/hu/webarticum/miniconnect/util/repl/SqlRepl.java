package hu.webarticum.miniconnect.util.repl;

import java.io.PrintStream;
import java.util.regex.Pattern;

import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniResult;

// TODO: better abstraction (context/executor vs output-handling), builder
public class SqlRepl implements Repl {

    private static final Pattern HELP_PATTERN = Pattern.compile(
            "\\s*help\\s*(?:\\(\\s*\\)\\s*)?(?:;\\s*)?", Pattern.CASE_INSENSITIVE);

    private static final Pattern QUIT_PATTERN = Pattern.compile(
            "\\s*(?:quit|exit)\\s*(?:\\(\\s*\\)\\s*)?(?:;\\s*)?", Pattern.CASE_INSENSITIVE);

    private static final Pattern COMMAND_PATTERN = Pattern.compile(
            "^(?:" + HELP_PATTERN + "|" + QUIT_PATTERN +
                    "|(?:[^'\"`\\\\;]++|\\\\.|(['\"`])(?:(?:\\\\|\\1)\\1|(?!\\1).)++\\1)*;.*+)$",
            Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
    

    private final MiniSession session;
    
    private final PrintStream out;
    
    private final PrintStream err;
    

    public SqlRepl(
            MiniSession session,
            PrintStream out,
            PrintStream err) {
        this.session = session;
        this.out = out;
        this.err = err;
    }
    

    @Override
    public Pattern commandPattern() {
        return COMMAND_PATTERN;
    }
    
    @Override
    public void welcome() {
        out.println("\nWelcome in miniConnect SQL REPL!\n");
    }

    @Override
    public void prompt() {
        out.print("SQL > ");
    }

    @Override
    public void prompt2() {
        out.print("    > ");
    }
    
    @Override
    public boolean execute(String command) {
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
    
    private void printException(Exception e) {
        String message = e.getMessage();
        if (message == null || message.isEmpty()) {
            message = e.getClass().getName();
        }
        out.println(message);
        
        e.printStackTrace(err);
    }

    private void printResult(MiniResult result) {
        if (!result.isSuccess()) {
            out.println(String.format("ERROR: %s", result.errorMessage()));
            return;
        }
        
        new ResultSetPrinter().print(result.resultSet(), out);
    }
    
    private void help() {
        out.println();
        out.println(String.format("  MiniConnect SQL REPL - %s",
                session.getClass().getSimpleName()));
        out.println();
        out.println("  Commands:");
        out.println("    \"help\": prints this document");
        out.println("    \"exit\", \"quit\": quits this program");
        out.println("    <any SQL>: will be executed in the session");
        out.println("      (must be terminated with \";\")");
        out.println();
    }
    
    @Override
    public void bye() {
        out.println("\nBye-bye!\n");
    }
    
}
