package hu.webarticum.miniconnect.jdbc.blob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import org.apache.commons.io.input.BoundedInputStream;

import hu.webarticum.miniconnect.util.data.ByteString;

public class WriteableBlob implements Blob {

    private static final long MAX_MEMORY_SIZE = 10 * 1024L;
    
    
    private Storage storage;
    
    
    public WriteableBlob() {
        this(false);
    }

    public WriteableBlob(boolean forceFileStorage) {
        storage = forceFileStorage ? new FileStorage() : new MemoryStorage();
    }
    
    
    @Override
    public synchronized long length() throws SQLException {
        return storage.length();
    }

    @Override
    public synchronized byte[] getBytes(long pos, int length) throws SQLException {
        return storage.getBytes(pos, length);
    }

    @Override
    public synchronized InputStream getBinaryStream() throws SQLException {
        return storage.getBinaryStream();
    }

    @Override
    public synchronized InputStream getBinaryStream(long pos, long length) throws SQLException {
        return storage.getBinaryStream(pos, length);
    }

    @Override
    public synchronized long position(byte[] pattern, long start) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public synchronized long position(Blob pattern, long start) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public synchronized int setBytes(long pos, byte[] bytes) throws SQLException {
        ensureStorageForWrite(pos, bytes.length);
        return setBytes(pos, bytes, 0, bytes.length);
    }

    @Override
    public synchronized int setBytes(
            long pos, byte[] bytes, int offset, int len) throws SQLException {
        ensureStorageForWrite(pos, len);
        return storage.setBytes(pos, bytes, offset, len);
    }

    @Override
    public synchronized OutputStream setBinaryStream(long pos) throws SQLException {
        return new WriteableBlobOutputStream(pos);
    }

    @Override
    public synchronized void truncate(long len) throws SQLException {
        storage.truncate(len);
    }

    @Override
    public synchronized void free() throws SQLException {
        storage.free();
    }
    
    private void ensureStorageForWrite(long pos, int len) throws SQLException {
        if (storage instanceof FileStorage) {
            return;
        }
        
        long targetLength = pos - 1 + len;
        if (targetLength <= MAX_MEMORY_SIZE) {
            return;
        }
        
        byte[] content = storage.getBytes(1L, (int) storage.length());
        Storage newStorage = new FileStorage();
        storage.setBytes(1L, content, 0, content.length);
        storage = newStorage;
    }
    

    private class WriteableBlobOutputStream extends OutputStream {

        private long oneBasedPosition;
        
        
        public WriteableBlobOutputStream(long oneBasedPosition) {
            this.oneBasedPosition = oneBasedPosition;
        }
        
        
        @Override
        public void write(int b) throws IOException {
            write(new byte[] { (byte) b });
        }

