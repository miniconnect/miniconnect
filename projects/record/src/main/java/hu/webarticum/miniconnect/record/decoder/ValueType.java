package hu.webarticum.miniconnect.record.decoder;

public interface ValueType {

    public Class<?> clazz();
    
    public ValueDecoder valueDecoder();
    
}
