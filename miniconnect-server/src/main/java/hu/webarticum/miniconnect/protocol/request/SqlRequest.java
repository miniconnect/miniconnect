package hu.webarticum.miniconnect.protocol.request;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.protocol.common.ByteString;

// TODO: id?
public class SqlRequest implements Request {

    private static final Type TYPE = Request.Type.SQL;
    
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    
    
    private final String sql;
    

    private SqlRequest(ByteString content) {
        this(new String(content.extract(1), CHARSET));
    }
    
    public SqlRequest(String sql) {
        this.sql = sql;
    }
    
    static SqlRequest decode(ByteString content) {
        return new SqlRequest(content);
    }
    
    
    @Override
    public Type type() {
        return TYPE;
    }

    @Override
    public ByteString encode() {
        byte[] sqlBytes = sql.getBytes(CHARSET);
        byte[] contentBytes = new byte[sqlBytes.length + 1];
        contentBytes[0] = TYPE.flag();
        System.arraycopy(sqlBytes, 0, contentBytes, 1, sqlBytes.length);
        return ByteString.wrap(contentBytes);
    }
    
    public String sql() {
        return sql;
    }

}
