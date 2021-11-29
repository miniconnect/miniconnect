package hu.webarticum.miniconnect.transfer.channel.lazysinglestream;

import java.io.IOException;
import java.io.OutputStream;

public interface OutputStreamFactory {

    public OutputStream open() throws IOException;
    
}
