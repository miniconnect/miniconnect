package hu.webarticum.miniconnect.messenger.contentaccess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.file.Files;

import hu.webarticum.miniconnect.util.data.ByteString;

public class FileChargeableContentAccess extends AbstractChargeableContentAccess {
    
    private static final long MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8L;

    private static final String FILE_ACCESS_MODE = "rw";
    

    private final File file;
    
    private final RandomAccessFile randomAccessFile;

    private final Object fileAccessLock = new Object();

    private final Object closeLock = new Object();
    
    
    private volatile boolean closed = false;
    

    public FileChargeableContentAccess(long length) {
        this(length, createTemporaryFile());
    }
    
    public FileChargeableContentAccess(long length, File file) {
        super(length);
        this.file = file;
        this.randomAccessFile = createRandomAccessfile(file);
    }
    
    // TODO: improve this
    // - check SystemUtils.IS_OS_UNIX
    // - use a better directory if possible, e. g. $XDG_CACHE_HOME
    // - check existing solutions in jdbc implementations
    private static File createTemporaryFile() {
        try {
            return Files.createTempFile("miniconnect-", ".blob").toFile();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    private static RandomAccessFile createRandomAccessfile(File file) {
        try {
            return new RandomAccessFile(file, FILE_ACCESS_MODE);
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }
    

    @Override
    public boolean isLarge() {
        return (length() > MAX_ARRAY_SIZE);
    }

    @Override
    public boolean isTemporary() {
        return true;
    }

    @Override
    public ByteString get() {
        if (isLarge()) {
            throw new IllegalStateException("Content is too large to get in its entirety");
        }
        return get(0, (int) length());
    }

    @Override
    protected ByteString loadPart(long start, int length) {
        byte[] bytes;
        synchronized (fileAccessLock) {
            try {
                bytes = loadPartUnsafe(start, length);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return ByteString.wrap(bytes);
    }

    private byte[] loadPartUnsafe(long start, int length) throws IOException {
        byte[] bytes = new byte[length];
        randomAccessFile.seek(start);
        randomAccessFile.readFully(bytes);
        return bytes;
    }
    
    @Override
    protected void savePart(long start, ByteString part) {
        synchronized (fileAccessLock) {
            try {
                savePartUnsafe(start, part);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
    
    private void savePartUnsafe(long start, ByteString part) throws IOException {
        randomAccessFile.seek(start);
        randomAccessFile.write(part.extract());
    }

    @Override
    protected void checkClosed() {
        if (closed) {
            throw new IllegalArgumentException("This LOB access was already closed");
        }
    }
    
    @Override
    public void close() {
        synchronized (closeLock) {
            if (closed) {
                return;
            }
            closed = true;
        }
        
        super.close();
        
        try {
            randomAccessFile.close();
            Files.delete(file.toPath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
}
