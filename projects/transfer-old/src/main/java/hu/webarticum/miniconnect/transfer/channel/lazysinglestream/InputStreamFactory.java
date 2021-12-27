package hu.webarticum.miniconnect.transfer.channel.lazysinglestream;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamFactory {

    public InputStream open() throws IOException;
    
}
