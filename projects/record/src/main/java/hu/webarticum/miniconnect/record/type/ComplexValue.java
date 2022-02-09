package hu.webarticum.miniconnect.record.type;

public class ComplexValue {
    
    private final Object value;
    
    
    public ComplexValue(Object value) {
        this.value = value;
    }
    

    public Object get() {
        return value;
    }
    
}
