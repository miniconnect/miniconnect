package hu.webarticum.miniconnect.record.converter;

public class UnsupportedConversionException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;
    

    private final Object source;
    
    private final Class<?> targetClazz;
    
    
    public UnsupportedConversionException(Object source, Class<?> targetClazz) {
        this(
                "Unsupported conversion to " + targetClazz + " from value " + source,
                source,
                targetClazz);
    }
    
    public UnsupportedConversionException(
            String message, Object source, Class<?> targetClazz) {
        super(message);
        this.source = source;
        this.targetClazz = targetClazz;
    }

    public UnsupportedConversionException(
            String message, Object source, Class<?> targetClazz, Exception cause) {
        super(message, cause);
        this.source = source;
        this.targetClazz = targetClazz;
    }
    

    public Object source() {
        return source;
    }

    public Class<?> targetClazz() {
        return targetClazz;
    }
    
}
