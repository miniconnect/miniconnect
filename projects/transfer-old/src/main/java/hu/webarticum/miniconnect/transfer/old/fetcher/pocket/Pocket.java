package hu.webarticum.miniconnect.transfer.old.fetcher.pocket;

public interface Pocket<T, U> {

    public enum AcceptStatus { DECLINED, ACCEPTED, COMPLETED  }
    
    
    public AcceptStatus accept(T item);
    
    public boolean hasMore();
    
    public U get();
    
}
