package hu.webarticum.miniconnect.transfer.fetcher.pocket;

public interface Pocket<T> {

    public enum AcceptStatus { DECLINED, ACCEPTED, COMPLETED  }
    
    
    public AcceptStatus accept(T item);
    
}
