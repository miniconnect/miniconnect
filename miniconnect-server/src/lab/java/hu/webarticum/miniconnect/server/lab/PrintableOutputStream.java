package hu.webarticum.miniconnect.server.lab;

import java.io.IOException;
import java.io.OutputStream;

public class PrintableOutputStream extends OutputStream { // NOSONAR
    
    private final OutputStream target;
    
    private final int inputLineWidth;
    
    
    private int position = 0;
    
    
    public PrintableOutputStream(OutputStream target, int inputLineWidth) {
        this.target = target;
        this.inputLineWidth = inputLineWidth;
    }
    
    
    @Override
    public void write(int b) throws IOException {
        if (b >= 32 && b < 127 && b != 92) {
            target.write((byte) ' ');
            target.write(b);
            target.write((byte) ' ');
        } else {
            String byteStr = String.format("\\%02x", b);
            target.write(byteStr.getBytes());
        }
        
        if (position % inputLineWidth == inputLineWidth - 1) {
            target.write((byte) '\n');
            position = 0;
        } else {
            position++;
        }
    }
    
}
