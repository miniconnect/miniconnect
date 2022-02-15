package hu.webarticum.miniconnect.record.custom;

public class CustomValue {
    
    private final Object value;
    
    
    public CustomValue(Object value) {
        this.value = value;
    }
    

    public Object get() {
        return value;
    }
    
    // TODO: toJsonString() etc.
    
}
