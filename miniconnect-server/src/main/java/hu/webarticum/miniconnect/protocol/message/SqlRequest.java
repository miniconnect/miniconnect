package hu.webarticum.miniconnect.protocol.message;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.transfer.util.ByteString;
import hu.webarticum.miniconnect.transfer.util.ByteUtil;

public class SqlRequest implements SessionRequest {

    private static final Type TYPE = Request.Type.SQL;
    
    private static final Charset CHARSET = StandardCharsets.UTF_8;


    private final int sessionId;

    private final int queryId;
    
    private final String sql;

    
    public SqlRequest(int sessionId, int queryId, String sql) {
        this.sessionId = sessionId;
        this.queryId = queryId;
        this.sql = sql;
    }
    
    static SqlRequest decode(ByteString content) {
        ByteString.Reader reader = content.reader().skip(1);
        
        int sessionId = ByteUtil.bytesToInt(reader.read(4));
        int queryId = ByteUtil.bytesToInt(reader.read(4));
        String sql = new String(reader.readRemaining(), CHARSET);
        
        return new SqlRequest(sessionId, queryId, sql);
    }
    
    
    @Override
    public Type type() {
        return TYPE;
    }

    @Override
    public int sessionId() {
        return sessionId;
    }

    public int queryId() {
        return queryId;
    }

    @Override
    public ByteString encode() {
        return ByteString.builder()
                .append(TYPE.flag())
                .append(ByteUtil.intToBytes(sessionId))
                .append(ByteUtil.intToBytes(queryId))
                .append(sql.getBytes(CHARSET))
                .build();
    }
    
    public String sql() {
        return sql;
    }

}
