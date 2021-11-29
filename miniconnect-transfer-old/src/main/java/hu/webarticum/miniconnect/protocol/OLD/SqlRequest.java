package hu.webarticum.miniconnect.protocol.OLD;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.transfer.util.ByteUtil;
import hu.webarticum.miniconnect.util.data.ByteString;

public class SqlRequest implements SessionRequest {

    private static final Type TYPE = Request.Type.SQL;
    
    private static final Charset CHARSET = StandardCharsets.UTF_8;


    private final int sessionId;

    private final int exchangeId;
    
    private final String sql;

    
    public SqlRequest(int sessionId, int exchangeId, String sql) {
        this.sessionId = sessionId;
        this.exchangeId = exchangeId;
        this.sql = sql;
    }
    
    static SqlRequest decode(ByteString content) {
        ByteString.Reader reader = content.reader().skip(1);
        
        int sessionId = ByteUtil.bytesToInt(reader.read(4));
        int exchangeId = ByteUtil.bytesToInt(reader.read(4));
        String sql = new String(reader.readRemaining(), CHARSET);
        
        return new SqlRequest(sessionId, exchangeId, sql);
    }
    
    
    @Override
    public Type type() {
        return TYPE;
    }

    @Override
    public int sessionId() {
        return sessionId;
    }

    public int exchangeId() {
        return exchangeId;
    }

    @Override
    public ByteString encode() {
        return ByteString.builder()
                .append(TYPE.flag())
                .append(ByteUtil.intToBytes(sessionId))
                .append(ByteUtil.intToBytes(exchangeId))
                .append(sql.getBytes(CHARSET))
                .build();
    }
    
    public String sql() {
        return sql;
    }

}
