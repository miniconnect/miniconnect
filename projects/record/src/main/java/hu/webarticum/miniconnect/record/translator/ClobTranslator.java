package hu.webarticum.miniconnect.record.translator;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.record.lob.ClobValue;
import hu.webarticum.miniconnect.record.type.StandardValueType;

public class ClobTranslator implements ValueTranslator {

    private static final String NAME = StandardValueType.CLOB.name();

    private static final String CHARSET_KEY = "charset";
    
    private static final ClobTranslator UTF8_INSTANCE =
            new ClobTranslator(StandardCharsets.UTF_8);
    

    private final Charset charset;
    
    
    private ClobTranslator(Charset charset) {
        this.charset = charset;
    }

    public static ClobTranslator of(Charset charset) {
        return new ClobTranslator(charset);
    }
    
    public static ClobTranslator of(ImmutableMap<String, ByteString> properties) {
        ByteString charsetValue = properties.get(CHARSET_KEY);
        Charset charset;
        if (charsetValue != null) {
            charset = Charset.forName(charsetValue.toString());
        } else {
            charset = StandardCharsets.UTF_8;
        }
        if (charset == StandardCharsets.UTF_8) {
            return UTF8_INSTANCE;
        } else {
            return new ClobTranslator(charset);
        }
    }
    

    @Override
    public String name() {
        return NAME;
    }
    
    @Override
    public int length() {
        return MiniValueDefinition.DYNAMIC_SIZE;
    }

    @Override
    public Object decode(MiniContentAccess contentAccess) {
        return ClobValue.of(contentAccess, charset);
    }

    @Override
    public MiniContentAccess encode(Object value) {
        ClobValue clobValue = (ClobValue) value;
        return clobValue.contentAccess();
    }

    @Override
    public ImmutableMap<String, ByteString> properties() {
        if (charset == StandardCharsets.UTF_8) {
            return ImmutableMap.empty();
        } else {
            return ImmutableMap.of(CHARSET_KEY, ByteString.of(charset.name()));
        }
    }
    
}
