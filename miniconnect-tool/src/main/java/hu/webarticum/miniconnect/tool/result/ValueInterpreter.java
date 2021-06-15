package hu.webarticum.miniconnect.tool.result;

import hu.webarticum.miniconnect.api.MiniValue;

// FIXME: rename to MiniValueInterpreter?
// TODO: encode to bytes instead of MiniValue
//       - ByteString encode(Object value)
//       - void encodeTo(Object value, OutputStream out)
//       - Object decode(ByteString value)
//       - Object decodeFrom(InputStream in)
public interface ValueInterpreter {

    public MiniValue encode(Object value);
    
    public Object decode(MiniValue value);
    
}
