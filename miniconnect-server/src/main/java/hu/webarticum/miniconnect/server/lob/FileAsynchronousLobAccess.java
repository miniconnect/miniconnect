package hu.webarticum.miniconnect.server.lob;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;

import hu.webarticum.miniconnect.util.data.ByteString;

public class FileAsynchronousLobAccess extends AbstractAsynchronousLobAccess {
    
    private static final long MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private static final String FILE_ACCESS_MODE = "rw";
    

    private final File file;
    
    private final RandomAccessFile randomAccessFile;

    private final Object fileAccessLock = new Object();

    private final Object closeLock = new Object();
    
    
    private volatile boolean closed = false;
    
    
    public FileAsynchronousLobAccess(long length, File file) throws IOException {
        super(length);
        this.file = file;
        this.randomAccessFile = new RandomAccessFile(file, FILE_ACCESS_MODE);
    }
    

    @Override
    public ByteString get() throws IOException {
        long length = length();
        if (length > MAX_ARRAY_SIZE) {
            throw new IllegalStateException("Content is too large to get in its entirety");
        }
        return get(0, (int) length);
    }

    @Override
    protected ByteString loadPart(long start, int length) throws IOException {
        byte[] bytes = new byte[length];
        synchronized (fileAccessLock) {
            randomAccessFile.seek(start);
            randomAccessFile.readFully(bytes);
        }
        return ByteString.wrap(bytes);
    }

    @Override
    protected void savePart(long start, ByteString part) throws IOException {
        synchronized (fileAccessLock) {
            randomAccessFile.seek(start);
            randomAccessFile.write(part.extract());
        }
    }

    @Override
    protected void checkClosed() {
        if (closed) {
            throw new IllegalArgumentException("This LOB access was already closed");
        }
    }
    
    @Override
    public void close() throws IOException {
        synchronized (closeLock) {
            if (closed) {
                return;
            }
            closed = true;
        }
        
        super.close();
        
        randomAccessFile.close();
        Files.delete(file.toPath());
    }
    
}