        @Override
        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            try {
                setBytes(oneBasedPosition, b, off, len);
            } catch (Exception e) {
                throw new IOException(e);
            }
            oneBasedPosition += len;
        }
        
    }

    
    private interface Storage {
        
        public long length() throws SQLException;
        
        public byte[] getBytes(long pos, int length) throws SQLException;
        
        public InputStream getBinaryStream() throws SQLException;
        
        public InputStream getBinaryStream(long pos, long length) throws SQLException;

        public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException;

        public void truncate(long len) throws SQLException;

        public void free() throws SQLException;
        
    }
    
    
    private static class MemoryStorage implements Storage {
        
        private ByteString.Builder content = ByteString.builder();
        
        
        @Override
        public long length() throws SQLException {
            return content.length();
        }

        @Override
        public byte[] getBytes(long pos, int length) throws SQLException {
            return content.build().extractLength(Math.toIntExact(pos - 1), length);
        }

        @Override
        public InputStream getBinaryStream() throws SQLException {
            return content.build().inputStream();
        }

        @Override
        public InputStream getBinaryStream(long pos, long length) throws SQLException {
            return content.build().inputStream(Math.toIntExact(pos - 1), Math.toIntExact(length));
        }

        @Override
        public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
            long length = length();
            if (pos - 1 == length) {
                content.append(bytes, offset, len);
                return len;
            } else if (pos - 1 > length) {
                int padSize = Math.toIntExact(pos - 1 - length);
                content.append(new byte[padSize]);
                content.append(bytes, offset, len);
                return len;
            }
            
            ByteString currentByteString = content.build();
            ByteString.Builder newContent = ByteString.builder();
            newContent.append(currentByteString, 0, Math.toIntExact(pos - 1));
            newContent.append(bytes, offset, len);
            long until = pos - 1 + len;
            if (until < length) {
                ByteString tail = currentByteString.substring(
                        Math.toIntExact(until), Math.toIntExact(length));
                newContent.append(tail);
            }
            content = newContent;
            
            return len;
        }

        @Override
        public void truncate(long len) throws SQLException {
            if (len >= length()) {
                return;
            }
            
            ByteString newByteString = content.build().substringLength(0, Math.toIntExact(len));
            content = ByteString.builder();
            content.append(newByteString);
        }

        @Override
        public void free() throws SQLException {
            content = ByteString.builder();
        }
        
    }
    
    
    private static class FileStorage implements Storage {

        private static final String FILE_ACCESS_MODE = "rw";
        

        private final File file;
        
        private final RandomAccessFile randomAccessFile;
        
        
        private FileStorage() {
            this.file = createTemporaryFile();
            this.randomAccessFile = createRandomAccessFile(file);
        }


        // FIXME see: hu.webarticum.miniconnect.tool.contentaccess.FileChargeableContentAccess
        private static File createTemporaryFile() {
            try {
                return Files.createTempFile("miniconnect-jdbc-", ".blob").toFile();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        
        private static RandomAccessFile createRandomAccessFile(File file) {
            try {
                return new RandomAccessFile(file, FILE_ACCESS_MODE);
            } catch (FileNotFoundException e) {
                throw new UncheckedIOException(e);
            }
        }


        @Override
        public long length() throws SQLException {
            try {
                return randomAccessFile.length();
            } catch (Exception e) {
                throw new SQLException(e);
            }
        }

        @Override
        public byte[] getBytes(long pos, int length) throws SQLException {
            byte[] result = new byte[length];
            try {
                randomAccessFile.seek(pos - 1);
                randomAccessFile.readFully(result);
            } catch (Exception e) {
                throw new SQLException(e);
            }
            return result;
        }

        @Override
        public InputStream getBinaryStream(long pos, long length) throws SQLException {
            long end = pos - 1 + length;
            long fullLength = length();
            if (end > fullLength) {
                throw new SQLException(String.format(
                        "Out of bounds, requested end: %d, but length is: %d", end, fullLength));
            }
            
            try {
                randomAccessFile.seek(pos - 1);
                InputStream innerStream =
                        Channels.newInputStream(randomAccessFile.getChannel()); // NOSONAR
                return new BoundedInputStream(innerStream, length);
            } catch (IOException e) {
                throw new SQLException(e);
            }
        }

        @Override
        public InputStream getBinaryStream() throws SQLException {
            try {
                randomAccessFile.seek(0L);
                return Channels.newInputStream(randomAccessFile.getChannel());
            } catch (Exception e) {
                throw new SQLException(e);
            }
        }

        @Override
        public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
            try {
                randomAccessFile.seek(pos - 1);
                randomAccessFile.write(bytes, offset, len);
            } catch (Exception e) {
                throw new SQLException(e);
            }
            return len;
        }

        @Override
        public void truncate(long len) throws SQLException {
            if (len >= length()) {
                return;
            }
            
            try {
                randomAccessFile.setLength(len);
            } catch (Exception e) {
                throw new SQLException(e);
            }
        }

        @Override
        public void free() throws SQLException {
            try {
                randomAccessFile.close();
                Files.delete(file.toPath());
            } catch (Exception e) {
                throw new SQLException(e);
            }
        }
        
    }

}
