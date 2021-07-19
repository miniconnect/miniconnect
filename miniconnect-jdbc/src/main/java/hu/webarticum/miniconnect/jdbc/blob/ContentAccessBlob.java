package hu.webarticum.miniconnect.jdbc.blob;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import hu.webarticum.miniconnect.api.MiniContentAccess;
import hu.webarticum.miniconnect.tool.contentaccess.ChargeableContentAccess;
import hu.webarticum.miniconnect.tool.contentaccess.FileChargeableContentAccess;
import hu.webarticum.miniconnect.tool.contentaccess.MemoryChargeableContentAccess;
import hu.webarticum.miniconnect.util.data.ByteString;

public class ContentAccessBlob implements Blob {
    
    private static final int MAX_MEMORY_CONTENT_SIZE = 10 * 1024 * 1024;
    
    
    private final MiniContentAccess contentAccess;
    
    private volatile boolean completed;
    
    
    public ContentAccessBlob(MiniContentAccess contentAccess) {
        this(contentAccess, true);
    }

    public ContentAccessBlob(long length) {
        this(createChargeableContentAccess(length), false);
    }

    private ContentAccessBlob(MiniContentAccess contentAccess, boolean completed) {
        this.contentAccess = contentAccess;
        this.completed = completed;
    }
    
    private static ChargeableContentAccess createChargeableContentAccess(long length) {
        if (length > MAX_MEMORY_CONTENT_SIZE) {
            return new FileChargeableContentAccess(length);
        } else {
            return new MemoryChargeableContentAccess((int) length);
        }
    }


    @Override
    public long length() throws SQLException {
        return contentAccess.length();
    }

    @Override
    public byte[] getBytes(long pos, int length) throws SQLException {
        if (!isAvailable(pos, length)) {
            throw new SQLException("Content part is not available");
        }
        return contentAccess.get(pos, length).extract();
    }
    
    public boolean isAvailable(long pos, long length) {
        if (completed) {
            return true;
        }
        
        ChargeableContentAccess chargeableContentAccess = (ChargeableContentAccess) contentAccess;
        return chargeableContentAccess.isAvailable(pos, length);
    }

    @Override
    public InputStream getBinaryStream() throws SQLException {
        return contentAccess.inputStream();
    }

    @Override
    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        return contentAccess.inputStream(pos, length);
    }

    @Override
    public long position(byte[] pattern, long start) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public long position(Blob pattern, long start) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public int setBytes(long pos, byte[] bytes) throws SQLException {
        return setBytes(pos, bytes, 0, bytes.length);
    }

    @Override
    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        if (completed) {
            throw new SQLException("This BLOB is already completed");
        }
        
        long zeroBasedPosition = pos - 1;
        ChargeableContentAccess chargeableContentAccess = (ChargeableContentAccess) contentAccess;
        try {
            chargeableContentAccess.accept(zeroBasedPosition, ByteString.of(bytes, offset, len));
        } catch (Exception e) {
            throw new SQLException(e);
        }
        
        return len;
    }

    @Override
    public OutputStream setBinaryStream(long pos) throws SQLException {
        if (pos != 1) {
            throw new SQLException("Start position must be 1");
        }
        
        return new MiniJdbcBlobOutputStream();
    }

    @Override
    public void truncate(long len) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void free() throws SQLException {
        contentAccess.close();
    }
    
    // FIXME: this is not thread safe
    private class MiniJdbcBlobOutputStream extends OutputStream {

        private long oneBasedPosition = 1;
        
        
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
                setBytes(oneBasedPosition, b);
            } catch (Exception e) {
                throw new IOException(e);
            }
            oneBasedPosition += len;
        }
        
    }

}
