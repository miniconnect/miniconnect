package hu.webarticum.miniconnect.record.translator;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.ByteString;
import hu.webarticum.miniconnect.lang.ImmutableMap;

public class StringTranslator implements ValueTranslator {

    public static final String NAME = "STRING"; // NOSONAR same name is OK

    public static final String CHARSET_KEY = "charset";
    
    
    private static final StringTranslator UTF8_INSTANCE =
            new StringTranslator(StandardCharsets.UTF_8);
    

    private final Charset charset;
    
    
    private StringTranslator(Charset charset) {
        this.charset = charset;
    }

    public static StringTranslator utf8Instance() {
        return UTF8_INSTANCE;
    }
    
    public static StringTranslator of(Charset charset) {
        if (charset == StandardCharsets.UTF_8) {
            return UTF8_INSTANCE;
        } else {
            return new StringTranslator(charset);
        }
    }
    
    public static StringTranslator of(ImmutableMap<String, ByteString> properties) {
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
            return new StringTranslator(charset);
        }
    }


    @Override
    public String name() {
        return NAME;
    }
    
    @Override
    public int length() {
        return MiniValueDefinition.DYNAMIC_LENGTH;
    }
    
    @Override
    public Object decode(MiniContentAccess contentAccess) {
        return contentAccess.get().toString(charset);
    }

    @Override
    public MiniContentAccess encode(Object value) {
        ByteString bytes = ByteString.of(value.toString(), charset);
        return new StoredContentAccess(bytes);
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
