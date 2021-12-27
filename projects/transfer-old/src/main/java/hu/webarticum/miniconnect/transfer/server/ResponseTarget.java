package hu.webarticum.miniconnect.transfer.server;

import java.io.IOException;

@FunctionalInterface
public interface ResponseTarget<R> {

    public void send(R response) throws IOException;
    
}
