package hu.webarticum.miniconnect.record.converter.typed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import hu.webarticum.miniconnect.record.converter.typed.extra.ToByteArrayConverter;
import hu.webarticum.miniconnect.record.converter.typed.extra.ToCharArrayConverter;
import hu.webarticum.miniconnect.record.converter.typed.extra.ToDateConverter;
import hu.webarticum.miniconnect.record.converter.typed.extra.ToInputStreamConverter;
import hu.webarticum.miniconnect.record.converter.typed.extra.ToReaderConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToBigDecimalConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToBigIntegerConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToBlobValueConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToBooleanConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToByteConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToByteStringConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToCharacterConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToClobValueConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToCustomValueConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToDoubleConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToFloatConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToInstantConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToIntegerConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToLargeIntegerConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToLocalDateConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToLocalDateTimeConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToLocalTimeConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToLongConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToNullConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToOffsetDateTimeConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToOffsetTimeConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToShortConverter;
import hu.webarticum.miniconnect.record.converter.typed.standard.ToStringConverter;

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
                new ToIntegerConverter(),
                new ToLongConverter(),
                new ToFloatConverter(),
                new ToDoubleConverter(),
                new ToLargeIntegerConverter(),
                new ToBigIntegerConverter(),
                new ToBigDecimalConverter(),
                new ToByteStringConverter(),
                new ToStringConverter(),
                new ToLocalTimeConverter(),
                new ToOffsetTimeConverter(),
                new ToLocalDateConverter(),
                new ToLocalDateTimeConverter(),
                new ToOffsetDateTimeConverter(),
                new ToInstantConverter(),
                new ToBlobValueConverter(),
                new ToClobValueConverter(),
                new ToCustomValueConverter(),
                new ToByteArrayConverter(),
                new ToCharArrayConverter(),
                new ToDateConverter(),
                new ToInputStreamConverter(),
                new ToReaderConverter(),
                }));
    }
    
}
