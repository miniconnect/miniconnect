package hu.webarticum.miniconnect.repl;

import java.io.PrintStream;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import hu.webarticum.miniconnect.api.MiniConnection;

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
    

    private final MiniConnection connection;
    
    private final PrintStream out;
    
    private final PrintStream err;
    

    public SqlRepl(
            MiniConnection connection,
            PrintStream out,
            PrintStream err) {
        this.connection = connection;
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
        
        out.println(String.format("  Your command was: '%s'", command));

        return true;
    }
    
    private void help() {
        out.println();
        out.println(String.format("  MiniConnect SQL REPL - %s",
                connection.getClass().getSimpleName()));
        out.println();
        out.println("  Commands:");
        out.println("    \"help\": prints this document");
        out.println("    \"exit\", \"quit\": quits this program");
        out.println("    <any SQL>: will be executed on the connection");
        out.println("      (must be terminated with \";\")");
        out.println();
    }
    
    @Override
    public void bye() {
        out.println("\nBye-bye!\n");
    }
    
}
