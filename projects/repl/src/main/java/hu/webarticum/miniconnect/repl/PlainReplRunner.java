package hu.webarticum.miniconnect.repl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PlainReplRunner implements ReplRunner {

    private final InputStream in;
    
    private final AnsiAppendable out;


    public PlainReplRunner(InputStream in, Appendable out) {
        this.in = in;
        this.out = new PlainAnsiAppendable(out);
    }

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
        repl.prompt(out);

        StringBuilder currentQueryBuilder = new StringBuilder();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = bufferedReader.readLine()) != null) { // NOSONAR
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
            repl.prompt(out);
        }

        repl.bye(out);
    }

}
