package hu.webarticum.miniconnect.jdbc.blob;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

public class MiniJdbcBlob implements Blob {

    @Override
    public long length() throws SQLException {
        return 0; // TODO
    }

    @Override
    public byte[] getBytes(long pos, int length) throws SQLException {
        return null; // TODO
    }

    @Override
    public InputStream getBinaryStream() throws SQLException {
        return null; // TODO
    }

    @Override
    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        return null; // TODO
    }

    @Override
    public long position(byte[] pattern, long start) throws SQLException {
        return 0; // TODO
    }

    @Override
    public long position(Blob pattern, long start) throws SQLException {
        return 0; // TODO
    }

    @Override
    public int setBytes(long pos, byte[] bytes) throws SQLException {
        return 0; // TODO
    }

    @Override
    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        return 0; // TODO
    }

    @Override
    public OutputStream setBinaryStream(long pos) throws SQLException {
        return null; // TODO
    }

    @Override
    public void truncate(long len) throws SQLException {
        // TODO
    }

    @Override
    public void free() throws SQLException {
        // TODO
    }

}
