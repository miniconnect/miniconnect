package hu.webarticum.miniconnect.api;

// FIXME: rename to MiniValueInterpreter?
// TODO: encode to bytes instead of MiniValue
//       - ByteString encode(Object value)
//       - void encodeTo(Object value, OutputStream out)
//       - Object decode(ByteString value)
//       - Object decodeFrom(InputStream in)
public interface MiniValueEncoder {

    public MiniColumnHeader headerFor(String columnName);
    
    public MiniValue encode(Object value);
    
    public Object decode(MiniValue value);
    
}
