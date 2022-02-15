package hu.webarticum.miniconnect.record.translator;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableMap;
import hu.webarticum.miniconnect.record.lob.ClobValue;

public class ClobTranslator implements ValueTranslator {
    
    private static final String CHARSET_KEY = "charset";
    
    private static final ClobTranslator UTF8_INSTANCE =
            new ClobTranslator(StandardCharsets.UTF_8);
    

    private final Charset charset;
    
    
    private ClobTranslator(Charset charset) {
        this.charset = charset;
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
    public int length() {
        return MiniValueDefinition.DYNAMIC_SIZE;
    }

    @Override
    public Object decode(MiniContentAccess contentAccess) {
        return new ClobValue(contentAccess, charset);
    }

    @Override
    public MiniContentAccess encode(Object value) {
        ClobValue clobValue = (ClobValue) value;
        return clobValue.contentAccess();
    }
    
}
