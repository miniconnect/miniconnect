package hu.webarticum.miniconnect.server.lab;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class HexOutputStream extends OutputStream { // NOSONAR
    
    private final PrintStream out;
    
    public HexOutputStream(PrintStream out) {
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        out.println((b < 16 ? "0" : "") + Integer.toString(b, 16));
    }

}
