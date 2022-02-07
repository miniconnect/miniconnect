package hu.webarticum.miniconnect.record;

import java.util.Iterator;

import hu.webarticum.miniconnect.api.MiniResultSet;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.lang.ImmutableList;

public class ResultTable implements Iterable<ResultRecord> {
    
    private final MiniResultSet resultSet;
    
    private final ResultRecord.ValueInterpreterSupplier valueInterpreterSupplier;
    

    public ResultTable(MiniResultSet resultSet) {
        this(resultSet, new Object());
    }

    public ResultTable(MiniResultSet resultSet, Object valueInterpreter) {
        this(resultSet, (i, h) -> valueInterpreter);
    }

    public ResultTable(
            MiniResultSet resultSet,
            ResultRecord.ValueInterpreterSupplier valueInterpreterSupplier) {
        this.resultSet = resultSet;
        this.valueInterpreterSupplier = valueInterpreterSupplier;
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
                    resultSet.columnHeaders(), rowIterator.next(), valueInterpreterSupplier);
        }
        
    }
    
}
