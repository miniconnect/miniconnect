package hu.webarticum.miniconnect.protocol.message;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.protocol.common.ByteString;
import hu.webarticum.miniconnect.protocol.util.ByteUtil;

// TODO: query id?
public class SqlRequest implements SessionRequest {

    private static final Type TYPE = Request.Type.SQL;
    
    private static final Charset CHARSET = StandardCharsets.UTF_8;


    private final int sessionId;
    
    private final String sql;

    
    public SqlRequest(int sessionId, String sql) {
        this.sessionId = sessionId;
        this.sql = sql;
    }
    
    static SqlRequest decode(ByteString content) {
        int sessionId = ByteUtil.bytesToInt(content.extract(1, 4));
        String sql = new String(content.extract(5), CHARSET);
        
        return new SqlRequest(sessionId, sql);
    }
    
    
    @Override
    public Type type() {
        return TYPE;
    }

    @Override
    public int sessionId() {
        return sessionId;
    }

    @Override
    public ByteString encode() {
        byte[] sqlBytes = sql.getBytes(CHARSET);
        
        byte[] contentBytes = new byte[sqlBytes.length + 5];
        
        contentBytes[0] = TYPE.flag();
        
        byte[] sessionIdBytes = ByteUtil.intToBytes(sessionId);
        System.arraycopy(sessionIdBytes, 0, contentBytes, 1, sessionIdBytes.length);

        System.arraycopy(sqlBytes, 0, contentBytes, 5, sqlBytes.length);
        
        return ByteString.wrap(contentBytes);
    }
    
    public String sql() {
        return sql;
    }

}
