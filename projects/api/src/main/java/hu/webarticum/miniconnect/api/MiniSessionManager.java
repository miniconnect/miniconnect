package hu.webarticum.miniconnect.api;

@FunctionalInterface
public interface MiniSessionManager {

    public MiniSession openSession();
    
}
