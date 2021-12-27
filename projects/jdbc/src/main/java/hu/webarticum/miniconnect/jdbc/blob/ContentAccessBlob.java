package hu.webarticum.miniconnect.jdbc.blob;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

import hu.webarticum.miniconnect.api.MiniContentAccess;

public class ContentAccessBlob implements Blob {
    
    private final MiniContentAccess contentAccess;
    
    
    public ContentAccessBlob(MiniContentAccess contentAccess) {
        this.contentAccess = contentAccess;
    }
    

    @Override
    public long length() throws SQLException {
        return contentAccess.length();
    }

    @Override
    public byte[] getBytes(long pos, int length) throws SQLException {
        return contentAccess.get(pos - 1, length).extract();
    }
    
    @Override
    public InputStream getBinaryStream() throws SQLException {
        return contentAccess.inputStream();
    }

    @Override
    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        return contentAccess.inputStream(pos - 1, length);
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
        throw createReadOnlyException();
    }

    @Override
    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public OutputStream setBinaryStream(long pos) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void truncate(long len) throws SQLException {
        throw createReadOnlyException();
    }

    @Override
    public void free() throws SQLException {
        contentAccess.close();
    }
    
    
    private SQLException createReadOnlyException() {
        return new SQLException("This BLOB is read-only");
    }
    
}
