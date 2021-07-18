package hu.webarticum.miniconnect.jdbc.blob;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.SQLException;

public class MiniJdbcClob implements NClob {

    @Override
    public long length() throws SQLException {
        return 0; // TODO
    }

    @Override
    public String getSubString(long pos, int length) throws SQLException {
        return null; // TODO
    }

    @Override
    public Reader getCharacterStream() throws SQLException {
        return null; // TODO
    }

    @Override
    public Reader getCharacterStream(long pos, long length) throws SQLException {
        return null; // TODO
    }

    @Override
    public InputStream getAsciiStream() throws SQLException {
        return null; // TODO
    }

    @Override
    public long position(String searchstr, long start) throws SQLException {
        return 0; // TODO
    }

    @Override
    public long position(Clob searchstr, long start) throws SQLException {
        return 0; // TODO
    }

    @Override
    public int setString(long pos, String str) throws SQLException {
        return 0; // TODO
    }

    @Override
    public int setString(long pos, String str, int offset, int len) throws SQLException {
        return 0; // TODO
    }

    @Override
    public OutputStream setAsciiStream(long pos) throws SQLException {
        return null; // TODO
    }

    @Override
    public Writer setCharacterStream(long pos) throws SQLException {
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
