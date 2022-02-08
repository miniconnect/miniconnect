package hu.webarticum.miniconnect.record;

import java.util.Iterator;

import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.lang.ImmutableList;
import hu.webarticum.miniconnect.record.converter.Converter;
import hu.webarticum.miniconnect.record.decoder.ValueDecoder;

public class ResultTable implements Iterable<ResultRecord> {
    
    private final MiniResultSet resultSet;
    
    private final ImmutableList<ValueDecoder> valueDecoders;
    
    private final Converter converter;
    

    public ResultTable(
            MiniResultSet resultSet,
            ImmutableList<ValueDecoder> valueDecoders,
            Converter converter) {
        this.resultSet = resultSet;
        this.valueDecoders = valueDecoders;
        this.converter = converter;
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
                    valueDecoders,
                    converter);
        }
        
    }
    
}
