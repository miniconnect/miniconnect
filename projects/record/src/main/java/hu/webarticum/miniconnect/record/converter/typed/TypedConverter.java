package hu.webarticum.miniconnect.record.converter.typed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public interface TypedConverter<T> {

    public Class<T> targetClazz();
    
    public T convert(Object source);
    
    
    public static Collection<TypedConverter<?>> defaultConverters() { // NOSONAR wildcard is OK
        return new ArrayList<>(Arrays.asList(new TypedConverter<?>[] { // NOSONAR for trailing comma
                new ToNullConverter(),
                new ToBooleanConverter(),
                new ToByteConverter(),
                new ToCharacterConverter(),
                new ToShortConverter(),
                }));
    }
    
}
