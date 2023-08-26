package hu.webarticum.miniconnect.record.lob;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;

import hu.webarticum.miniconnect.api.MiniContentAccess;

public class DynamicCharWidthClobValue implements ClobValue {

    public static final int DYNAMIC_CHAR_WIDTH = -1;
    

    private static final int DEFAULT_BUFFER_SIZE = 4096;
    
    private static final long UNSPECIFIED_LENGTH = -1;

    
    private final MiniContentAccess contentAccess;
    
    private final Charset charset;
    
    private final int bufferSize;
    
    private volatile long cachedLength = UNSPECIFIED_LENGTH;
    
    private final TreeMap<Long, Long> positionIndex = new TreeMap<>();
    

    public DynamicCharWidthClobValue(MiniContentAccess contentAccess, Charset charset) {
        this(contentAccess, charset, DEFAULT_BUFFER_SIZE);
    }
    
    public DynamicCharWidthClobValue(
            MiniContentAccess contentAccess, Charset charset, int bufferSize) {
        this.contentAccess = contentAccess;
        this.charset = charset;
        this.bufferSize = bufferSize;
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
        long lastKnownBytePos = positionIndex.get(lastKnownCharPos);
        long fullByteLength = contentAccess.length();
        long remainingByteLength = fullByteLength - lastKnownBytePos;
        if (remainingByteLength == 0L) {
            return lastKnownBytePos;
        }

        try (Reader remainingReader = new InputStreamReader(
                contentAccess.inputStream(lastKnownBytePos, remainingByteLength), charset)) {
            generateRemainingIndex(remainingReader, lastKnownCharPos, lastKnownBytePos);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        
        return positionIndex.lastKey();
    }
    
    private void generateRemainingIndex(
            Reader remainingReader, long startingCharPos, long startingBytePos)
            throws IOException {
        char[] buffer = new char[bufferSize];
        long charPos = startingCharPos;
        long bytePos = startingBytePos;
        int readLength;
        while ((readLength = remainingReader.read(buffer)) != -1) {
            String chunk = new String(buffer, 0, readLength);
            charPos += chunk.length();
            bytePos += chunk.getBytes(charset).length;
            positionIndex.put(charPos, bytePos);
        }
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
        char[] buffer = new char[bufferSize];
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
        Map.Entry<Long, Long> beforeStartEntry = positionIndex.floorEntry(start);
        long afterStartBytePos = nextKnownBytePos(start);
        long startBytePos = findBytePos(
                start, beforeStartEntry.getKey(), beforeStartEntry.getValue(), afterStartBytePos);
        long end = start + length;
        Map.Entry<Long, Long> beforeEndEntry = positionIndex.floorEntry(end);
        long afterEndBytePos = nextKnownBytePos(end);
        long endBytePos = findBytePos(
                end, beforeEndEntry.getKey(), beforeEndEntry.getValue(), afterEndBytePos);
        long byteLength = endBytePos - startBytePos;
        return new InputStreamReader(contentAccess.inputStream(startBytePos, byteLength), charset);
    }
    
    private long nextKnownBytePos(long charPos) {
        Map.Entry<Long, Long> afterEntry = positionIndex.ceilingEntry(charPos);
        if (afterEntry == null) {
            return contentAccess.length();
        }
        
        return afterEntry.getValue();
    }
    
    private long findBytePos(
            long charPos, long beforeCharPos, long beforeBytePos, long afterBytePos) {
        if (beforeCharPos == charPos) {
            return beforeBytePos;
        }

        long byteLength = afterBytePos - beforeBytePos;
        try (Reader reader = new InputStreamReader(
                contentAccess.inputStream(beforeBytePos, byteLength), charset)) {
            return generateIndexTo(reader, beforeCharPos, beforeBytePos, charPos);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private long generateIndexTo(
            Reader reader, long startingCharPos, long startingBytePos, long targetCharPos)
            throws IOException {
        char[] buffer = new char[bufferSize];
        long bytePos = startingBytePos;
        long remainingCharLength = targetCharPos - startingCharPos;
        int readLength;
        while (remainingCharLength > 0) {
            int maxReadLength = remainingCharLength > bufferSize ?
                    bufferSize :
                    (int) remainingCharLength;
            readLength = reader.read(buffer, 0, maxReadLength);
            if (readLength == -1) {
                throw new IllegalArgumentException("Unexpected end of content");
            }
            String chunk = new String(buffer, 0, readLength);
            bytePos += chunk.getBytes(charset).length;
            remainingCharLength -= readLength;
        }
        positionIndex.put(targetCharPos, bytePos);
        return bytePos;
    }

}
