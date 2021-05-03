package hu.webarticum.miniconnect.server.util;

public interface Offeror<T> {

    public Listening listen(Listener<T> listener);
    
    public interface Listener<T> {
        
        public boolean accept(T item);
        
    }

    public interface Listening extends AutoCloseable {
        
        public void close();
        
    }
    
}
