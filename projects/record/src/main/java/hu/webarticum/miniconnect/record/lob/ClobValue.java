package hu.webarticum.miniconnect.record.lob;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

import hu.webarticum.miniconnect.api.MiniContentAccess;

public class ClobValue {
    
    public static final int DYNAMIC_CHAR_WIDTH = -1;
    

    private static final long UNSPECIFIED_LENGTH = -1;

    private static final float FLOAT_EPSILON = 0.0001f;
    
    
    private final MiniContentAccess contentAccess;
    
    private final Charset charset;
    
    private final int charWidth;
    
    private volatile long cachedLength = UNSPECIFIED_LENGTH;
    

    public ClobValue(MiniContentAccess contentAccess) {
        this(contentAccess, StandardCharsets.UTF_8, DYNAMIC_CHAR_WIDTH);
    }
    
    public ClobValue(MiniContentAccess contentAccess, Charset charset) {
        this(contentAccess, charset, detectCharWidth(charset));
    }

    public ClobValue(MiniContentAccess contentAccess, Charset charset, int charWidth) {
        if (charWidth <= 0 && charWidth != DYNAMIC_CHAR_WIDTH) {
            throw new IllegalArgumentException("Invalid character width: " + charWidth);
        }
        
        this.contentAccess = contentAccess;
        this.charset = charset;
        this.charWidth = charWidth;
    }
    
    // FIXME
    private static final int detectCharWidth(Charset charset) {
        if (charset == StandardCharsets.US_ASCII) {
            return 1;
        } else if (charset == StandardCharsets.ISO_8859_1) {
            return 1;
        } else if (charset == StandardCharsets.UTF_16BE) {
            return 2;
        } else if (charset == StandardCharsets.UTF_16LE) {
            return 2;
        } else if (charset == StandardCharsets.UTF_8) {
            return DYNAMIC_CHAR_WIDTH;
        } else if (charset == StandardCharsets.UTF_16) {
            return DYNAMIC_CHAR_WIDTH;
        }
        
        CharsetEncoder encoder = charset.newEncoder();
        float maxBytes = encoder.maxBytesPerChar();
        int roundedMaxBytes = (int) maxBytes;
        if (!areVeryClose(roundedMaxBytes, maxBytes)) {
            return DYNAMIC_CHAR_WIDTH;
        }
        
        float averageBytes = encoder.averageBytesPerChar();
        if (areVeryClose(averageBytes, roundedMaxBytes)) {
            return roundedMaxBytes;
        }
        
        return DYNAMIC_CHAR_WIDTH;
    }
    
    private static boolean areVeryClose(float value1, float value2) {
        return Math.abs(value1 - value2) < FLOAT_EPSILON;
    }
    
    
    public MiniContentAccess contentAccess() {
        return contentAccess;
    }

    public Charset charset() {
        return charset;
    }

    public long length() {
        if (cachedLength == UNSPECIFIED_LENGTH) {
            cachedLength = calculateLength();
        }
        
        return cachedLength;
    }
    
    public long calculateLength() {
        if (charWidth == DYNAMIC_CHAR_WIDTH) {
            return calculateLengthWhenDynamicCharWidth();
        } else {
            return calculateLengthWhenFixedCharWidth();
        }
    }
    
    private long calculateLengthWhenDynamicCharWidth() {

        // TODO
        return 0L;
        
    }

    private long calculateLengthWhenFixedCharWidth() {
        return contentAccess.length() / charWidth;
    }

    public String get(long start, int length) {
        if (charWidth == DYNAMIC_CHAR_WIDTH) {
            return getWhenDynamicCharWidth(start, length);
        } else {
            return getWhenFixedCharWidth(start, length);
        }
    }

    private String getWhenDynamicCharWidth(long start, int length) {

        // TODO
        return null;
        
    }

    private String getWhenFixedCharWidth(long start, int length) {
        return contentAccess.get(start * charWidth, length * charWidth).toString(charset);
    }

    public Reader reader() {
        return new InputStreamReader(contentAccess.inputStream(), charset);
    }

    public Reader reader(long start, long length) {
        if (charWidth == DYNAMIC_CHAR_WIDTH) {
            return readerWhenDynamicCharWidth(start, length);
        } else {
            return readerWhenFixedCharWidth(start, length);
        }
    }

    private Reader readerWhenDynamicCharWidth(long start, long length) {

        // TODO
        return null;
        
    }

    private Reader readerWhenFixedCharWidth(long start, long length) {
        InputStream inputStream = contentAccess.inputStream(start * charWidth, length * charWidth);
        return new InputStreamReader(inputStream, charset);
    }

    public BlobValue toBlob() {
        return new BlobValue(contentAccess);
    }

}
