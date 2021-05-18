package hu.webarticum.miniconnect.server.server;

import java.util.function.Consumer;

public interface Server<Q, R> {

    public void accept(Q request, Consumer<R> responseConsumer);
    
}
