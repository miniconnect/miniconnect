package hu.webarticum.miniconnect.repl;

import java.io.IOException;

import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class RichReplRunner implements ReplRunner {
    
    private final AnsiAppendable out = new RichAnsiAppendable(System.out); // NOSONAR System.out is necessary
    

    @Override
    public void run(Repl repl) {
        try {
            runThrows(repl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void runThrows(Repl repl) throws IOException {
        repl.welcome(out);

        StringBuilder currentQueryBuilder = new StringBuilder();
        
        try (Terminal terminal = createTerminal()) {
            LineReader reader = createLineReader(terminal);
            while (true) { // NOSONAR
                String prompt = composePrompt(repl);
                String line;
                try {
                    line = reader.readLine(prompt);
                } catch (UserInterruptException e) {
                    break;
                }

                currentQueryBuilder.append(line);
                String query = currentQueryBuilder.toString();
                if (!repl.isCommandComplete(query)) {
                    currentQueryBuilder.append('\n');
                    repl.prompt2(out);
                    continue;
                }
                currentQueryBuilder = new StringBuilder();
                if (!repl.execute(query, out)) {
                    break;
                }
            }
        }
        
        repl.bye(out);
    }

    private Terminal createTerminal() throws IOException {
        return TerminalBuilder.builder()
                .color(true)
                .jansi(true)
                .system(true)
                .build();
    }

    private LineReader createLineReader(Terminal terminal) {
        DefaultParser parser = new DefaultParser();
        History history = new DefaultHistory();
        return LineReaderBuilder.builder()
                .terminal(terminal)
                .parser(parser)
                .history(history)
                .build();
    }

    private String composePrompt(Repl repl) throws IOException {
        StringBuilder promptBuilder = new StringBuilder();
        AnsiAppendable promptOut = new RichAnsiAppendable(promptBuilder);
        repl.prompt(promptOut);
        return promptBuilder.toString();
    }
    
}
