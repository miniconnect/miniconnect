package hu.webarticum.miniconnect.repl;

import java.io.PrintStream;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import hu.webarticum.miniconnect.api.MiniConnection;

// TODO: better abstraction (context/executor vs output-handling), builder
public class SqlRepl implements Repl {
    
    private static final Pattern QUIT_PATTERN = Pattern.compile(
            "\\s*(?:quit|exit)\\s*(?:\\(\\s*\\)\\s*)?", Pattern.CASE_INSENSITIVE);
    

    private final MiniConnection connection;

    private final Supplier<String> prompt;

    private final Supplier<String> prompt2;
    
    private final PrintStream out;
    
    private final PrintStream err;
    

    public SqlRepl(
            MiniConnection connection,
            Supplier<String> prompt,
            Supplier<String> prompt2,
            PrintStream out,
            PrintStream err) {
        this.connection = connection;
        this.prompt = prompt;
        this.prompt2 = prompt2;
        this.out = out;
        this.err = err;
    }
    
    
    @Override
    public void welcome() {
        out.println("\nWelcome in miniConnect SQL REPL!\n");
    }

    @Override
    public void prompt() {
        out.print(prompt.get());
    }

    @Override
    public void prompt2() {
        out.print(prompt2.get());
    }
    
    @Override
    public boolean execute(String command) {
        if (QUIT_PATTERN.matcher(command).matches()) {
            return false;
        }
        
        out.println(String.format("  Your command was: '%s'", command));

        return true;
    }
    
    @Override
    public void bye() {
        out.println("\nBye-bye!\n");
    }
    
}
