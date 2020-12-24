package hu.webarticum.miniconnect.repl;

import java.io.IOException;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import hu.webarticum.miniconnect.api.MiniConnection;

// TODO: better abstraction (context/executor vs output-handling), builder
public class SqlRepl implements Repl {
    
    private static final Pattern QUIT_PATTERN = Pattern.compile(
            "\\s*(?:quit|exit)\\s*(?:\\(\\s*\\)\\s*)?", Pattern.CASE_INSENSITIVE);
    

    private final MiniConnection connection;

    private final Supplier<String> prompt;
    
    private final Appendable out;
    
    private final Appendable err;
    

    public SqlRepl(
            MiniConnection connection,
            Supplier<String> prompt,
            Appendable out,
            Appendable err) {
        this.connection = connection;
        this.prompt = prompt;
        this.out = out;
        this.err = err;
    }
    
    
    @Override
    public void welcome() {
        writeSilently(out, "\nWelcome in miniConnect SQL REPL!\n\n");
        writeSilently(out, prompt.get());
    }
    
    private void writeSilently(Appendable out, String text) {
        try {
            out.append(text);
        } catch (IOException e) {
            // nothing to do
        }
    }
    
    @Override
    public boolean execute(String command) {
        if (QUIT_PATTERN.matcher(command).matches()) {
            return false;
        }
        
        writeSilently(out, String.format("  Your command was: '%s'\n", command));

        writeSilently(out, prompt.get());
        return true;
    }
    
    @Override
    public void bye() {
        writeSilently(out, "\nBye-bye!\n\n");
    }
    
}
