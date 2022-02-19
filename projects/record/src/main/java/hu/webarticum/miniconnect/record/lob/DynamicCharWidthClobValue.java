package hu.webarticum.miniconnect.record.lob;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.SortedMap;
import java.util.TreeMap;

import hu.webarticum.miniconnect.api.MiniContentAccess;

public class DynamicCharWidthClobValue implements ClobValue {

    public static final int DYNAMIC_CHAR_WIDTH = -1;
    

    private static final long UNSPECIFIED_LENGTH = -1;

    private static final int BUFFER_SIZE = 4096;

    
    private final MiniContentAccess contentAccess;
    
    private final Charset charset;
    
    private volatile long cachedLength = UNSPECIFIED_LENGTH;
    
    private final SortedMap<Long, Long> positionIndex = new TreeMap<>();
    

    public DynamicCharWidthClobValue(MiniContentAccess contentAccess, Charset charset) {
        this.contentAccess = contentAccess;
        this.charset = charset;
        this.positionIndex.put(0L, 0L);
    }

    
    @Override
    public MiniContentAccess contentAccess() {
        return contentAccess;
    }

    @Override
    public Charset charset() {
        return charset;
    }

    @Override
    public long length() {
        if (cachedLength == UNSPECIFIED_LENGTH) {
            cachedLength = calculateLength();
        }
        
        return cachedLength;
    }

    private long calculateLength() {
        long lastKnownCharPos = positionIndex.lastKey();
        long lastKnownBytePos = positionIndex.lastKey();
        long fullByteLength = contentAccess.length();
        long remainingByteLength = fullByteLength - lastKnownBytePos;
        if (remainingByteLength == 0L) {
            return lastKnownBytePos;
        }
        
        float charsPerByte = charset.newDecoder().averageCharsPerByte();
        long expectedRemainingCharLength = (long) (remainingByteLength * charsPerByte);
        
        // TODO
        return 0L;
        
    }

    @Override
    public String get(long start, int length) {
        try {
            return getThrowing(start, length);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    private String getThrowing(long start, int length) throws IOException {
        StringBuilder resultBuilder = new StringBuilder();
        Reader subReader = reader(start, length);
        char[] buffer = new char[BUFFER_SIZE];
        int readLength;
        while ((readLength = subReader.read(buffer)) != -1) {
            resultBuilder.append(buffer, 0, readLength);
        }
        return resultBuilder.toString();
    }

    @Override
    public Reader reader() {
        return new InputStreamReader(contentAccess.inputStream(), charset);
    }

    @Override
    public Reader reader(long start, long length) {
        // TODO
        return null;
        
    }

}
