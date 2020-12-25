package hu.webarticum.miniconnect.repl;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

import hu.webarticum.miniconnect.api.MiniConnection;
import hu.webarticum.miniconnect.manager.MiniDriverManager;

public class SqlReplMain {
    
    private static final Pattern QUOTES_PATTERN = Pattern.compile(
            "^(?:[^'\"`\\\\;]++|\\\\.|(['\"`])(?:(?:\\\\|\\1)\\1|(?!\\1).)++\\1)*;.*+$", // NOSONAR
            Pattern.MULTILINE | Pattern.DOTALL);
    

    public static void main(String[] args) throws IOException {
        try (MiniConnection connection = MiniDriverManager.openConnection(args[0], new Properties())) {
            Repl repl = new SqlRepl(
                    connection,
                    () -> "SQL > ",
                    () -> "    > ",
                    System.out, // NOSONAR
                    System.err); // NOSONAR
            new ReplRunner(repl, System.in, QUOTES_PATTERN).run();
        }
    }
    
}
