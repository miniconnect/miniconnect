package hu.webarticum.miniconnect.record;

import java.util.Iterator;
import java.util.NoSuchElementException;

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

        private boolean nextRowFetched = false;

        private ImmutableList<MiniValue> nextRow = null;


        @Override
        public boolean hasNext() {
            if (!nextRowFetched) {
                fetchNextRow();
            }
            
            return nextRow != null;
        }

        @Override
        public ResultRecord next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            
            ResultRecord result = buildResultRecord();
            invalidate();
            return result;
        }

        private void fetchNextRow() {
            nextRow = resultSet.fetch();
            nextRowFetched = true;
        }
        
        private ResultRecord buildResultRecord() {
            return new ResultRecord(resultSet.columnHeaders(), nextRow, valueTranslators, converter);
        }
        
        private void invalidate() {
            nextRow = null;
            nextRowFetched = false;
        }
        
    }
    
}
