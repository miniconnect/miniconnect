package hu.webarticum.miniconnect.util.repl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

// TODO: handle incomplete lines
public class ReplRunner implements Runnable {
    
    private final Repl repl;

    private final InputStream in;
    

    public ReplRunner(Repl repl, InputStream in) {
        this.repl = repl;
        this.in = in;
    }
    
    @Override
    public void run() {
        repl.welcome();
        repl.prompt();

        StringBuilder currentQueryBuilder = new StringBuilder();
        
        Pattern commandPattern = repl.commandPattern();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = readLineSilently(bufferedReader)) != null) { // NOSONAR
            if (currentQueryBuilder.length() == 0 && line.isBlank()) {
                repl.prompt();
                continue;
            }
            
            currentQueryBuilder.append(line);
            String query = currentQueryBuilder.toString();
            if (!commandPattern.matcher(query).matches()) {
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
