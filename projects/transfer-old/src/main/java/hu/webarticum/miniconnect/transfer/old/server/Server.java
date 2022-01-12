package hu.webarticum.miniconnect.transfer.old.server;

@FunctionalInterface
public interface Server<Q, R> {

    public void accept(Q request, ResponseTarget<R> responseTarget);
    
}
