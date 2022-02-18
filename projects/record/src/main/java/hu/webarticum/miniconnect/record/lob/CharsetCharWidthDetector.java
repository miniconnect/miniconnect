package hu.webarticum.miniconnect.record.lob;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

public class CharsetCharWidthDetector {

    public static final int DYNAMIC_CHAR_WIDTH = -1;
    
    
    private static final float FLOAT_EPSILON = 0.0001f;

    
    public int detectCharWidth(Charset charset) {
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

    private boolean areVeryClose(float value1, float value2) {
        return Math.abs(value1 - value2) < FLOAT_EPSILON;
    }
    
}
