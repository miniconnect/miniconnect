package hu.webarticum.miniconnect.server.surface;

import java.util.function.Predicate;

public interface RequestSender<Q, R> {

    public void send(Q request);

    public CloseableSource<R> send(Q request, Predicate<R> filter);
    
}
