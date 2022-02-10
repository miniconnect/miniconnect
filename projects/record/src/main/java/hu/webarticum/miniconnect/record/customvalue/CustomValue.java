package hu.webarticum.miniconnect.record.customvalue;

public class CustomValue {
    
    private final Object value;
    
    
    public CustomValue(Object value) {
        this.value = value;
    }
    

    public Object get() {
        return value;
    }
    
}
