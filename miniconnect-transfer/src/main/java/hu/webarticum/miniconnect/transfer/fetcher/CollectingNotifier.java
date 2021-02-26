package hu.webarticum.miniconnect.transfer.fetcher;

import hu.webarticum.miniconnect.transfer.fetcher.pocket.Pocket;

public interface CollectingNotifier<T> {

    public Pocket<T> pocket();

    public void remove();

}
