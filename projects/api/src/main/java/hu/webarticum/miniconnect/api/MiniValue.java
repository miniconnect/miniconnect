package hu.webarticum.miniconnect.api;

public interface MiniValue {

    public MiniValueDefinition definition();

    public boolean isNull();
    
    public MiniContentAccess contentAccess();

}
