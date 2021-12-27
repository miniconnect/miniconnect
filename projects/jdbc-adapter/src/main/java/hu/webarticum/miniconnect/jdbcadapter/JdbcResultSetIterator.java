package hu.webarticum.miniconnect.jdbcadapter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator wrapper for JDBC {@link ResultSet} instances.
 * 
 * It is partially thread safe:
 * another thread can continue the abandoned iteration
 * (if the wrapped {@link ResultSet} instance is thread safe).
 */
public class JdbcResultSetIterator<T> implements Iterator<T> {
    
    private final ResultSet resultSet;
    
    private final Mapper<T> mapper;
    
    private boolean wasNextFetched = false;
    
    private boolean hasNext;
    

    public JdbcResultSetIterator(ResultSet resultSet, Mapper<T> mapper) {
        this.resultSet = resultSet;
        this.mapper = mapper;
    }
    
    
    @Override
    public boolean hasNext() {
        if (!wasNextFetched) {
            hasNext = fetchResultSet();
            wasNextFetched = true;
        }
        
        return hasNext;
    }
    
    @Override
    public T next() { // NOSONAR NoSuchElementException is supported
        try {
            return extractNext();
        } finally {
            wasNextFetched = false;
        }
    }

    private T extractNext() {
        if (!wasNextFetched) {
            return extractNextWhenNotFetched();
        } else if (hasNext) {
            return extractRow();
        } else {
            throw new NoSuchElementException();
        }
    }
    
    private T extractNextWhenNotFetched() {
        if (fetchResultSet()) {
            return extractRow();
        } else {
            throw new NoSuchElementException();
        }
    }

    private boolean fetchResultSet() {
        try {
            return resultSet.next();
        } catch (SQLException e) {
            throw new UncheckedSqlException(e);
        }
    }
    
    private T extractRow() {
        try {
            return mapper.map(resultSet);
        } catch (Exception e) {
            throw new UncheckedIOException(new IOException("Row mapping failed", e));
        }
    }
    
    
    public interface Mapper<T> {
        
        public T map(ResultSet resultSet) throws Exception; // NOSONAR
        
    }
    
}
