package hu.webarticum.miniconnect.record.lob;

import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.api.MiniContentAccess;

public interface ClobValue {
    
    public static ClobValue of(MiniContentAccess contentAccess) {
        return of(
                contentAccess,
                StandardCharsets.UTF_8,
                CharsetCharWidthDetector.DYNAMIC_CHAR_WIDTH);
    }
    
    public static ClobValue of(MiniContentAccess contentAccess, Charset charset) {
        return of(contentAccess, charset, new CharsetCharWidthDetector().detectCharWidth(charset));
    }

    public static ClobValue of(MiniContentAccess contentAccess, Charset charset, int charWidth) {
        if (charWidth == CharsetCharWidthDetector.DYNAMIC_CHAR_WIDTH) {
            return new DynamicCharWidthClobValue(contentAccess, charset);
        } else {
            return new FixedCharWidthClobValue(contentAccess, charset, charWidth);
        }
    }

    
    public MiniContentAccess contentAccess();

    public Charset charset();

    public long length();
    
    public String get(long start, int length);

    public Reader reader();

    public Reader reader(long start, long length);

    public default BlobValue toBlob() {
        return BlobValue.of(contentAccess());
    }

}
