package hu.webarticum.miniconnect.repl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = readLineSilently(bufferedReader)) != null) {
            if (!repl.execute(line)) {
                break;
            }
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
