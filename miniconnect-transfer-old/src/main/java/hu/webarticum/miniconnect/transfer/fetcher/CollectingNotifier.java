package hu.webarticum.miniconnect.transfer.fetcher;

import java.io.InterruptedIOException;

import hu.webarticum.miniconnect.transfer.fetcher.pocket.Pocket;

public interface CollectingNotifier<T, U> {

    public Pocket<T, U> pocket();
    
    public U await() throws InterruptedIOException;

    public void remove();

}
