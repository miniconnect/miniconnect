package hu.webarticum.miniconnect.rdmsframework.util;

import java.util.Optional;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.impl.result.StoredColumnHeader;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.impl.result.StoredResultSetData;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.record.translator.JavaTranslator;
import hu.webarticum.miniconnect.record.translator.ValueTranslator;
import hu.webarticum.miniconnect.record.type.StandardValueType;

public final class ResultUtil {
    
    private ResultUtil() {
        // utility class
    }
    
    
    public static MiniResult createSingleValueResult(String columnName, Object content) {
        Class<?> clazz = content == null ? String.class : content.getClass();
        ValueTranslator translator = createValueTranslatorFor(clazz);
        MiniValueDefinition columnDefinition = translator.definition();
        MiniColumnHeader columnHeader = new StoredColumnHeader(columnName, false, columnDefinition);
        MiniValue value = translator.encodeFully(content);
        return new StoredResult(new StoredResultSetData(
                ImmutableList.of(columnHeader),
                ImmutableList.of(ImmutableList.of(value))));
    }
    
    // FIXME: custom translators?
    public static ValueTranslator createValueTranslatorFor(Class<?> clazz) {
        Optional<StandardValueType> valueTypeOptional = StandardValueType.forClazz(String.class);
        if (valueTypeOptional.isPresent()) {
            return valueTypeOptional.get().defaultTranslator();
        } else {
            return JavaTranslator.of(clazz);
        }
    }

}
