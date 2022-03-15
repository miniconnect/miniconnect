package hu.webarticum.miniconnect.rest.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hu.webarticum.miniconnect.api.MiniColumnHeader;
import hu.webarticum.miniconnect.api.MiniResult;
import hu.webarticum.miniconnect.api.MiniSession;
import hu.webarticum.miniconnect.api.MiniValue;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.impl.result.StoredColumnHeader;
import hu.webarticum.miniconnect.impl.result.StoredResult;
import hu.webarticum.miniconnect.impl.result.StoredResultSetData;
import hu.webarticum.miniconnect.impl.result.StoredValue;
import hu.webarticum.miniconnect.impl.result.StoredValueDefinition;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.record.type.StandardValueType;

public class DefaultEntityListQueryExecutor implements EntityListQueryExecutor {
    
    private final MiniSession session;
    

    public DefaultEntityListQueryExecutor(MiniSession session) {
        this.session = session;
    }
    
    
    public MiniResult execute(EntityListQuery query) {
        
        // TODO
        
        MiniValueDefinition intDefinition = new StoredValueDefinition(
                StandardValueType.INT.name());
        MiniValueDefinition stringDefinition = new StoredValueDefinition(
                StandardValueType.STRING.name());
        List<MiniColumnHeader> columnHeaders = new ArrayList<>();
        columnHeaders.add(new StoredColumnHeader("id", false, intDefinition));
        columnHeaders.add(new StoredColumnHeader("label", false, stringDefinition));
        columnHeaders.add(new StoredColumnHeader("description", false, stringDefinition));
        List<List<MiniValue>> rows = new ArrayList<>();
        rows.add(Arrays.asList(
                asMiniValue(1),
                asMiniValue("first"),
                asMiniValue("hello world")));
        rows.add(Arrays.asList(
                asMiniValue(2),
                asMiniValue("second"),
                asMiniValue("lorem ipsum")));
        rows.add(Arrays.asList(
                asMiniValue(3),
                asMiniValue("third"),
                asMiniValue("xxx yyy")));
        return new StoredResult(new StoredResultSetData(columnHeaders, rows));
        
    }

    private MiniValue asMiniValue(Object value) {
        return new StoredValue(asByteString(value));
    }
    
    private ByteString asByteString(Object value) {
        if (value instanceof Integer) {
            return ByteString.ofInt((int) value);
        } else if (value instanceof String) {
            return ByteString.of((String) value);
        } else {
            return ByteString.empty();
        }
    }

}
