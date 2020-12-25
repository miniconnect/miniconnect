package hu.webarticum.miniconnect.repl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

// TODO: handle incomplete lines
public class ReplRunner implements Runnable {
    
    private final Repl repl;

    private final InputStream in;

    private final Pattern queryPattern;
    

    public ReplRunner(Repl repl, InputStream in) {
        this(repl, in, null);
    }
    
    public ReplRunner(Repl repl, InputStream in, Pattern queryPattern) {
        this.repl = repl;
        this.in = in;
        this.queryPattern = queryPattern;
    }
    
    @Override
    public void run() {
        repl.welcome();
        repl.prompt();

        StringBuilder currentQueryBuilder = new StringBuilder();
        
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = readLineSilently(bufferedReader)) != null) {
            currentQueryBuilder.append(line);
            String query = currentQueryBuilder.toString();
            if (queryPattern != null && !queryPattern.matcher(query).matches()) {
                currentQueryBuilder.append('\n');
                repl.prompt2();
                continue;
            }
            currentQueryBuilder = new StringBuilder();
            if (!repl.execute(query)) {
                break;
            }
            repl.prompt();
        }
        
        repl.bye();
    }
    
    private String readLineSilently(BufferedReader bufferedReader) {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace(); // FIXME
            return null;
        }
    }
    
}
