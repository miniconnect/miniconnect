package hu.webarticum.miniconnect.record.translator;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.api.MiniValueDefinition;
import hu.webarticum.miniconnect.impl.result.StoredContentAccess;
import hu.webarticum.miniconnect.lang.BitString;
import hu.webarticum.miniconnect.lang.ByteString;

public class BitTranslator implements ValueTranslator {

    public static final String NAME = "BIT"; // NOSONAR same name is OK


    private static final BitTranslator INSTANCE = new BitTranslator();


    private BitTranslator() {
        // singleton
    }

    public static BitTranslator instance() {
        return INSTANCE;
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
        ByteString.Reader reader = contentAccess.get().reader();
        int length = reader.readInt();
        int wordCount = (length + 63) >>> 6;
        long[] words = new long[wordCount];
        for (int i = 0; i < wordCount; i++) {
            words[i] = reader.readLong();
        }
        return BitString.of(words, length);
    }

    @Override
    public MiniContentAccess encode(Object value) {
        BitString bitStringValue = (BitString) value;
        ByteString.Builder builder = ByteString.builder();
        builder.appendInt(bitStringValue.length());
        for (long word : bitStringValue.data()) {
            builder.appendLong(word);
        }
        return StoredContentAccess.of(builder.build());
    }

    @Override
    public String assuredClazzName() {
        return BitString.class.getName();
    }

}
