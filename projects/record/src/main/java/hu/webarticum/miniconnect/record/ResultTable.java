package hu.webarticum.miniconnect.record;

import java.util.Iterator;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.record.converter.Converter;
import hu.webarticum.miniconnect.record.converter.DefaultConverter;
import hu.webarticum.miniconnect.record.translator.JavaTranslator;
import hu.webarticum.miniconnect.record.translator.ValueTranslator;
import hu.webarticum.miniconnect.record.type.StandardValueType;
import hu.webarticum.miniconnect.record.type.ValueType;

public class ResultTable implements Iterable<ResultRecord> {
    
    private final MiniResultSet resultSet;
    
    private final ImmutableList<ValueTranslator> valueTranslators;
    
    private final Converter converter;
    

    public ResultTable(MiniResultSet resultSet) {
        this(
                resultSet,
                resultSet.columnHeaders().map(ResultTable::defaultTranslatorFor),
                new DefaultConverter());
    }
    
    public ResultTable(
            MiniResultSet resultSet,
            ImmutableList<ValueTranslator> valueTranslators,
            Converter converter) {
        this.resultSet = resultSet;
        this.valueTranslators = valueTranslators;
        this.converter = converter;
    }
    
    private static ValueTranslator defaultTranslatorFor(MiniColumnHeader columnHeader) {
        MiniValueDefinition valueDefinition = columnHeader.valueDefinition();
        String typeName = valueDefinition.type();
        if (typeName.equals(JavaTranslator.NAME)) {
            return JavaTranslator.unboundInstance();
        }
        ValueType valueType = StandardValueType.valueOf(typeName);
        return valueType.translatorFor(valueDefinition.properties());
    }
    

    public ImmutableList<ValueTranslator> valueTranslators() {
        return valueTranslators;
    }

    public MiniResultSet resultSet() {
        return resultSet;
    }

    @Override
    public Iterator<ResultRecord> iterator() {
        return new ResultTableIterator();
    }
    
    
    private class ResultTableIterator implements Iterator<ResultRecord> {
        
        private final Iterator<ImmutableList<MiniValue>> rowIterator = resultSet.iterator();
        
        
        @Override
        public boolean hasNext() {
            return rowIterator.hasNext();
        }

        @Override
        public ResultRecord next() {
            return new ResultRecord(
                    resultSet.columnHeaders(),
                    rowIterator.next(),
                    valueTranslators,
                    converter);
        }
        
    }
    
}
